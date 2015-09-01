package org.spideruci.tacoco;

import static org.spideruci.tacoco.cli.CliAble.LOG;
import static org.spideruci.tacoco.cli.CliAble.PM;
import static org.spideruci.tacoco.cli.CliAble.AnalyzerCli.readOptionalArgumentValue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.junit.experimental.ParallelComputer;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public final class TacocoRunner {
	public static void main(String[] args) {

		boolean log=false;
		if(readOptionalArgumentValue(LOG,"off").equals("on")) log=true;	
		
		
		System.out.println("Starting Tacoco Runner");
		
		JUnitCore core = new JUnitCore();
		
		//add Tacoco Listener
		TacocoListener listener = new TacocoListener();
		core.addListener(listener);
		
		ArrayList<String> klasses = getClasses(args[0]);
		Class<?>[] cls = new Class<?>[klasses.size()];
		int i=0;
		for(String testClass : klasses){
			try {
				cls[i++] = Class.forName(testClass);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		String pm = readOptionalArgumentValue(PM,"none");
		boolean classes=false, methods=false;
		if(pm.equals("classes")) classes = true;
		else if(pm.equals("methods")) methods = true;
		else if(pm.equals("both")) {
			classes = true;
			methods = true;
		}
		
		Result result = core.run(new ParallelComputer(classes,methods),cls); //class,method
		List<Failure> failures = result.getFailures(); 
		
		/* rerun without parallelism if it fails
		if(!failures.isEmpty()){
			if(classes || methods) result = core.run(cls);
		}
		*/
		
        System.out.println("-------------------------------------------------");
		System.out.println("Tacoco Execution Time: "+result.getRunTime()/1000.0 +"sec");
		System.out.println("Run Counts:" + result.getRunCount());
		System.out.println("Failure Counts:" + result.getFailureCount());
		System.out.println("Ignore Counts:" + result.getIgnoreCount());
		System.out.println("Parallel Mode:" + pm);
		System.out.println("-------------------------------------------------");
		
		if(log) System.out.println(failures);
		
	}

	public static ArrayList<String> getClasses(final String p){		
		final ArrayList<String> ret = new ArrayList<String>();
		
		try {
			
			//parse pom.xml file to decide which test case is executed
			MavenXpp3Reader reader = new MavenXpp3Reader();
			Model model = reader.read(new FileInputStream(new File("pom.xml")));
			//System.out.println(model.getProperties());
			
			
			Files.walkFileTree(Paths.get(p), new SimpleFileVisitor<Path>() {
			    @Override
			    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			        String str = file.toString();
			        if(str.endsWith(".class") && !str.matches("(.*)\\$(.*)")) {
			        	//System.out.println(str.replaceAll(p.endsWith("/")?p:p+"/","").replace('/','.').replaceAll("\\.class","")); 
			        	ret.add(str.replaceAll(p.endsWith("/")?p:p+"/","").replace('/','.').replaceAll("\\.class",""));
			        }
			        return FileVisitResult.CONTINUE;
			    }
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}			
}
