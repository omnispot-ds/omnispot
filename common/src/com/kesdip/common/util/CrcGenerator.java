/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Jan 19, 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.common.util;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Utility command-line class to generate a CRC from a file. The file/folder
 * name is passed as an argument. The outcome is a "crc.txt" file in the current
 * directory with the filenames and the CRCs in each line. In the case of a
 * directory in the command line, the logic iterates all files in it (not
 * recursively).
 * 
 * @author gerogias
 */
public class CrcGenerator {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		if (args.length == 0) {
			System.err.println(getUsage());
			return;
		}
		File file = new File(args[0]);
		if (!file.isFile() && !file.isDirectory()) {
			System.err.println(getUsage());
			return;
		}
		processFile(file);
	}

	/**
	 * @return String the usage string
	 */
	private static final String getUsage() {
		return "Usage: " + CrcGenerator.class.getName() + " fileName\n"
				+ "\tfileName: A valid file or directory";
	}

	/**
	 * Process a file or folder and store the results in a text file.
	 * 
	 * @param file
	 *            the file/folder to process
	 */
	private static void processFile(File file) {
		StringBuilder builder = new StringBuilder();
		File[] files = null;
		if (file.isDirectory()) {
			files = FileUtils.getFolderContents(file);
		} else {
			files = new File[] { file };
		}
		for (File temp : files) {
			System.out.println("Processing " + temp.getAbsolutePath() + "...");
			builder.append(temp.getAbsolutePath()).append(": ").append(
					FileUtils.getCrc(temp).getValue()).append("\r\n");
		}
		try {
			FileOutputStream out = new FileOutputStream("crc.txt");
			out.write(builder.toString().getBytes());
			out.close();
			System.out.println("Done!");
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
}
