/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Jan 14, 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.common.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.Random;
import java.util.zip.CRC32;

import org.apache.log4j.Logger;

import com.kesdip.common.exception.GenericSystemException;

/**
 * File-related utility methods.
 * 
 * @author gerogias
 */
public class FileUtils {

	/**
	 * The logger.
	 */
	private final static Logger logger = Logger.getLogger(FileUtils.class);

	/**
	 * Temp directory for the JVM.
	 */
	private final static String TMP_DIR = System.getProperty("java.io.tmpdir");

	/**
	 * Utility method to create a folder structure,if it does not exist.
	 * 
	 * @param name
	 *            the folder name
	 * @return File the file object
	 * @throws IllegalArgumentException
	 *             if the name is <code>null</code>/empty
	 * @throws GenericSystemException
	 *             if the folder could not be created
	 */
	public static File getFolder(String name) throws GenericSystemException {
		if (StringUtils.isEmpty(name)) {
			logger.error("Argument cannot be null/empty");
			throw new IllegalArgumentException("Argument cannot be null/empty");
		}
		File rootFolder = new File(name);
		if (!rootFolder.exists() && !rootFolder.mkdirs()) {
			logger.error(rootFolder + " could not be created");
			throw new GenericSystemException(rootFolder
					+ " could not be created");
		}
		if (logger.isTraceEnabled()) {
			logger.trace("Folder is " + rootFolder);
		}
		return rootFolder;
	}

	/**
	 * Returns the files inside the given folder. It only returns the immediate
	 * children of the folder.
	 * 
	 * @param folder
	 *            the folder
	 * @return File[] an array of files, never <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the argument is <code>null</code> or not a folder
	 */
	public static File[] getFolderContents(File folder)
			throws IllegalArgumentException {
		if (folder == null || !folder.isDirectory()) {
			logger.error("Argument is not a valid folder");
			throw new IllegalArgumentException("Argument is not a valid folder");
		}
		return folder.listFiles(new FileOnlyFilter());
	}

	/**
	 * Returns the name from a full path.
	 * 
	 * @param path
	 * @return String the file name or <code>null</code>
	 */
	public static String getName(String path) {
		if (path == null) {
			return null;
		}
		String[] parts = path.split("\\\\|\\/");
		if (logger.isTraceEnabled()) {
			logger.trace("Name is " + parts[parts.length - 1]);
		}
		return parts[parts.length - 1];
	}

	/**
	 * Returns the extension from a file.
	 * 
	 * @param file
	 *            the file
	 * @return String the extension without the dot or an empty string
	 * @throws IllegalArgumentException
	 *             if the argument is <code>null</code> or does not represent a
	 *             file
	 */
	public static String getSuffix(File file) throws IllegalArgumentException {
		if (file == null) {
			logger.error("Argument is null");
			throw new IllegalArgumentException("Argument is null");
		}
		if (!file.isFile()) {
			logger.error("Argument is not a valid file");
			throw new IllegalArgumentException("Argument is not a valid file");
		}
		return getSuffix(file.getAbsolutePath());
	}

	/**
	 * Returns the extension from a file name.
	 * 
	 * @param filename
	 *            the filename
	 * @return String the extension without the dot or an empty string
	 */
	public static String getSuffix(String filename)
			throws IllegalArgumentException {
		if (StringUtils.isEmpty(filename)) {
			return "";
		}
		int dotIndex = filename.lastIndexOf('.');
		if (dotIndex == -1) {
			return "";
		}
		return filename.substring(dotIndex + 1);
	}

	/**
	 * Creates a unique filename from the given file object.
	 * <p>
	 * It extracts the name of the file, if there is path information and
	 * appends it a random suffix, without altering the file type suffix.
	 * </p>
	 * 
	 * @param file
	 *            the file
	 * @return String the unique file name
	 * @throws IllegalArgumentException
	 *             if the argument is null
	 */
	public static String getUniqueFileName(File file)
			throws IllegalArgumentException {

		if (file == null) {
			logger.error("File is null");
			throw new IllegalArgumentException("File is null");
		}

		return getUniqueFileName(file.getAbsolutePath());
	}

	/**
	 * Creates a unique filename from the given file path.
	 * <p>
	 * It extracts the name from the path, if there is parent folder information
	 * and appends it a random suffix, without altering the file type suffix.
	 * </p>
	 * 
	 * @param file
	 *            the file
	 * @return String the unique file name
	 * @throws IllegalArgumentException
	 *             if the argument is null
	 */
	public static String getUniqueFileName(String fullPath)
			throws IllegalArgumentException {

		if (StringUtils.isEmpty(fullPath)) {
			logger.error("Path is null");
			throw new IllegalArgumentException("Path is null");
		}

		String name = getName(fullPath);
		Random random = new Random(System.currentTimeMillis());
		String suffix = "_" + System.currentTimeMillis() + "_"
				+ random.nextInt(10000);
		int dotIndex = name.lastIndexOf('.');
		if (dotIndex == -1) {
			return name + suffix;
		} else {
			return name.substring(0, dotIndex) + suffix
					+ name.substring(dotIndex);
		}
	}

	/**
	 * Returns a unique file inside <code>java.io.tmpdir</code> with "tmp" as
	 * prefix.
	 * 
	 * @param deleteOnExit
	 *            if the file should be deleted on JVM's exit
	 * @return File the created file
	 * @throws IOException
	 *             if the file could not be created
	 * @throws IllegalArgumentException
	 *             if the name is <code>null</code>/empty
	 */
	public static File createUniqueFile(boolean deleteOnExit)
			throws IOException, IllegalArgumentException {
		return createUniqueFile("tmp", deleteOnExit);
	}

	/**
	 * Returns a unique file inside <code>java.io.tmpdir</code> with the given
	 * name as prefix.
	 * 
	 * @param name
	 *            the name of the file, w/o any path info
	 * @param deleteOnExit
	 *            if the file should be deleted on JVM's exit
	 * @return File the created file
	 * @throws IOException
	 *             if the file could not be created
	 * @throws IllegalArgumentException
	 *             if the name is <code>null</code>/empty
	 */
	public static File createUniqueFile(String name, boolean deleteOnExit)
			throws IOException, IllegalArgumentException {
		if (StringUtils.isEmpty(name)) {
			logger.error("Name is null");
			throw new IllegalArgumentException("Name is null");
		}
		String unique = getUniqueFileName(name);
		File uniqueFile = new File(TMP_DIR, unique);
		uniqueFile.createNewFile();
		if (deleteOnExit) {
			uniqueFile.deleteOnExit();
		}
		return uniqueFile;
	}

	/**
	 * Calculates the CRC of a file.
	 * 
	 * @param file
	 *            the file
	 * @return CRC32 the result
	 * @throws GenericSystemException
	 *             on error
	 * @throws IllegalArgumentException
	 *             if the file is <code>null</code> or not a file
	 */
	public static final CRC32 getCrc(File file) throws GenericSystemException,
			IllegalArgumentException {
		if (file == null) {
			logger.error("File is null");
			throw new IllegalArgumentException("File is null");
		}

		if (!file.isFile()) {
			logger.error("File " + file.getAbsolutePath() + " is not valid");
			throw new IllegalArgumentException("File " + file.getAbsolutePath()
					+ " is not valid");
		}
		CRC32 crc = null;
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			crc = StreamUtils.getCrc(in);
		} catch (Exception e) {
			logger.error("Error calculating CRC32 for file "
					+ file.getAbsolutePath(), e);
			throw new GenericSystemException(
					"Error calculating CRC32 for file "
							+ file.getAbsolutePath(), e);
		} finally {
			StreamUtils.close(in);
		}
		return crc;
	}

	/**
	 * Converts a file to a {@link URL} object.
	 * 
	 * @param file
	 *            the file
	 * @return URL the url or <code>null</code>
	 */
	public static URL toUrl(File file) {
		if (file == null) {
			return null;
		}
		String path = file.getAbsolutePath();
		String url = "file://" + (path.startsWith("/") ? "" : '/')
				+ file.getAbsolutePath();
		try {
			return new URL(url);
		} catch (MalformedURLException mue) {
			return null;
		}
	}

	/**
	 * Returns the size of the file in bytes.
	 * 
	 * @param file
	 *            the file
	 * @return int the size in bytes
	 * @throws IllegalArgumentException
	 *             if the file is <code>null</code> or invalid
	 * @throws GenericSystemException
	 *             on error
	 */
	public static int getSize(File file) throws IllegalArgumentException,
			GenericSystemException {
		if (file == null || !file.isFile()) {
			logger.error("File is not valid");
			throw new IllegalArgumentException("File is not valid");
		}
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			return fis.available();
		} catch (IOException ioe) {
			logger.error("Error calculating size", ioe);
			throw new GenericSystemException("Error calculating size", ioe);
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
				// do nothing
			}
		}
	}

	/**
	 * Wrapper around {@link File#getAbsolutePath()}.
	 * 
	 * @param path
	 *            the path to transform
	 * @return String the path in it's native form or an empty string
	 */
	public static String getNativePathName(String path) {
		if (StringUtils.isEmpty(path)) {
			return "";
		}
		File file = new File(path);
		return file.getAbsolutePath();
	}

	/**
	 * Simple file copy method utilizing nio.
	 * 
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	public static void copyFile(File in, File out) throws IOException {
		FileChannel inChannel = new FileInputStream(in).getChannel();
		FileChannel outChannel = new FileOutputStream(out).getChannel();
		try {
			// magic number for Windows, 64Mb - 32Kb)
			int maxCount = (64 * 1024 * 1024) - (32 * 1024);
			long size = inChannel.size();
			long position = 0;
			while (position < size)
				position += inChannel
						.transferTo(position, maxCount, outChannel);
		} catch (IOException e) {
			throw e;
		} finally {
			if (inChannel != null)
				inChannel.close();
			if (outChannel != null)
				outChannel.close();
		}
	}

	/**
	 * Implementation of the "tail -n" functionality. Based on code found <a
	 * href="http://crawler.archive.org/xref-test/org/archive/crawler/util/LogReader.html#762"
	 * >here</a>
	 * 
	 * @param file
	 *            the file to read from. It will be opened in read mode
	 * @param lines
	 *            the number of lines to read
	 * @return String the actual text read
	 * @throws IOException
	 *             on error
	 */
	public static String tail(File file, int lines) throws IOException {
		int BUFFERSIZE = 2048;
		long pos = 0;
		long endPos = 0;
		long lastPos = 0;
		int numOfLines = 0;
		byte[] buffer = new byte[BUFFERSIZE];
		StringBuilder sb = new StringBuilder();
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(file, "r");
			endPos = raf.length();
			lastPos = endPos;

			// Check for non-empty file
			// Check for newline at EOF
			if (endPos > 0) {
				byte[] oneByte = new byte[1];
				raf.seek(endPos - 1);
				raf.read(oneByte);
				if ((char) oneByte[0] != '\n') {
					numOfLines++;
				}
			}

			do {
				// seek back BUFFERSIZE bytes
				// if length of the file if less then BUFFERSIZE start from BOF
				pos = 0;
				if ((lastPos - BUFFERSIZE) > 0) {
					pos = lastPos - BUFFERSIZE;
				}
				raf.seek(pos);
				// If less then BUFFERSIZE available read the remaining bytes
				if ((lastPos - pos) < BUFFERSIZE) {
					int remainer = (int) (lastPos - pos);
					buffer = new byte[remainer];
				}
				raf.readFully(buffer);
				// in the buffer seek back for newlines
				for (int i = buffer.length - 1; i >= 0; i--) {
					if ((char) buffer[i] == '\n') {
						numOfLines++;
						// break if we have last n lines
						if (numOfLines > lines) {
							pos += (i + 1);
							break;
						}
					}
				}
				// reset last position
				lastPos = pos;
			} while ((numOfLines <= lines) && (pos != 0));

			// print last n lines starting from last position
			for (pos = lastPos; pos < endPos; pos += buffer.length) {
				raf.seek(pos);
				if ((endPos - pos) < BUFFERSIZE) {
					int remainer = (int) (endPos - pos);
					buffer = new byte[remainer];
				}
				raf.readFully(buffer);
				sb.append(new String(buffer));
			}
		} catch (FileNotFoundException e) {
			sb = null;
		} catch (IOException e) {
			logger.error("Error reading tail from file", e);
		} finally {
			try {
				raf.close();
			} catch (Exception e) {
				// do nothing
			}
		}
		return sb.toString();
	}

	/**
	 * A filter which accepts only files.
	 * 
	 * @author gerogias
	 */
	private static class FileOnlyFilter implements FileFilter {

		/**
		 * Accepts only files.
		 * 
		 * @see java.io.FileFilter#accept(java.io.File)
		 */
		public boolean accept(File pathname) {
			return pathname.isFile();
		}

	}

}
