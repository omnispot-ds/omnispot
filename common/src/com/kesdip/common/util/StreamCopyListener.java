/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 11 Ιουλ 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.common.util;

import java.util.zip.CRC32;

/**
 * Listener for copy of a stream. The listener determines every how many bytes
 * it will be notified.
 * <p>
 * Important: For optimization reasons the listener cannot specify the exact
 * number of bytes but the number of <code>2048</code> byte frames. Therefore,
 * if {@link #getByteBufferCount()} returns 10, this means that the listener
 * will be notified every <code>10 * 2048</code> bytes.
 * </p>
 * 
 * @author gerogias
 * @see StreamUtils#copyStream(java.io.InputStream, java.io.OutputStream,
 *      java.util.zip.CRC32, StreamCopyListener)
 */
public interface StreamCopyListener {

	/**
	 * Instructs the stream copy function every how many frames of
	 * <code>2048</code> bytes the listener will be notified.
	 * 
	 * @return int the number of frames. Cannot be 0 or negative, must
	 *         <em>always</em> be the same
	 */
	int getByteBufferCount();

	/**
	 * Notifies the listener that <code>numOfBytes</code> have been copied. In
	 * the usual case this number will be
	 * <code>{@link #getByteBufferCount()} * 2048</code>, however this is not
	 * always guarranteed (e.g. when the stream ends or it was starved).
	 * <p>
	 * The method must <em>not</em> allow any exception to propagate to the
	 * calling code.
	 * </p>
	 * 
	 * @param numOfBytes
	 *            number of bytes actually copied
	 * @param currentCrc
	 *            the current value of the CRC, may be <code>null</code>
	 */
	void bufferCopied(int numOfBytes, CRC32 currentCrc);

	/**
	 * Notifies the listener that copying has completed successfully.
	 * <p>
	 * The method must <em>not</em> allow any exception to propagate to the
	 * calling code.
	 * </p>
	 */
	void copyCompleted();

	/**
	 * Notifies the listener that copying has failed.
	 * <p>
	 * The method must <em>not</em> allow any exception to propagate to the
	 * calling code.
	 * </p>
	 */
	void copyFailed();
}
