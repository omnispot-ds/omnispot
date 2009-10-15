/*
 * Disclaimer:
 * Copyright 2008 - Panou E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Nov 4, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */
package com.kesdip.admin.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kesdip.admin.web.util.SimpleRequestWrapper;
import com.kesdip.admin.web.util.SimpleResponseWrapper;


/**
 * Makes sure that all traffic has the encoding properly set.
 * It reads the "encoding" filter parameter; if it is not present, 
 * it defaults to "UTF-8".
 * Wraps the request in a {@link SimpleRequestWrapper}.
 * @author gerogias
 */
public class EncodingFilter implements Filter {

	private String encoding = "UTF-8";
	
	/**
	 * Empty method.
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		
	}

	/**
	 * Wrap the request and set the encoding.
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
			ServletException {
		if (! (req instanceof SimpleRequestWrapper)) {
			req.setCharacterEncoding(encoding);
			res.setCharacterEncoding(encoding);
			SimpleRequestWrapper reqWrapper = new SimpleRequestWrapper((HttpServletRequest)req);
			SimpleResponseWrapper resWrapper = new SimpleResponseWrapper((HttpServletResponse)res);
			chain.doFilter(reqWrapper, resWrapper);
		} else {
			chain.doFilter(req, res);
		}
	}

	/**
	 * Sets the encoding field.
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig config) throws ServletException {
		String param = config.getInitParameter("encoding");
		if (param != null) {
			this.encoding = param;
		}
	}

}
