package com.kesdip.player.component.flash;

import java.util.ArrayList;
import java.util.List;

public class ExternalInterfaceCall {
	
	private String functionName;
	private List<Object> arguments = new ArrayList<Object>();
	
	public ExternalInterfaceCall(String functionName) {
		this.functionName = functionName;
	}
	
	public String getFunctionName() {
		return functionName;
	}
	
	public Object[] getArguments() {
		return arguments.toArray();
	}
	
	public void addArgument(Object arg) {
		arguments.add(arg);
	}
	
	@Override
	public String toString() {
		return super.toString();
	}

}
