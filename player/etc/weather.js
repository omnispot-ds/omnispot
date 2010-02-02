
importPackage(com.kesdip.player.components.weather);

// refresh every hour
var t = ScriptUtils.setInterval(function() {
		updateWeather();
}, 60 * 60 * 1000);
// in case you need to cancel the timeout or interval use t.cancel();


function updateWeather() {
	doc = ScriptUtils.testLoadXML("z:/WeatherWidget/Weather.xml"); // or use loadXML(url)
	builder = ScriptUtils.weatherDataBuilder(3000); // how long to display each forecast (milliseconds). this setting is honored by the swf.
	
	addForecastsForAllGids(builder, doc);
							
	FlashWeatherComponent.getInstance().setWeatherData(builder.getWeatherData());
}

// adds weather data for all locations and dates
function addForecastsForAllGids(builder, doc) {
	nodes = doc.selectNodes("//gid");
	iter = nodes.iterator();
	gids = [];
	while(iter.hasNext()) {
		// java.lang.System.out.println("foo");
		el = iter.next();
		gids.push(el.attributeValue('id'));
	}
	addForecastsForGids(builder, doc, gids);
}

// adds weather data for the specified gids (list of their ids)
function addForecastsForGids(builder, doc, gids) {
	for each (gid in gids)
		addForecastsForGid(builder, doc, gid);
}

// adds weather data for the specified gid
function addForecastsForGid(builder, doc, gid) {
	el = doc.selectNodes("//gid[@id='" + gid + "']").get(0);
	addForecastsByGid(builder, doc, el);
}

// adds weather data for the specified dom4j element
function addForecastsByGid(builder, doc, el) {
	place = el.elementText("PointName");
	
	cur = el.element("Current");
	builder.addForecast(place, "Τώρα", cur.elementText('PointDesc'), cur.elementText('Temperature'), cur.elementText('Humidity'));
	
	addForecasts(builder, place, el.selectNodes(".//date"));	
}

function addForecast(builder, place, el) {
	builder.addForecast(place, el.getText(), el.elementText('PointDesc'), el.elementText('minTemp') + ' ως ' + el.elementText('maxTemp'), el.elementText('minHum') + ' ως ' + el.elementText('maxHum'));
}

function addForecasts(builder, place, els) {
	iter = els.iterator();
	while(iter.hasNext()) {
		el = iter.next();
		addForecast(builder, place, el);
	}
}