package com.simplicity.maged.mccobjectdetection.components.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.util.Log;

public class SimplicityLogger {

	static StringBuilder logStringBuilder;

	public static void initLog() {
		if (logStringBuilder == null) {
			logStringBuilder = new StringBuilder();
		} else {
			logStringBuilder = new StringBuilder();
		}
	}

	public static void appendLine(String log) {
		if (logStringBuilder == null) {
			logStringBuilder = new StringBuilder();
		}
		/*if (logStringBuilder.length() > 0) {
			logStringBuilder.append("\r\n");
		}*/
		logStringBuilder.append(log);
	}

	public static void writeLog(String logFilePath) {
		(new File(logFilePath.substring(0, logFilePath.lastIndexOf("/"))))
				.mkdir();
		File logFile = new File(logFilePath);
		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (Exception e) {
				Log.e("simplicity", "SimplicityLogger: " + e.toString());
			}
		}
		try {
			// BufferedWriter for performance, true to set append to file flag
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile,
					true));
			buf.append(logStringBuilder.toString());
			buf.newLine();
			buf.close();
		} catch (Exception e) {
			Log.e("simplicity", "SimplicityLogger: " + e.toString());
		}
	}
}
