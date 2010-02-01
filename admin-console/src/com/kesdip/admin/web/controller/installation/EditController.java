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

import com.kesdip.admin.web.controller.installation .ViewController;
import com.kesdip.business.domain.admin.generated.Installation;
import com.kesdip.business.exception.ValidationException;
import com.kesdip.business.logic.InstallationLogic;

/**
 * Controller for Installation editing.
 * 
 * @author gerogias
 */
public class EditController extends ViewController {

	/**
	 * Serialization version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Performs object update.
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
			Installation dbInstallation = logic.update(installation);
			setCurrentObject(request, dbInstallation);
		} catch (ValidationException ve) {
			return handleErrors(request, response, ve, installation);
		} catch (Exception e) {
			return handleErrors(request, response, e);
		}
		return new ModelAndView(getSuccessView());
	}

}
