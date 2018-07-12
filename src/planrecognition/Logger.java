package planrecognition;


import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;



public class Logger {


	public static boolean LOGIT_ON = true;
	
	
	
	public static void logit(String logstring)
	{

		if(Logger.LOGIT_ON==true)
		{
			try
			{
				
				File file = new File("logfile"+".log"); //filepath is being passes through //ioc         //and filename through a method 

				
				if (file.exists()) 
				{
					file.delete(); //you might want to check if delete was successfull
				}
				
				
				
				PrintWriter pw = new PrintWriter(new FileOutputStream(file,true));
				pw.append(logstring);
				pw.append(" ");
				//pw.append("\n");
				pw.close();

			}
			catch(Exception e)
			{

			}
		}
	}

}
