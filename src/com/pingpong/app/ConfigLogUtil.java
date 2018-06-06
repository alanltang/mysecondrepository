package	com.pingpong.app;

import java.io.*;
import java.util.Date;
import java.text.SimpleDateFormat;

public class ConfigLogUtil {

  /**
	 *  return current system time
	 */
	private static String getCurrentTime() {
		SimpleDateFormat formater = new SimpleDateFormat("MM-dd-yy HH:mm:ss");
		Date now = new Date();
		String ts = formater.format(now);
		return ts;
	}

	public static void log(String logFileName, String msg) {
		try {
      String currentTime = getCurrentTime();
      StringBuffer sb = new StringBuffer();
      sb.append(currentTime);
      sb.append(" ");
      if (msg != null)
        sb.append(msg);		

      try {
        PrintWriter pwLogFile = new PrintWriter(new FileWriter(logFileName, true));
        pwLogFile.println(sb.toString());
        pwLogFile.close();
      }catch(Exception e) {
        System.err.println("fail to write log file: " + logFileName );
        e.printStackTrace();
      }
		} catch (Exception e)
		{
			System.err.println("Fail to write log file.");
			e.printStackTrace();
		}
	}
}
