/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 8, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.admin.web.controller.user;

import javax.servlet.http.HttpServletRequest;

import com.kesdip.admin.web.controller.BaseFormController;
import com.kesdip.business.domain.admin.generated.User;
import com.kesdip.business.logic.UserLogic;

/**
 * Controller for the User viewing form.
 * 
 * @author gerogias
 */
public class ViewController extends BaseFormController {

	/**
	 * Serialization version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates the form-backing object from the DB.
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {

		User user = new User();
		user.setUsername(request.getParameter("id"));

		UserLogic logic = getLogicFactory().getUserLogic();
		User dbUser = logic.getInstance(user);
		setCurrentObject(request, dbUser);
		return dbUser;
	}
}
