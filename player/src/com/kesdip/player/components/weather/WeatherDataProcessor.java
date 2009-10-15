package com.kesdip.player.components.weather;

import java.util.Set;

import com.kesdip.player.components.Resource;

public abstract class WeatherDataProcessor {

	public abstract WeatherData process(Object sourceData);
	
	public abstract Set<Resource> gatherResources();
}
