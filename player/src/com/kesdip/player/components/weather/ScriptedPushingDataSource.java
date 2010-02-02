package com.kesdip.player.components.weather;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.kesdip.player.components.Resource;

public class ScriptedPushingDataSource extends WeatherDataSource {
	
	private Resource scriptFile;
	
	private ScriptingHelper helper = new ScriptingHelper();
	
	public Resource getScriptFile() {
		return scriptFile;
	}

	public void setScriptFile(Resource scriptFile) {
		this.scriptFile = scriptFile;
		helper.setScriptFile(scriptFile);
	}
	
	@Override
	public Set<Resource> gatherResources() {
		HashSet<Resource> retVal = new HashSet<Resource>();
		retVal.add(scriptFile);
		return retVal;
	}

	@Override
	public Object getWeatherData() {
		return FlashWeatherComponent.WEATHERDATA_SCRIPT_PUSH;
	}
	
	@Override
	public WeatherData getProcessedWeatherData() {
		Map<String, Object> context = new HashMap<String, Object>();
		helper.process(context);
		return FlashWeatherComponent.WEATHERDATA_SCRIPT_PUSH;
	}

}
