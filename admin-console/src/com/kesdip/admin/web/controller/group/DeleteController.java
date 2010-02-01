/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 8, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.admin.web.controller.group;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import com.kesdip.admin.web.controller.BaseFormController;
import com.kesdip.business.domain.admin.generated.Customer;
import com.kesdip.business.domain.admin.generated.InstallationGroup;
import com.kesdip.business.exception.ValidationException;
import com.kesdip.business.logic.GroupLogic;

/**
 * Controller for {@link InstallationGroup} deletion.
 * 
 * @author gerogias
 */
public class DeleteController extends BaseFormController {

	/**
	 * Serialization version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Performs object removal.
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

		InstallationGroup group = (InstallationGroup) command;
		GroupLogic logic = getLogicFactory().getGroupLogic();
		
		Customer parent = logic.getInstance(group).getCustomer();
		try {
			logic.delete(group);
		} catch (ValidationException ve) {
			return handleErrors(request, response, ve, group);
		} catch (Exception e) {
			return handleErrors(request, response, e);
		}
		setCurrentObject(request, parent);
		return new ModelAndView(getSuccessView());
	}

}
