package com.kesdip.bootstrap;

import com.kesdip.common.util.ProcessUtils;

import junit.framework.TestCase;

public class KillExplorerTest extends TestCase {
	public void testExplorerDeath() {
		ProcessUtils.killAll("explorer.exe");
	}
}
