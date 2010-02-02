package com.kesdip.player.components.weather;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import com.jniwrapper.win32.automation.types.BStr;
import com.kesdip.player.component.flash.ExternalInterfaceSerializer;
import com.kesdip.player.components.ComponentException;
import com.kesdip.player.components.FlashComponent;
import com.kesdip.player.components.Resource;

public class FlashWeatherComponent extends FlashComponent implements InitializingBean {
	
	private final static Logger logger = Logger.getLogger(FlashWeatherComponent.class);
	
	public final static WeatherData WEATHERDATA_SCRIPT_PUSH = new WeatherData();
	
	private WeatherDataSource weatherDataSource;
	
	private static FlashWeatherComponent singleton;
	
	public static FlashWeatherComponent getInstance() {
		return singleton;
	}
	
	public WeatherDataSource getWeatherDataSource() {
		return weatherDataSource;
	}

	public void setWeatherDataSource(WeatherDataSource weatherDataSource) {
		this.weatherDataSource = weatherDataSource;
	}

	public FlashWeatherComponent() {
		super();
		singleton = this;
	}
	
	@Override
	protected void initInvocationProxy() {
		invocationProxy = new InvocationProxy();
	}
	
    private int count;
    
    @Override
    public void repaint() throws ComponentException {
    	super.repaint();
    	if (count++ == 10) {
    		setWeatherData(weatherDataSource.getProcessedWeatherData());
    	}
    }

	@Override
	public void afterPropertiesSet() throws Exception {
		
	}
	
	public void setWeatherData(WeatherData weatherData) {
		if (weatherData == WEATHERDATA_SCRIPT_PUSH)
			return;
		doInvoke("setWeatherData", weatherData);
	}
	
	public void setWeatherData(Map<String, ?> weatherData) {
		doInvoke("setWeatherData", weatherData);
	}
	
	private void setWeatherDataPrivate(Map<String, ?> weatherData) {
		if (weatherData == null)
			return;
		//Map<String, String> map = new HashMap<String, String>();
		//map.put("date", weatherData.date);
		//map.put("imageUrl", weatherData.imageUrl);
		//map.put("location", weatherData.location);
		//map.put("otherData", weatherData.otherData);
		//map.put("temperature", weatherData.temperature);
		
		String s = ExternalInterfaceSerializer.encodeInvoke("setWeatherData", new Object[]{weatherData});
		flash.callFunction(new BStr(s));
	}

	@Override
	public Set<Resource> gatherResources() {
		HashSet<Resource> retVal = new HashSet<Resource>();
		retVal.add(source);
		retVal.addAll(weatherDataSource.gatherResources());
		return retVal;
	}
	
	protected class InvocationProxy extends FlashComponent.InvocationProxy {
		public void setWeatherData(Map<String, ?> weatherData) {
			FlashWeatherComponent.this.setWeatherDataPrivate(weatherData);
		}
	}

}
