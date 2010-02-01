/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 8, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.admin.web.controller.customer;

import javax.servlet.http.HttpServletRequest;

import com.kesdip.admin.web.controller.BaseFormController;
import com.kesdip.business.domain.admin.generated.Customer;
import com.kesdip.business.logic.CustomerLogic;

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

		Customer customer = new Customer();
		customer.setId(Long.valueOf(request.getParameter("id")));

		CustomerLogic logic = getLogicFactory().getCustomerLogic();
		Customer dbCustomer = logic.getInstance(customer);
		setCurrentObject(request, dbCustomer);
		return dbCustomer;
	}
}
