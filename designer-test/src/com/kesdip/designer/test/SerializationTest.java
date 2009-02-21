package com.kesdip.designer.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import junit.framework.TestCase;

import com.kesdip.designer.model.ComponentModelElement;
import com.kesdip.designer.model.Deployment;
import com.kesdip.designer.model.ImageComponent;
import com.kesdip.designer.model.Layout;
import com.kesdip.designer.model.Region;
import com.kesdip.designer.model.Resource;
import com.kesdip.designer.model.TickerComponent;
import com.kesdip.designer.model.VideoComponent;

public class SerializationTest extends TestCase {
	private Deployment createTestDeployment() {
		Deployment retVal = new Deployment();
		retVal.setPropertyValue(Deployment.WIDTH_PROP, "800");
		retVal.setPropertyValue(Deployment.HEIGHT_PROP, "600");
		retVal.setPropertyValue(Deployment.BIT_DEPTH_PROP, "24");
		retVal.setPropertyValue(Deployment.ID_PROP, "1234567890");
		retVal.setPropertyValue(Deployment.START_TIME_PROP, "1/1/1970 00:00");
		
		Layout l = new Layout();
		l.setPropertyValue(Layout.NAME_PROP, "Layout1");
		l.setPropertyValue(Layout.CRON_EXPRESSION_PROP, "1 2 3 4 5");
		l.setPropertyValue(Layout.DURATION_PROP, "2000");
		retVal.addLayout(l);
		
		Region r = new Region();
		r.setPropertyValue(Region.NAME_PROP, "Region1");
		r.setPropertyValue(Region.TRANSPARENT_PROP, "false");
		r.setPropertyValue(ComponentModelElement.XPOS_PROP, "0");
		r.setPropertyValue(ComponentModelElement.YPOS_PROP, "0");
		r.setPropertyValue(ComponentModelElement.WIDTH_PROP, "800");
		r.setPropertyValue(ComponentModelElement.HEIGHT_PROP, "600");
		l.addRegion(r);
		
		VideoComponent v = new VideoComponent();
		v.setPropertyValue(VideoComponent.REPEAT_PROP, "true");
		v.setPropertyValue(ComponentModelElement.XPOS_PROP, "0");
		v.setPropertyValue(ComponentModelElement.YPOS_PROP, "0");
		v.setPropertyValue(ComponentModelElement.WIDTH_PROP, "800");
		v.setPropertyValue(ComponentModelElement.HEIGHT_PROP, "600");
		v.addVideo(new Resource("resources/kallisti.jpg", ""));
		v.addVideo(new Resource("resources/leverage.jpg", ""));
		v.addVideo(new Resource("resources/rasputin22.jpg", ""));
		r.addComponent(v);
		
		r = new Region();
		r.setPropertyValue(Region.NAME_PROP, "Region2");
		r.setPropertyValue(Region.TRANSPARENT_PROP, "true");
		r.setPropertyValue(ComponentModelElement.XPOS_PROP, "0");
		r.setPropertyValue(ComponentModelElement.YPOS_PROP, "500");
		r.setPropertyValue(ComponentModelElement.WIDTH_PROP, "800");
		r.setPropertyValue(ComponentModelElement.HEIGHT_PROP, "100");
		l.addRegion(r);
		
		TickerComponent t = new TickerComponent();
		t.setPropertyValue(TickerComponent.URL_PROP, "This is a test.");
		t.setPropertyValue(ComponentModelElement.XPOS_PROP, "0");
		t.setPropertyValue(ComponentModelElement.YPOS_PROP, "500");
		t.setPropertyValue(ComponentModelElement.WIDTH_PROP, "800");
		t.setPropertyValue(ComponentModelElement.HEIGHT_PROP, "100");
		r.addComponent(t);
		
		l = new Layout();
		l.setPropertyValue(Layout.NAME_PROP, "Layout2");
		retVal.addLayout(l);
		
		r = new Region();
		r.setPropertyValue(Region.NAME_PROP, "Region3");
		r.setPropertyValue(Region.TRANSPARENT_PROP, "false");
		r.setPropertyValue(ComponentModelElement.XPOS_PROP, "0");
		r.setPropertyValue(ComponentModelElement.YPOS_PROP, "0");
		r.setPropertyValue(ComponentModelElement.WIDTH_PROP, "800");
		r.setPropertyValue(ComponentModelElement.HEIGHT_PROP, "600");
		l.addRegion(r);
		
		ImageComponent i = new ImageComponent();
		i.setPropertyValue(ComponentModelElement.XPOS_PROP, "0");
		i.setPropertyValue(ComponentModelElement.YPOS_PROP, "0");
		i.setPropertyValue(ComponentModelElement.WIDTH_PROP, "800");
		i.setPropertyValue(ComponentModelElement.HEIGHT_PROP, "600");
		i.addImage(new Resource("resources/hot_potato.jpg", ""));
		i.addImage(new Resource("resources/Bakunin_Nadar.jpg", ""));
		r.addComponent(i);
		
		return retVal;
	}
	
	public void testSerialization() throws Exception {
		Deployment d = createTestDeployment();
		OutputStream os = new FileOutputStream("test.xml");
		d.serialize(os);
		os.close();
		
		Deployment other = new Deployment();
		InputStream is = new FileInputStream("test.xml");
		other.deserialize(is);
		is.close();
		
		other.checkEquivalence(d);
	}
}
