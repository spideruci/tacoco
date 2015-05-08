import org.junit.runner.JUnitCore;


public class TacocoRunner extends JUnitCore
{
	public static void main(String[] args)
	{
		int sleepLength = 2000;
		System.out.println("Testing Thread.sleep()");
		System.out.println("Going to sleep for "+sleepLength+" ms.");
		try
		{
			Thread.sleep(sleepLength);
		}
		catch(InterruptedException e)
		{
			System.out.println(e.getMessage());
		}
		System.out.println("Done sleeping.");
		JUnitCore core = new TacocoRunner();
		core.addListener(new TacocoListener());
		try
		{
			core.run(Class.forName(args[0]));
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}
}
