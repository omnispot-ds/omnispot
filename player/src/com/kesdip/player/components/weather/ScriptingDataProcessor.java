package com.kesdip.player.components.weather;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.log4j.Logger;

import com.kesdip.player.components.Resource;
import com.kesdip.player.registry.ContentRegistry;

public class ScriptingDataProcessor extends WeatherDataProcessor {
	
	private Resource scriptFile;
	
	private ScriptingHelper helper = new ScriptingHelper();
	
	public Resource getScriptFile() {
		return scriptFile;
	}

	public void setScriptFile(Resource scriptFile) {
		this.scriptFile = scriptFile;
		helper.setScriptFile(scriptFile);
	}
	
	public ScriptingDataProcessor() {
		super();
	}

	@Override
	public Set<Resource> gatherResources() {
		HashSet<Resource> retVal = new HashSet<Resource>();
		retVal.add(scriptFile);
		return retVal;
	}

	@Override
	public WeatherData process(Object sourceData) {
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("sourceData", sourceData);
		return (WeatherData) helper.process(context);
	}
}
