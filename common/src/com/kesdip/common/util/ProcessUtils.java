package com.kesdip.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.sun.jna.Native;
import com.sun.jna.Pointer;

public class ProcessUtils {
	private static final Logger logger = Logger.getLogger(ProcessUtils.class);
	
	public interface Kernel32 extends com.sun.jna.examples.win32.Kernel32 {
		/** The INSTANCE. */
		Kernel32	INSTANCE	= (Kernel32) Native.loadLibrary("kernel32", Kernel32.class);

		/*
		 * HANDLE WINAPI OpenProcess( DWORD dwDesiredAccess, BOOL
		 * bInheritHandle, DWORD dwProcessId );
		 */
		
		/**
		 * Open process.
		 * 
		 * @param dwDesiredAccess
		 *            the dw desired access
		 * @param bInheritHandle
		 *            the b inherit handle
		 * @param dwProcessId
		 *            the dw process id
		 * 
		 * @return the pointer
		 */
		Pointer OpenProcess(int dwDesiredAccess, boolean bInheritHandle, int dwProcessId);

		/** The PROCES s_ terminate. */
		int	PROCESS_TERMINATE			= 1;

		/** The PROCES s_ quer y_ information. */
		int	PROCESS_QUERY_INFORMATION	= 1024;

		/** The STANDAR d_ right s_ required. */
		int	STANDARD_RIGHTS_REQUIRED	= 0xF0000;

		/** The SYNCHRONIZE. */
		int	SYNCHRONIZE					= 0x100000;

		/** The PROCES s_ al l_ access. */
		int	PROCESS_ALL_ACCESS			= STANDARD_RIGHTS_REQUIRED | SYNCHRONIZE | 0xFFF;
		
		/*
		 * BOOL WINAPI TerminateProcess( HANDLE hProcess, UINT uExitCode );
		 */

		/**
		 * Terminate process.
		 * 
		 * @param hProcess
		 *            the h process
		 * @param uExitCode
		 *            the u exit code
		 * 
		 * @return true, if successful
		 */
		boolean TerminateProcess(Pointer hProcess, int uExitCode);

		/*
		 * BOOL WINAPI CloseHandle( HANDLE hObject );
		 */
		
		/**
		 * Close handle.
		 * 
		 * @param hObject
		 *            the h object
		 * 
		 * @return true, if successful
		 */
		public boolean CloseHandle(Pointer hObject);

	}
	
	/**
	 * Kill a particular process.
	 * 
	 * @param pid The PID of the process to kill.
	 * @param code The exit code of the process.
	 * @return True iff the process was killed successfully.
	 */
	public static boolean kill(int pid, int code)
	{
		if (pid <= 0)
			return false;
		Pointer hProcess = Kernel32.INSTANCE.OpenProcess(Kernel32.PROCESS_TERMINATE, false, pid);
		boolean result = Kernel32.INSTANCE.TerminateProcess(hProcess, code);
		Thread.yield();
		if (!result)
			System.out.println("kill failed: " + pid);
		Kernel32.INSTANCE.CloseHandle(hProcess);
		return result;
	}

	/**
	 * Get a set of ProcessInfo objects for all the processes running on the
	 * system. This relies on the 'tasklist.exe' utility to be available on the
	 * system which is true for all Windows OS, apart from the Home Editions.
	 * 
	 * @return A Set of ProcessInfo objects.
	 * @throws IOException Iff something goes wrong.
	 */
	public static Set<ProcessInfo> getAllProcesses() throws IOException {
		Set<ProcessInfo> retVal = new HashSet<ProcessInfo>();
		String line;
		Process p = Runtime.getRuntime().exec("tasklist.exe /fo csv /nh");
		BufferedReader input = new BufferedReader(
				new InputStreamReader(p.getInputStream()));
		while ((line = input.readLine()) != null) {
		    if (!line.trim().equals("")) {
		    	// get the process name
		    	line = line.substring(1);
		    	String executable = line.substring(0, line.indexOf('"'));
		    	String remainder = line.substring(line.indexOf('"') + 3);
		    	long pid = Long.parseLong(remainder.substring(0, remainder.indexOf('"')));
		    	retVal.add(new ProcessInfo(pid, executable));
		    }
		}
		input.close();

		return retVal;
	}

	/**
	 * Kill all processes that are running the executable that is given as an
	 * argument.
	 * 
	 * @param executableName The executable that all processes to be killed
	 * are running.
	 * @return True iff all processes running the specified executable are
	 * killed successfully.
	 * @throws IOException Iff something goes wrong.
	 */
	public static boolean killAll(String executableName) throws IOException {
		Set<ProcessInfo> processes = getAllProcesses();

		Set<Integer> toKillPids = new HashSet<Integer>();
		for (ProcessInfo pi : processes) {
			if (pi.getExecutable().equals(executableName))
				toKillPids.add((int) pi.getPid());
		}
		
		boolean retVal = true;
		for (Integer pid : toKillPids) {
			if (!kill(pid, 0)) {
				logger.info("Unable to kill notepad process");
				retVal = false;
			}
		}
	
		return retVal;
	}
}
