/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 4, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */
package com.kesdip.admin.web.util;

import java.util.Locale;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.tiles.TilesJstlView;

/**
 * A Spring <code>ViewResolver</code>.
 * It instantiates <code>TilesJstlViews</code> or <code>JstlViews</code>.
 * @author gerogias
 */
public class SpringViewResolver implements ViewResolver, ApplicationContextAware {

	private ApplicationContext context;
	
	/**
	 * Instantiate a new <code>View</code>.
	 * It ises the following rules
	 * <ul>
	 * <li>if suffix is ".tiles", create a new <code>TilesJstlView</code></li>
	 * <li>otherwise create a <code>JstlView</code></li>
	 * </ul>
	 *  .
	 * @see org.springframework.web.servlet.ViewResolver#resolveViewName(java.lang.String, java.util.Locale)
	 */
	public View resolveViewName(String url, Locale locale) throws Exception {
		View view = null;
		if (url.endsWith(".tiles")) {
			view = new TilesJstlView();
			((TilesJstlView)view).setUrl(url);
			((TilesJstlView)view).setApplicationContext(context);
			
		} else {
			view = new JstlView();
			((JstlView)view).setUrl(url);
			((JstlView)view).setApplicationContext(context);
		}		
		return view;
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}

}
