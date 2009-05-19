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
import java.net.MalformedURLException;
import java.net.URL;
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
