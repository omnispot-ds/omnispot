/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 9, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.admin.web.controller.search;

import java.io.Serializable;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import com.kesdip.admin.web.controller.BaseFormController;
import com.kesdip.business.beans.QuickSearchBean;
import com.kesdip.business.exception.ValidationException;
import com.kesdip.business.logic.SearchLogic;
import com.kesdip.common.util.BeanUtils;

/**
 * Quick search web controller.
 * 
 * @author gerogias
 */
public class QuickSearchController extends BaseFormController {

	/**
	 * Serialization version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The map between class names and views to forward to.
	 */
	private Properties views = null;

	/**
	 * @return the views
	 */
	public Properties getViews() {
		return views;
	}

	/**
	 * @param views
	 *            the views to set
	 */
	public void setViews(Properties views) {
		this.views = views;
	}

	/**
	 * Performs the search and forwards to the appropriate screen.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param command
	 *            the DTO
	 * @param errors
	 *            the errors object
	 * @return ModelAndView the sucess or failure view
	 */
	@Override
	public ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
			throws Exception {

		QuickSearchBean bean = (QuickSearchBean) command;
		SearchLogic logic = getLogicFactory().getSearchLogic();
		String view = getFormView();
		try {
			Serializable result = logic.quickSearch(bean);
			view = resolveViewName(result);
		} catch (ValidationException ve) {
			return handleErrors(request, response, ve, bean);
		} catch (Exception e) {
			return handleErrors(request, response, e);
		}
		response.sendRedirect(request.getContextPath() + view);
		return new ModelAndView(view);
	}

	/**
	 * Resolves the view based on the class name of the object.
	 * <p>
	 * The object's primary key is used to create a request parameter of the
	 * form <code>/foo/bar/view.do?id=key</code>.
	 * </p>
	 * 
	 * @param result
	 *            the object to check
	 * @return String the view, post-fixed with an identifier
	 */
	private final String resolveViewName(Serializable result) throws Exception {
		if (result == null) {
			return views.getProperty("null");
		}
		String className = BeanUtils.getCleanClassName(result.getClass());
		String view = views.getProperty(className);
		String key = null;
		try {
			key = BeanUtils.getProperty((Object)result, "id");
		} catch (NoSuchMethodException nsme) {
			// try with username
			key = BeanUtils.getProperty((Object)result, "username");
		}
		if (key != null) {
			return view + "?id=" + key;
		}
		key = BeanUtils.getProperty((Object)result, "username");
		return view + "?id=" + key;
	}
}
