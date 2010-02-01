/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Feb 4, 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.admin.web.controller.installation;

import javax.servlet.http.HttpServletRequest;

import com.kesdip.admin.web.controller.BaseFormController;
import com.kesdip.business.beans.ActionQueryBean;
import com.kesdip.business.domain.admin.generated.Installation;
import com.kesdip.business.logic.ActionLogic;

/**
 * Controller for the log viewing form.
 * 
 * @author gerogias
 */
public class ViewLogController extends BaseFormController {

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

		ActionQueryBean bean = (ActionQueryBean) super
				.formBackingObject(request);

		String installationId = request.getParameter("installation.id");

		Installation installation = new Installation();
		installation.setId(Long.valueOf(installationId));
		bean.setInstallation(installation);
		setCurrentObject(request, installation);
		
		ActionLogic logic = getLogicFactory().getActionLogic();
		bean = logic.getLogEntry(bean);
		return bean;
	}

}
