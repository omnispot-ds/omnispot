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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
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
	 * Reads a stream into a String. The stream is closed afterwards. Uses the
	 * platform's default encoding.
	 * 
	 * @param in
	 *            the input stream
	 * @return String the contents of the stream, never <code>null</code>
	 * @throws IOException
	 *             if an error occurs
	 */
	public static final String readString(InputStream in) throws IOException {

		return readString(in, getDefaultEncoding(), true);
	}

	/**
	 * Reads a stream into a String. The stream is closed afterwards.
	 * 
	 * @param in
	 *            the input stream
	 * @param encoding
	 *            the encoding to use
	 * @return String the contents of the stream, never <code>null</code>
	 * @throws IOException
	 *             if an error occurs
	 */
	public static final String readString(InputStream in, String encoding)
			throws IOException {

		return readString(in, encoding, true);
	}

	/**
	 * Reads a stream into a String.
	 * 
	 * @param in
	 *            the input stream
	 * @param encoding
	 *            the encoding to use
	 * @param closeStream
	 *            close the stream if <code>true</code>
	 * @return String the contents of the stream, never <code>null</code>
	 * @throws IOException
	 *             if an error occurs
	 */
	public static final String readString(InputStream in, String encoding,
			boolean closeStream) throws IOException {

		BufferedReader reader = null;
		StringBuffer buffer = new StringBuffer();
		if (logger.isTraceEnabled()) {
			logger.trace("Reading from stream " + in);
		}
		try {
			reader = new BufferedReader(new InputStreamReader(in, encoding));

			String temp = null;
			while ((temp = reader.readLine()) != null) {
				buffer.append(temp).append("\n");
			}
		} catch (IOException ioe) {
			logger.error("Error reading string", ioe);
		} finally {
			if (closeStream) {
				close(reader);
			}
		}
		return buffer.toString();
	}

	/**
	 * Load a local resource as a string.
	 * 
	 * @param resourceUrl
	 *            the resource URL
	 * @return String its content
	 * @throws IOException
	 *             on read errors
	 */
	public static final String readResource(URL resourceUrl) throws IOException {
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = null;
		if (logger.isTraceEnabled()) {
			logger.trace("Reading from resource " + resourceUrl);
		}
		try {
			reader = new BufferedReader(new InputStreamReader(resourceUrl
					.openStream()));
			String temp = null;
			while ((temp = reader.readLine()) != null) {
				builder.append(temp).append("\n");
			}
		} finally {
			close(reader);
		}
		return builder.toString();
	}

	/**
	 * Load a local resource as a byte array.
	 * 
	 * @param resourceUrl
	 *            the resource URL
	 * @return byte[] its content
	 * @throws IOException
	 *             on read errors
	 */
	public static final byte[] readResourceData(URL resourceUrl)
			throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		if (logger.isTraceEnabled()) {
			logger.trace("Reading from resource " + resourceUrl);
		}
		InputStream input = null;
		try {
			input = resourceUrl.openStream();
			copyStream(input, out);
		} finally {
			close(input);
		}
		return out.toByteArray();
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

		if (logger.isTraceEnabled()) {
			logger.trace("Streaming file " + file.getAbsolutePath());
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
			close(fis);
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
		logger.trace("Copying between streams");
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
	 *             if the arguments are <code>null</code> or the source is not a
	 *             file
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
		if (logger.isTraceEnabled()) {
			logger.trace("Copying file " + source.getAbsolutePath()
					+ " to file " + dest.getAbsolutePath());
		}
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(dest);
			streamFile(source, out);
		} catch (Exception e) {
			logger.error("Error copying file", e);
			throw new GenericSystemException("Error copying file", e);
		} finally {
			close(out);
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

		if (inputStream == null || dest == null) {
			logger.error("Arguments cannot be null");
			throw new IllegalArgumentException("Arguments cannot be null");
		}

		if (logger.isTraceEnabled()) {
			logger.trace("Copying stream to file " + dest.getAbsolutePath());
		}

		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(dest);
			copyStream(inputStream, outputStream, crc);
		} catch (Exception e) {
			logger.error("Error copying stream", e);
			throw new GenericSystemException("Error copying stream", e);
		} finally {
			close(outputStream);
		}
	}

	/**
	 * Calculate the CRC from a file.
	 * 
	 * @param file
	 *            the file
	 * @return CRC32 the CRC
	 * @throws GenericSystemException
	 *             on error
	 * @throws IllegalArgumentException
	 *             if the argument is null or not a file
	 */
	public static final CRC32 getCrc(File file) throws GenericSystemException,
			IllegalArgumentException {
		if (file == null || !file.isFile()) {
			logger.error("File is null or not a file");
			throw new IllegalArgumentException("File is null or not a file");
		}

		FileInputStream fios = null;
		CRC32 crc = null;
		try {
			fios = new FileInputStream(file);
			crc = getCrc(fios);
		} catch (IOException ex) {
			logger.error("Error calculating CRC", ex);
			throw new GenericSystemException("Error calculating CRC", ex);
		} finally {
			StreamUtils.close(fios);
		}
		return crc;
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
		if (logger.isTraceEnabled()) {
			logger.trace("CRC is " + crc.getValue());
		}
		return crc;
	}

	/**
	 * Attempts to close the stream, suppressing all exceptions.
	 * 
	 * @param in
	 *            the stream
	 */
	public static final void close(InputStream in) {
		try {
			in.close();
		} catch (Exception e) {
			// do nothing
		}
	}

	/**
	 * Attempts to close the stream, suppressing all exceptions.
	 * 
	 * @param out
	 *            the stream
	 */
	public static final void close(OutputStream out) {
		try {
			out.close();
		} catch (Exception e) {
			// do nothing
		}
	}

	/**
	 * Attempts to close the reader, suppressing all exceptions.
	 * 
	 * @param in
	 *            the reader
	 */
	public static final void close(Reader in) {
		try {
			in.close();
		} catch (Exception e) {
			// do nothing
		}
	}

	/**
	 * Attempts to close the writer, suppressing all exceptions.
	 * 
	 * @param out
	 *            the writer
	 */
	public static final void close(Writer out) {
		try {
			out.close();
		} catch (Exception e) {
			// do nothing
		}
	}

	/**
	 * @return String the platform's default encoding
	 */
	public static final String getDefaultEncoding() {
		byte[] byteArray = { 'a' };
		InputStream inputStream = new ByteArrayInputStream(byteArray);
		InputStreamReader reader = new InputStreamReader(inputStream);
		return reader.getEncoding();
	}

}