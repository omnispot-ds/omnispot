/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Jan 19, 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.validation.action;

import com.kesdip.business.beans.ActionBean;
import com.kesdip.business.constenum.IActionTypesEnum;
import com.kesdip.business.domain.admin.generated.Parameter;
import com.kesdip.business.util.Errors;
import com.kesdip.business.validation.BaseValidator;
import com.kesdip.common.util.StringUtils;

/**
 * Validator for simple actions.
 * 
 * @author gerogias
 */
public class SimpleActionValidator extends BaseValidator {

	/**
	 * Check if
	 * <ul>
	 * <li>there is a parent object</li>
	 * <li>in case of {@link IActionTypesEnum#RECONFIGURE}, all parameters
	 * have a name/value</li>
	 * </ul>
	 * 
	 * @see com.kesdip.business.validation.Validator#validate(java.lang.Object,
	 *      com.kesdip.business.util.Errors)
	 */
	public void validate(Object obj, Errors errors) {
		
		ActionBean bean = (ActionBean) obj;
		// check null parent objects
		if (bean.getCustomer() == null && bean.getSite() == null
				&& bean.getInstallationGroup() == null
				&& bean.getInstallation() == null) {
			errors.addError("error.invalid.parent");
		}
		boolean reconfParamFailed = false;
		// check parameters
		if (IActionTypesEnum.RECONFIGURE == bean.getAction().getType()) {
			for (Parameter parameter : bean.getAction().getParameters()) {
				if (StringUtils.isEmpty(parameter.getName())
						|| StringUtils.isEmpty(parameter.getValue())) {
					if (!reconfParamFailed) {
						errors.addError("error.actionParameter.values");
						reconfParamFailed = true;
					}
				}
			}
		}
	}

}
