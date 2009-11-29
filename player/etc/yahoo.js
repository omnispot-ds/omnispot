importPackage(org.dom4j);
importPackage(com.kesdip.player.components.weather);

// el.getNamespaceForPrefix(arg0)
// QName(String, Namespace)
if (sourceData == null){}
	//
else {
	yweatherNs = sourceData.getNamespaceForPrefix('yweather');
	//def city = sourceData.element('channel').element('yweather:location').attributeValue('city')
	chanElement = sourceData.element('channel')
	locElement = chanElement.element(new QName('location', yweatherNs));
	itemElement = chanElement.element('item');
	condElement = itemElement.element(new QName('condition', yweatherNs));
	//println locElement.name
	weatherData = new WeatherData();
	weatherData.location = locElement.attributeValue('city');
	weatherData.date = condElement.attributeValue('date');
	weatherData.imageUrl = 'http://l.yimg.com/a/i/us/we/52/27.gif';
	weatherData.temperature = condElement.attributeValue('temp');
	weatherData.otherData = '<b>blah:</b> bloom';
}