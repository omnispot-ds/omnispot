package com.kesdip.designer.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.zip.CRC32;

public class FileUtils {
	public static long getChecksum(String path) {
		try {
			CRC32 crc = new CRC32();
			File file = new File(path);
			InputStream is = new BufferedInputStream(new FileInputStream(file));
			byte[] buffer = new byte[2048];
			int readCount = 0;
			while ((readCount = is.read(buffer)) != -1) {
				crc.update(buffer, 0, readCount);
			}
			is.close();
			return crc.getValue();
		} catch (Exception e) {
			DesignerLog.logError("Unable to calculate checksum for: " + path, e);
			return 0L;
		}
	}
}
