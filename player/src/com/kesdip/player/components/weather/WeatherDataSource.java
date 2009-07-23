package com.kesdip.player.components.weather;

import java.util.Set;

import com.kesdip.player.components.Resource;

public abstract class WeatherDataSource {
	
	protected WeatherDataProcessor weatherDataProcessor;
	
	public void setWeatherDataProcessor(WeatherDataProcessor weatherDataProcessor) {
		this.weatherDataProcessor = weatherDataProcessor;
	}
	
	public abstract Object getWeatherData();
	
	public WeatherData getProcessedWeatherData() {
		return weatherDataProcessor.process(getWeatherData());
	}

	public Set<Resource> gatherResources() {
		return weatherDataProcessor.gatherResources();
	}
}
