/*
 * Disclaimer:
 * Copyright 2008-2010 - Omni-Spot E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 18 Ιαν 2010
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.common.util.tomcat;

import java.io.InputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.log4j.Logger;

import com.kesdip.common.util.StringUtils;

/**
 * Wrapper class around the actions allowed by Tomcat's manager application.
 * 
 * @author gerogias
 */
public class ManagerWrapper {

	/**
	 * The logger.
	 */
	private static final Logger logger = Logger.getLogger(ManagerWrapper.class);

	/**
	 * The target manager.
	 */
	private TomcatManagerInfo mgrInfo = null;

	/**
	 * The target application.
	 */
	private ApplicationInfo appInfo = null;

	/**
	 * The proxy information, if any.
	 */
	private ProxyInfo proxyInfo = null;

	/**
	 * Default constructor.
	 * 
	 * @param mgrInfo
	 *            the manager
	 * @param appInfo
	 *            the target app
	 * @param proxyInfo
	 *            the proxy to use, ignored is <code>null</code>
	 */
	public ManagerWrapper(TomcatManagerInfo mgrInfo, ApplicationInfo appInfo,
			ProxyInfo proxyInfo) {
		this.mgrInfo = mgrInfo;
		this.appInfo = appInfo;
		this.proxyInfo = proxyInfo;
	}

	/**
	 * Prepare a configured instance of {@link HttpClient}.
	 * 
	 * @return HttpClient a configured instance
	 */
	private final HttpClient prepareHttpClient() {

		HttpClient httpClient = new HttpClient();
		httpClient.getParams().setAuthenticationPreemptive(true);
		httpClient.getState().setCredentials(
				AuthScope.ANY,
				new UsernamePasswordCredentials(mgrInfo.getUserName(), mgrInfo
						.getPassword()));
		if (proxyInfo != null) {
			httpClient.getHostConfiguration().setProxy(proxyInfo.getHost(),
					proxyInfo.getPort());
		}
		return httpClient;
	}

	/**
	 * Performs an undeploy operation of the application.
	 * 
	 * @throws IllegalStateException
	 *             on error
	 */
	public void undeployApp() throws IllegalStateException {

		HttpClient httpClient = prepareHttpClient();
		GetMethod undeploy = new GetMethod(getUndeployPath());
		if (logger.isDebugEnabled()) {
			logger.debug("Undeploy path: " + getUndeployPath());
		}
		try {
			httpClient.executeMethod(undeploy);

			// see
			// http://tomcat.apache.org/tomcat-5.5-doc/manager-howto.html#Undeploy%20an%20Existing%20Application
			if (undeploy.getResponseBodyAsString().startsWith("FAIL")) {
				logger.error("Error undeploying " + appInfo.getContextPath()
						+ ": " + undeploy.getResponseBodyAsString());
				throw new IllegalStateException("Error undeploying "
						+ appInfo.getContextPath() + ": "
						+ undeploy.getResponseBodyAsString());
			}
		} catch (IllegalStateException ise) {
			throw ise;
		} catch (Exception e) {
			logger.error("Error undeploying " + appInfo.getContextPath(), e);
			throw new IllegalStateException("Error undeploying "
					+ appInfo.getContextPath(), e);
		} finally {
			undeploy.releaseConnection();
		}
	}

	/**
	 * Deploys the WAR on the server. It marks the new deployment with the
	 * latest version as tag. In case of failure and if a previous tag has been
	 * defined, tries to redeploy the previous tag.
	 * 
	 * @param warStream
	 *            the loaded WAR
	 * @throws IllegalStateException
	 *             on error
	 */
	public void deployAdminConsole(InputStream warStream)
			throws IllegalStateException {

		HttpClient httpClient = prepareHttpClient();
		PutMethod deploy = new PutMethod(getNewTagDeployPath());
		deploy.setRequestEntity(new InputStreamRequestEntity(warStream));
		try {
			httpClient.executeMethod(deploy);
			// see
			// http://tomcat.apache.org/tomcat-5.5-doc/manager-howto.html#Deploy%20A%20New%20Application%20Remotely
			if (deploy.getResponseBodyAsString().startsWith("FAIL")) {
				logger.error(appInfo.getContextPath()
						+ ": Failed to deploy version " + appInfo.getNewTag()
						+ ": " + deploy.getResponseBodyAsString());
				throw new IllegalStateException(appInfo.getContextPath()
						+ ": Failed to deploy version " + appInfo.getNewTag()
						+ ": " + deploy.getResponseBodyAsString());
			}
		} catch (IllegalStateException ise) {
			if (!StringUtils.isEmpty(appInfo.getPreviousTag())) {
				redeployPreviousTag(httpClient);
			}
			throw ise;
		} catch (Exception e) {
			if (!StringUtils.isEmpty(appInfo.getPreviousTag())) {
				redeployPreviousTag(httpClient);
			}
			logger.error(appInfo.getContextPath()
					+ ": Error deploying version " + appInfo.getNewTag(), e);
			throw new IllegalStateException(appInfo.getContextPath()
					+ ": Error deploying version " + appInfo.getNewTag(), e);
		} finally {
			deploy.releaseConnection();
		}
	}

	/**
	 * Performs a redeploy action using the previousVersion as tag.
	 * 
	 * @param httpClient
	 *            the initialized HTTP client
	 * @throws IllegalStateException
	 *             on error
	 */
	private final void redeployPreviousTag(HttpClient httpClient)
			throws IllegalStateException {
		try {
			GetMethod redeploy = new GetMethod(getPreviousTagDeployPath());
			if (logger.isDebugEnabled()) {
				logger.debug("Redeploying previous tag: "
						+ appInfo.getPreviousTag());
			}
			httpClient.executeMethod(redeploy);
			if (redeploy.getResponseBodyAsString().startsWith("FAIL")) {
				logger.warn(appInfo.getContextPath()
						+ ": Failed to restore version "
						+ appInfo.getPreviousTag());
			} else {
				if (logger.isInfoEnabled()) {
					logger.info(appInfo.getContextPath() + ": Version "
							+ appInfo.getPreviousTag() + " restored OK");
				}
			}
		} catch (Exception e1) {
			logger.warn(appInfo.getContextPath() + ": Error restoring version "
					+ appInfo.getPreviousTag(), e1);
		}
	}

	/**
	 * @return String the full undeploy path
	 */
	public final String getUndeployPath() {
		return mgrInfo.getUndeployUrl() + "?path=" + appInfo.getContextPath();
	}

	/**
	 * @return String the deploy path
	 */
	public final String getNewTagDeployPath() {
		return mgrInfo.getDeployUrl() + "?path=" + appInfo.getContextPath()
				+ "&tag=" + appInfo.getNewTag();
	}

	/**
	 * @return String the deploy path
	 */
	public final String getPreviousTagDeployPath() {
		return mgrInfo.getDeployUrl() + "?path=" + appInfo.getContextPath()
				+ "&tag=" + appInfo.getPreviousTag();
	}

}
