package org.spideruci.tacoco;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

import org.junit.runner.JUnitCore;


public final class TacocoRunner {
	public static void main(String[] args) {
/*		
		try {
			addPath(args[0]);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
*/
		JUnitCore core = new JUnitCore();
		core.addListener(new TacocoListener());

		for(String testClass : getClasses(args[0])){
			try {
				core.run(Class.forName(testClass));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		//System.out.println(System.getProperty("java.class.path"));
	}
/*
	public static void addPath(String s) throws Exception {
	    File f = new File(s);
	    URI u = f.toURI();
	    URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
	    Class<URLClassLoader> urlClass = URLClassLoader.class;
	    Method method = urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
	    method.setAccessible(true);
	    method.invoke(urlClassLoader, new Object[]{u.toURL()});
	}
*/
	public static ArrayList<String> getClasses(final String p){		
		final ArrayList<String> ret = new ArrayList<String>();
		
		try {
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
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}			
}
