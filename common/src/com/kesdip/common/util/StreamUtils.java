/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 1, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.zip.CRC32;

import org.apache.log4j.Logger;

import com.kesdip.common.exception.GenericSystemException;

/**
 * A class with methods to work with streams.
 * 
 * @author gerogias
 */
public class StreamUtils {

	/**
	 * The logger.
	 */
	private static final Logger logger = Logger.getLogger(StreamUtils.class
			.getName());

	/**
	 * Reads a stream into a String. The stream is closed afterwards.
	 * 
	 * @param in
	 *            the input stream
	 * @return String the contents of the stream, never <code>null</code>
	 * @throws IOException
	 *             if an error occurs
	 */
	public static final String readString(InputStream in) throws IOException {

		BufferedReader reader = null;
		StringBuffer buffer = new StringBuffer();
		if (logger.isDebugEnabled()) {
			logger.debug("Reading from stream " + in);
		}
		try {
			reader = new BufferedReader(new InputStreamReader(in));

			String temp = null;
			while ((temp = reader.readLine()) != null) {
				buffer.append(temp).append("\n");
			}
		} catch (IOException ioe) {
			logger.error("Error reading string", ioe);
		} finally {
			try {
				reader.close();
			} catch (Exception e) {
				// do nothing
			}
		}
		return buffer.toString();
	}

	/**
	 * Load a local resource as a string.
	 * 
	 * @param resourceUrl
	 *            the resource URL
	 * @return String its cntext
	 * @throws IOException
	 *             on read errors
	 */
	public static final String readResource(URL resourceUrl) throws IOException {
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = null;
		if (logger.isDebugEnabled()) {
			logger.debug("Reading from resource " + resourceUrl);
		}
		try {
			// replace spaces in path
			reader = new BufferedReader(new FileReader(resourceUrl.getFile()
					.replace("%20", " ")));
			String temp = null;
			while ((temp = reader.readLine()) != null) {
				builder.append(temp).append("\n");
			}
		} finally {
			try {
				reader.close();
			} catch (Exception e) {
				// do nothing
			}
		}
		return builder.toString();
	}

	/**
	 * Streams the contents of the file to the stream. The stream is not closed
	 * after writing.
	 * 
	 * @param file
	 *            the file
	 * @param out
	 *            the output
	 * @throws IllegalArgumentException
	 *             if the arguments are <code>null</code> or invalid
	 * @throws IOException
	 *             on error
	 */
	public static final void streamFile(File file, OutputStream out)
			throws IOException, IllegalArgumentException {
		if (file == null || out == null) {
			logger.error("Arguments are null");
			throw new IllegalArgumentException("Arguments are null");
		}
		if (!file.isFile()) {
			logger.error(file + " is not a valid file");
			throw new IllegalArgumentException(file + " is not a valid file");
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Streaming file " + file.getAbsolutePath());
		}
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			copyStream(fis, out);
		} catch (IOException ioe) {
			logger.error("Error streaming file", ioe);
			throw ioe;
		} catch (Exception ex) {
			logger.error("Error streaming file", ex);
			throw new IOException(ex);
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
				// do nothing
			}
		}
	}

	/**
	 * Copies the contents of in to out, which updating the crc checksum
	 * parameter. The streams are not closed.
	 * 
	 * @param in
	 *            input
	 * @param out
	 *            output
	 * @param crc
	 *            the CRC checksum to update during the stream copy; ignored if
	 *            <code>null</code>
	 * @throws IOException
	 *             on error
	 * @throws IllegalArgumentException
	 *             if the arguments are <code>null</code>
	 */
	public static final void copyStream(InputStream in, OutputStream out,
			CRC32 crc) throws IOException, IllegalArgumentException {
		if (in == null || out == null) {
			logger.error("Arguments cannot be null");
			throw new IllegalArgumentException("Arguments cannot be null");
		}
		logger.debug("Copying between streams");
		try {
			byte[] buffer = new byte[2048];
			int readCount = 0;
			while ((readCount = in.read(buffer)) != -1) {
				out.write(buffer, 0, readCount);
				if (crc != null)
					crc.update(buffer, 0, readCount);
			}
		} catch (IOException ioe) {
			logger.error("Error copying streams", ioe);
			throw ioe;
		} catch (Exception ex) {
			logger.error("Error copying streams", ex);
			throw new IOException(ex.getMessage());
		}
	}

	/**
	 * Copies the contents of in to out. The streams are not closed.
	 * 
	 * @param in
	 *            input
	 * @param out
	 *            output
	 * @throws IOException
	 *             on error
	 * @throws IllegalArgumentException
	 *             if the arguments are <code>null</code>
	 */
	public static final void copyStream(InputStream in, OutputStream out)
			throws IOException, IllegalArgumentException {
		copyStream(in, out, null);
	}

	/**
	 * Copies the contents from the source file to the destination file.
	 * 
	 * @param source
	 *            source file
	 * @param dest
	 *            destination file
	 * @throws IOException
	 *             on error
	 * @throws IllegalArgumentException
	 *             if the arguments are <code>null</code> or the source is not
	 *             a file
	 */
	public static final void copyFile(File source, File dest)
			throws GenericSystemException, IllegalArgumentException {
		if (source == null || dest == null) {
			logger.error("Files cannot be null");
			throw new IllegalArgumentException("Files cannot be null");
		}
		if (!source.isFile()) {
			logger.error("Source is not a valid file");
			throw new IllegalArgumentException("Source is not a valid file");
		}
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(dest);
			streamFile(source, out);
		} catch (Exception e) {
			logger.error("Error copying file", e);
			throw new GenericSystemException("Error copying file", e);
		} finally {
			try {
				out.close();
			} catch (Exception e) {
				// do nothing
			}
		}
	}

	/**
	 * Copies the stream to the destination file. The stream is not closed.
	 * 
	 * @param inputStream
	 *            the stream
	 * @param dest
	 *            the file
	 * @throws IllegalArgumentException
	 *             if the arguments are null
	 * @throws GenericSystemException
	 *             on error
	 */
	public final static void copyToFile(InputStream inputStream, File dest)
			throws IllegalArgumentException, GenericSystemException {
		copyToFile(inputStream, dest, null);
	}

	/**
	 * Copies the stream to the destination file. The stream is not closed.
	 * During copying it updates the given CRC.
	 * 
	 * @param inputStream
	 *            the stream
	 * @param dest
	 *            the file
	 * @param crc
	 *            the CRC to update; ignored if <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the arguments are null
	 * @throws GenericSystemException
	 *             on error
	 */
	public final static void copyToFile(InputStream inputStream, File dest,
			CRC32 crc) throws IllegalArgumentException, GenericSystemException {

		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(dest);
			copyStream(inputStream, outputStream, crc);
		} catch (Exception e) {
			logger.error("Error copying stream", e);
			throw new GenericSystemException("Error copying stream", e);
		} finally {
			try {
				outputStream.close();
			} catch (Exception e) {
				// do nothing
			}
		}
	}

	/**
	 * Get the CRC from an input stream. The stream's pointer is assumed to be
	 * at the first byte. The stream is read fully and is not closed.
	 * 
	 * @param input
	 *            the stream
	 * @return CRC32 the calculated CRC, never <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the stream is <code>null</code>
	 * @throws GenericSystemException
	 *             on error
	 */
	public static final CRC32 getCrc(InputStream in)
			throws GenericSystemException, IllegalArgumentException {

		if (in == null) {
			logger.error("Stream cannot be null");
			throw new IllegalArgumentException("Stream cannot be null");
		}

		CRC32 crc = new CRC32();
		try {
			byte[] buffer = new byte[2048];
			int readCount = 0;
			while ((readCount = in.read(buffer)) != -1) {
				crc.update(buffer, 0, readCount);
			}
		} catch (Exception ex) {
			logger.error("Error calculating CRC", ex);
			throw new GenericSystemException("Error calculating CRC", ex);
		}
		return crc;
	}
}
