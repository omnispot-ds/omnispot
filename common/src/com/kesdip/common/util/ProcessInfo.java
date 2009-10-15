package com.kesdip.common.util;

/**
 * Helper class that represents a single process. For the time being we are
 * only modeling the PID and the executable name.
 * 
 * @author Pafsanias Ftakas
 */
public class ProcessInfo {
	private long pid;
	private String executable;
	
	public ProcessInfo(long pid, String executable) {
		this.pid = pid;
		this.executable = executable;
	}
	
	public long getPid() {
		return pid;
	}
	
	public String getExecutable() {
		return executable;
	}
}
