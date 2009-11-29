/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 8, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.admin.web.controller;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContextAware;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import com.kesdip.admin.web.constenum.IObjectKeysEnum;
import com.kesdip.business.beans.QuickSearchBean;
import com.kesdip.business.exception.ValidationException;
import com.kesdip.business.logic.LogicFactory;
import com.kesdip.business.util.Error;
import com.kesdip.business.util.list.OptionListGenerator;

/**
 * Base class for all <code>SimpleFormController</code> instances.
 * 
 * @author gerogias
 */
public abstract class BaseFormController extends SimpleFormController implements
		ApplicationContextAware, Serializable {

	/**
	 * The logger.
	 */
	private final static Logger logger = Logger
			.getLogger(BaseFormController.class);

	/**
	 * The logic factory.
	 */
	private LogicFactory logicFactory = null;

	/**
	 * The list generator.
	 */
	private OptionListGenerator listGenerator = null;

	/**
	 * A map with the form views.
	 */
	private Map<String, String> allFormViews = null;

	/**
	 * A map with the success views.
	 */
	private Map<String, String> allSuccessViews = null;

	/**
	 * @return the allFormViews
	 */
	public Map<String, String> getAllFormViews() {
		return allFormViews;
	}

	/**
	 * @param allFormViews
	 *            the allFormViews to set
	 */
	public void setAllFormViews(Map<String, String> allFormViews) {
		this.allFormViews = allFormViews;
	}

	/**
	 * @return the allSuccessViews
	 */
	public Map<String, String> getAllSuccessViews() {
		return allSuccessViews;
	}

	/**
	 * @param allSuccessViews
	 *            the allSuccessViews to set
	 */
	public void setAllSuccessViews(Map<String, String> allSuccessViews) {
		this.allSuccessViews = allSuccessViews;
	}

	/**
	 * Handles basic validation errors for a request. Sends the request back to
	 * the original form with the model populated with the errors and the
	 * modified command object.
	 * 
	 * @param req
	 *            the request
	 * @param res
	 *            the response
	 * @param t
	 *            the thrown exception
	 * @return ModelAndView the view to return to
	 * @throws Exception
	 *             on error
	 */
	public ModelAndView handleErrors(HttpServletRequest req,
			HttpServletResponse res, Throwable t) throws Exception {

		logger.error("Error handling request", t);
		if (t instanceof ServletRequestBindingException) {
			ServletRequestBindingException srbe = (ServletRequestBindingException) t;
			BindException be = (BindException) srbe.getRootCause();
			BindException bindException = new BindException(be.getModel().get(
					be.getObjectName()), getCommandName());
			for (Object oldError : be.getFieldErrors()) {
				bindException.rejectValue(((FieldError) oldError).getField(),
						((FieldError) oldError).getCode(),
						((FieldError) oldError).getCode());
			}
			for (Object oldError : be.getGlobalErrors()) {
				bindException.reject(((ObjectError) oldError).getCode(),
						((ObjectError) oldError).getCode());
			}
			ModelAndView mv = new ModelAndView(getFormView());
			mv.addAllObjects(bindException.getModel());
			return mv;
		} else {
			BindException be = new BindException(formBackingObject(req),
					getCommandName());
			be.reject("some.code", t.getClass().getName() + ":"
					+ t.getMessage());
			ModelAndView mv = new ModelAndView(getFormView());
			mv.addAllObjects(be.getModel());
			return mv;
		}
	}

	/**
	 * Handles business validation errors for a request. Sends the request back
	 * to the original form with the model populated with the errors and the
	 * modified command object.
	 * 
	 * @param req
	 *            the request
	 * @param res
	 *            the response
	 * @param validationException
	 *            the thrown exception
	 * @param object
	 *            the command object
	 * @return ModelAndView the view to return to
	 */
	public ModelAndView handleErrors(HttpServletRequest req,
			HttpServletResponse res, ValidationException validationException,
			Object command) {

		logger.debug("Handling validation errors");
		BindException bindException = new BindException(command,
				getCommandName());

		for (Error e : validationException.getObjectErrors()) {
			bindException.reject(e.getMessageKey(), e.getMessageKey());
		}
		for (Error e : validationException.getFieldErrors()) {
			bindException.rejectValue(e.getField(), e.getMessageKey(), e
					.getMessageKey());
		}

		ModelAndView mv = new ModelAndView(getFormView());
		mv.addAllObjects(bindException.getModel());
		return mv;
	}

	/**
	 * Sets the list generator in the model.
	 * 
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#referenceData(javax.servlet.http.HttpServletRequest,
	 *      java.lang.Object, org.springframework.validation.Errors)
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected Map referenceData(HttpServletRequest request, Object command,
			org.springframework.validation.Errors errors) throws Exception {
		Map<String, Object> entries = new HashMap<String, Object>();
		entries.put("listGenerator", getListGenerator());
		entries.put("searchDataObject", new QuickSearchBean());
		return entries;
	}

	/**
	 * Sets the list generator in the model.
	 * 
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#processFormSubmission(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse, java.lang.Object,
	 *      org.springframework.validation.BindException)
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected ModelAndView processFormSubmission(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
			throws Exception {
		ModelAndView view = super.processFormSubmission(request, response,
				command, errors);
		view.getModel().put("listGenerator", getListGenerator());
		view.getModel().put("searchDataObject", new QuickSearchBean());
		return view;
	}

	/**
	 * Sets the current object in the request.
	 * 
	 * @param req
	 *            the request
	 * @param obj
	 *            the object
	 */
	protected void setCurrentObject(HttpServletRequest req, Object obj) {

		req.setAttribute(IObjectKeysEnum.CURRENT_REQUEST_OBJECT, obj);
	}

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
	 * @return the listGenerator
	 */
	public OptionListGenerator getListGenerator() {
		return listGenerator;
	}

	/**
	 * @param listGenerator
	 *            the listGenerator to set
	 */
	public void setListGenerator(OptionListGenerator listGenerator) {
		this.listGenerator = listGenerator;
	}
}
