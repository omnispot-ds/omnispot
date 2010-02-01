/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Feb 2, 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.logic;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.kesdip.business.domain.admin.generated.Content;

/**
 * Content-related logic.
 * 
 * @author gerogias
 */
public class ContentLogic extends BaseLogic {

	/**
	 * @return Content the content with the given url or <code>null</code>
	 */
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public Content getContentByUrl(String url) {
		List<Content> results = getHibernateTemplate().find(
				"select c from " + Content.class.getName() + " c "
						+ "where c.url = ?", url);
		return !results.isEmpty() ? results.get(0) : null;
	}
}
