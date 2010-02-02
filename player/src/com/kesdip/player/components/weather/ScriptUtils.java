package com.kesdip.player.components.weather;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

/**
 * Various convenience functions to call from the weather script.
 * 
 * @author n.giamouris
 *
 */
public class ScriptUtils {
	
	/**
	 * Mimics the client-side javascript function, returning a Timer instance
	 * instead of a numeric id. Instead of clearInterval, use Timer.cancel to
	 * clear the interval.
	 * 
	 * @param runnable The runnable to run at the specified interval.
	 * @param period The interval period in milliseconds.
	 * @return A Timer instance.
	 */
	public static Timer setInterval(final Runnable runnable, long period) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				runnable.run();
			}
		}, 0, period);
		return timer;
	}
	
	/**
	 * Similar to the client-side javascript function.
	 * 
	 * @param runnable
	 * @param delay
	 * @return
	 * @see #setInterval
	 */
	public static Timer setTimeout(final Runnable runnable, long delay) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				runnable.run();
			}
		}, delay);
		return timer;
	}
	
	/**
	 * Loads an XML document from the specified url.
	 * @param url
	 * @return The retrieved XML as a Dom4J document.
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws DocumentException
	 */
	public static Document loadXML(String url) 
			throws MalformedURLException, IOException, DocumentException {
		URLConnection conn = new URL(url).openConnection();
		SAXReader sax = new SAXReader();
		Document doc = sax.read(conn.getInputStream());
		return doc;
	}
	
	/**
	 * For testing purposes. Instead of fetching the XML document from the web
	 * it is fetched from the filesystem.
	 * @param filepath
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 * @throws DocumentException
	 * @see #loadXML
	 */
	public static Document testLoadXML(String filepath) 
			throws UnsupportedEncodingException, FileNotFoundException, DocumentException {
		SAXReader reader = new SAXReader();
		Document doc = reader.read(new FileInputStream(filepath), "UTF-8");
		return doc;
	}
	
	/**
	 * For debugging scripts through eclipse. Set a breakpoint and call this from
	 * your script.
	 * @param o
	 */
	public static void debugThis(Object... args) {
		System.out.println("stop!");
	}
	
	/**
	 * Creates a new WeatherBuilder instance.
	 * @param displayInterval
	 * @return
	 * @see WeatherDataBuilder
	 */
	public static WeatherDataBuilder weatherDataBuilder(int displayInterval) {
		return new WeatherDataBuilder(displayInterval);
	}
	
	/**
	 * Builder class to relieve some of the burden of assembling the weather data.
	 */
	public static class WeatherDataBuilder {
		private Map<String, Object> weatherData = new HashMap<String, Object>();
		
		public WeatherDataBuilder() {}
		
		public WeatherDataBuilder(int displayInterval) {
			weatherData.put("displayInterval", displayInterval);
		}
		
		@SuppressWarnings("unchecked")
		public WeatherDataBuilder addForecast(String city, String when, String prediction, 
				String temperature, String humidity) {
			List<Map<String, ?>> forecasts = (List<Map<String, ?>>) weatherData.get("forecasts");
			if (!weatherData.containsKey("forecasts")) {
				forecasts = new ArrayList<Map<String,?>>();
				weatherData.put("forecasts", forecasts);
			}
			Map<String, String> forecast = new HashMap<String, String>();
			forecast.put("city", city);
			forecast.put("when", when);
			forecast.put("prediction", prediction);
			forecast.put("temperature", temperature);
			forecast.put("humidity", humidity);
			forecasts.add(forecast);
			return this;
		}
		
		public Map<String, Object> getWeatherData() {
			return weatherData;
		}
	}

}
