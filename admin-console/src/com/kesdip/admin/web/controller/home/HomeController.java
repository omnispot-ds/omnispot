/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 9, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.admin.web.controller.home;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import com.kesdip.business.beans.QuickSearchBean;
import com.kesdip.business.logic.LogicFactory;

/**
 * Invoked when the home page is rendered. Performs status queries and puts
 * {@link StatusBean} in the request. Redirects to the appropriate view
 * depending on the request.
 * 
 * @author gerogias
 */
public class HomeController extends ParameterizableViewController {

	/**
	 * A named collection of views.
	 */
	private Properties views = null;

	/**
	 * The logic factory.
	 */
	private LogicFactory logicFactory = null;

	/**
	 * @return the logicFactory
	 */
	public LogicFactory getLogicFactory() {
		return logicFactory;
	}

	/**
	 * @param logicFactory
	 *            the logicFactory to set
	 */
	public void setLogicFactory(LogicFactory logicFactory) {
		this.logicFactory = logicFactory;
	}

	/**
	 * Perform queries and set the object in the request.
	 * 
	 * @see org.springframework.web.servlet.mvc.ParameterizableViewController#handleRequestInternal(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		updateStatusBean(request);
		// default value
		String view = getViewName();
		String key = null;
		for (Object temp : views.keySet()) {
			key = (String) temp;
			if (request.getRequestURI().contains(key)) {
				view = views.getProperty(key);
			}
		}
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("searchDataObject", new QuickSearchBean());
		return new ModelAndView(view, model);
	}

	/**
	 * Updates the status bean in the request.
	 * 
	 * @param req
	 *            the request
	 */
	private final void updateStatusBean(HttpServletRequest req) {
//		AntennaLogic antennaLogic = logicFactory.getAntennaLogic();
//		FuelTankLogic fuelTankLogic = logicFactory.getFuelTankLogic();
//		StatusBean bean = new StatusBean();
//		bean.setAntennasOnBackup(antennaLogic.getAntennasOnBackup());
//		bean.setAntennasDown(antennaLogic.getAntennasDown());
//		bean.setTotalFuel(fuelTankLogic.getTotalFuel());
//		req.setAttribute(IObjectKeysEnum.STATUS_BEAN_KEY, bean);
	}

	/**
	 * @return the views
	 */
	public Properties getViews() {
		return views;
	}

	/**
	 * @param views the views to set
	 */
	public void setViews(Properties views) {
		this.views = views;
	}

}
