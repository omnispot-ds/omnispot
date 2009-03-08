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
	
	private WeatherDataSource weatherDataSource;
	
	public WeatherDataSource getWeatherDataSource() {
		return weatherDataSource;
	}

	public void setWeatherDataSource(WeatherDataSource weatherDataSource) {
		this.weatherDataSource = weatherDataSource;
	}

	public FlashWeatherComponent() {
		super();
	}
	
    private int count;
    
    @Override
    public void repaint() throws ComponentException {
    	super.repaint();
    	if (count++ == 10) {
    		doInvoke("setWeatherData", new Object[] { weatherDataSource.getProcessedWeatherData() });
    	}
    }

	@Override
	public void afterPropertiesSet() throws Exception {
		
	}
	
	public void setWeatherData(WeatherData weatherData) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("date", weatherData.date);
		map.put("imageUrl", weatherData.imageUrl);
		map.put("location", weatherData.location);
		map.put("otherData", weatherData.otherData);
		map.put("temperature", weatherData.temperature);
		
		String s = ExternalInterfaceSerializer.encodeInvoke("setData", new Object[]{map});
		flash.callFunction(new BStr(s));
	}

	@Override
	public Set<Resource> gatherResources() {
		HashSet<Resource> retVal = new HashSet<Resource>();
		retVal.add(source);
		retVal.addAll(weatherDataSource.gatherResources());
		return retVal;
	}

}
