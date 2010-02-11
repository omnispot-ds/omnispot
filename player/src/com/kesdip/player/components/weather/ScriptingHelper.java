package com.kesdip.player.components.weather;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.log4j.Logger;

import com.kesdip.player.components.Resource;
import com.kesdip.player.registry.ContentRegistry;

public class ScriptingHelper {
	
	private final static Logger logger = Logger.getLogger(ScriptingHelper.class);
	
	// for fast debugging - should be null for deployments to work.
	protected File file = null; //new File("z:/MyScript.js");
	
	private Resource scriptFile;
	
	private String scriptTxt;
	private long lastModified;
	
	private ScriptEngineManager sem = new ScriptEngineManager();
	private ScriptEngine engine;
	
	public Resource getScriptFile() {
		return scriptFile;
	}

	public void setScriptFile(Resource scriptFile) {
		this.scriptFile = scriptFile;
	}

	private void reloadIfNecessary() {
		if (file == null) {
			ContentRegistry registry = ContentRegistry.getContentRegistry();
			String filename = registry.getResourcePath(scriptFile, true);
			file = new File(filename);
		}
		if (lastModified == 0 || lastModified != file.lastModified()) {
			try {
				char[] buf = new char[(int)file.length()];
				Reader in = new InputStreamReader(new FileInputStream(file), "UTF-8");
				int numRead = in.read(buf);
				scriptTxt = String.valueOf(buf, 0, numRead);
				String fileExtension = file.getName().substring(file.getName().lastIndexOf('.') + 1);
				// we cannot count on an extension, they are not maintained upon deployment
				engine = sem.getEngineByName("JavaScript");
				if (engine == null) {
					throw new RuntimeException("No scripting engine could be resolved for script extension ." + fileExtension);
				}
			} catch (Exception ex) {
				throw new RuntimeException("Error reading file " + file.getPath(), ex);
			}
		}
	}
	
	public Object process(Map<String, ?> context) {
		reloadIfNecessary();
		try {
			Bindings bindings = engine.createBindings();
			bindings.putAll(context);
			engine.eval(scriptTxt, bindings);
			Object result = bindings.get("result");
			return result;
		} catch (ScriptException ex) {
			if (scriptFile != null)
				logger.error("Error evaluating " + scriptFile.getIdentifier(), ex);
			else
				logger.error("Error evaluating " + file.getPath(), ex);
		}
		return null;
	}
	
}
