/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Jan 23, 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.admin.web.controller.installation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import com.kesdip.admin.web.controller.BaseFormController;
import com.kesdip.business.domain.admin.generated.Installation;
import com.kesdip.business.domain.admin.generated.Site;
import com.kesdip.business.exception.ValidationException;
import com.kesdip.business.logic.InstallationLogic;
import com.kesdip.business.logic.SiteLogic;

/**
 * Controller for the {@link Installation} creation form.
 * 
 * @author gerogias
 */
public class CreateController extends BaseFormController {

	/**
	 * Serialization version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Performs object creation.
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

		Installation installation = (Installation) command;
		InstallationLogic logic = getLogicFactory().getInstallationLogic();
		try {
			logic.create(installation);
			setCurrentObject(request, installation);
		} catch (ValidationException ve) {
			return handleErrors(request, response, ve, installation);
		} catch (Exception e) {
			return handleErrors(request, response, e);
		}
		return new ModelAndView(getSuccessView());
	}

	/**
	 * Initialize the internal Customer object from the DB.
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {
		Installation installation = (Installation) super.formBackingObject(request);

		try {
			Site site = new Site();
			site.setId(Long.valueOf(request.getParameter("site.id")));
	
			SiteLogic logic = getLogicFactory().getSiteLogic();
			Site dbSite = logic.getInstance(site);
			installation.setSite(dbSite);
			setCurrentObject(request, dbSite);
		} catch (Exception e) {
			// do nothing
		}
		return installation;
	}

}
