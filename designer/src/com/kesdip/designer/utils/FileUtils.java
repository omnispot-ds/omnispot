package com.kesdip.designer.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.zip.CRC32;

import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

@SuppressWarnings("restriction")
public class FileUtils {
	/*
	 * TODO This is extremely klunky, but I could not find how to open a file
	 * that is outside the workspace directly. So this code temporarily imports
	 * the file into the workspace in a project called temp.
	 */
	public static IFile getFile(File f) throws FileNotFoundException, CoreException {
		IFile deploymentFile = ResourcesPlugin.getWorkspace().getRoot().
			getFile(new Path("temp/" + f.getName()));
		InputStream is = new BufferedInputStream(new FileInputStream(f));
		if (deploymentFile.exists())
			deploymentFile.setContents(is, true, false, null);
		else {
			if (!((Project) deploymentFile.getParent()).exists()) {
				((Project) deploymentFile.getParent()).create(null);
			}
			((Project) deploymentFile.getParent()).open(null);
			deploymentFile.create(is, true, null);
		}

		return deploymentFile;
	}
	
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
