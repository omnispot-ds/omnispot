package com.kesdip.player.components.weather;

import java.io.File;
import java.io.FileReader;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.log4j.Logger;

public class ScriptingDataProcessor extends WeatherDataProcessor {
	
	private final static Logger logger = Logger.getLogger(ScriptingDataProcessor.class);
	
	private ScriptEngineManager sem = new ScriptEngineManager();
	private ScriptEngine engine;
	
	private String scriptFile;
	protected File file;
	
	private String scriptTxt;
	private long lastModified;
	
	public ScriptingDataProcessor() {
		super();
	}
	
	public String getScriptFile() {
		return scriptFile;
	}

	public void setScriptFile(String scriptFile) {
		this.scriptFile = scriptFile;
		file = new File(scriptFile);
	}
	
	private void reloadIfNecessary() {
		if (lastModified == 0 || lastModified != file.lastModified()) {
			try {
				char[] buf = new char[(int)file.length()];
				int numRead = new FileReader(file).read(buf);
				scriptTxt = String.valueOf(buf, 0, numRead);
				String fileExtension = file.getName().substring(file.getName().lastIndexOf('.') + 1);
				engine = sem.getEngineByExtension(fileExtension);
				if (engine == null) {
					throw new RuntimeException("No scripting engine could be resolved for script extension ." + fileExtension);
				}
			} catch (Exception ex) {
				throw new RuntimeException("Error reading file " + file.getPath(), ex);
			}
		}
	}

	@Override
	public WeatherData process(Object sourceData) {
		reloadIfNecessary();
		try {
			Bindings bindings = engine.createBindings();
			bindings.put("sourceData", sourceData);
			engine.eval(scriptTxt, bindings);
			WeatherData result = (WeatherData) bindings.get("weatherData");
			return result;
		} catch (ScriptException ex) {
			logger.error("Error evaluating " + scriptFile, ex);
		}
		return null;
	}
}
