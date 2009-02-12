package com.kesdip.player.components.weather;

public abstract class WeatherDataSource {
	
	protected WeatherDataProcessor weatherDataProcessor;
	
	public void setWeatherDataProcessor(WeatherDataProcessor weatherDataProcessor) {
		this.weatherDataProcessor = weatherDataProcessor;
	}
	
	public abstract Object getWeatherData();
	
	public WeatherData getProcessedWeatherData() {
		return weatherDataProcessor.process(getWeatherData());
	}

}
