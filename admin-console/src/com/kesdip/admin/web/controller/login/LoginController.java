/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 8, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.admin.web.controller.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import com.kesdip.admin.web.controller.BaseFormController;
import com.kesdip.business.domain.admin.generated.User;
import com.kesdip.business.exception.ValidationException;
import com.kesdip.business.logic.UserLogic;

/**
 * Controller for user login.
 * 
 * @author gerogias
 */
public class LoginController extends BaseFormController {

	/**
	 * Serialization version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Performs user login, puts the user instance in the session and forwards
	 * to the container's authentication.
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

		User user = (User) command;
		UserLogic logic = getLogicFactory().getUserLogic();
		try {
			User dbUser = logic.doLogin(user);
			request.getSession().setAttribute("user", dbUser);
			// forward to container authentication
			request.setAttribute("userName", StringEscapeUtils.escapeHtml(user
					.getUsername()));
			request.setAttribute("password", StringEscapeUtils.escapeHtml(user
					.getPassword()));
			request.getRequestDispatcher("/public/loginProxy.jsp").forward(
					request, response);
		} catch (ValidationException ve) {
			return handleErrors(request, response, ve, user);
		} catch (Exception e) {
			return handleErrors(request, response, e);
		}
		return new ModelAndView(getSuccessView());
	}
}
