import org.dom4j.QName;
import org.dom4j.Element;
import com.kesdip.player.components.weather.WeatherData;

// el.getNamespaceForPrefix(arg0)
// QName(String, Namespace)
class yahoo {
	
	public static Element sourceData;
	public static WeatherData weatherData;
	
	public static void main(String... args) {
		
		if (sourceData == null){}
			//
		else {
			Element yweatherNs = sourceData.getNamespaceForPrefix("yweather");
			//def city = sourceData.element('channel').element('yweather:location').attributeValue('city')
			Element chanElement = sourceData.element("channel");
			Element locElement = chanElement.element(new QName("location", yweatherNs));
			//println locElement.name
			weatherData = new WeatherData();
			weatherData.location = locElement.attributeValue("city");
		}
		
		System.out.println("hello from java");
	}
}