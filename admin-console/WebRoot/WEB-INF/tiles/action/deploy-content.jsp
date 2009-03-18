<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="controls" uri="http://www.kesdip.com/admin-console/taglib"%>

<fmt:setBundle basename="messages" scope="application" var="msg" />

<div id="form_container">

	<form:form id="form"
		action="${pageContext.request.contextPath}/secure/action/deploy-content.do"
		commandName="dataObject" method="post" cssClass="appnitro" 
		enctype="multipart/form-data">
		
		<c:if test="${not empty dataObject.customer}">
			<form:hidden path="customer.id" />
		</c:if>
		<c:if test="${not empty dataObject.installationGroup}">
			<form:hidden path="installationGroup.id" />
		</c:if>
		<c:if test="${not empty dataObject.site}">
			<form:hidden path="site.id" />
		</c:if>
		<c:if test="${not empty dataObject.installation}">
			<form:hidden path="installation.id" />
		</c:if>

		<div class="form_description">
			<h2>
				<fmt:message bundle="${msg}" key="deploy.content.title" />
			</h2>
			<p>
				<fmt:message bundle="${msg}" key="deploy.content.description" />
			</p>
			<p class="error_message_title">
				<form:errors />
			</p>
		</div>
		<ul>
			<!-- Customer -->
			<c:if test="${not empty dataObject.customer}">
			<li id="li_customer">
				<label class="description" >
					<fmt:message bundle="${msg}" key="deploy.content.customer" />
				</label>
				<div class="readonly_value">
					${dataObject.customer.name}
				</div>
			</li>
			</c:if>
			<!-- Site -->
			<c:if test="${not empty dataObject.site}">
			<li id="li_site">
				<label class="description" >
					<fmt:message bundle="${msg}" key="deploy.content.site" />
				</label>
				<div class="readonly_value">
					${dataObject.site.name}
				</div>
			</li>
			</c:if>
			<!-- Group -->
			<c:if test="${not empty dataObject.installationGroup}">
			<li id="li_group">
				<label class="description" >
					<fmt:message bundle="${msg}" key="deploy.content.group" />
				</label>
				<div class="readonly_value">
					${dataObject.installationGroup.name}
				</div>
			</li>
			</c:if>
			<!-- Installation -->
			<c:if test="${not empty dataObject.installation}">
			<li id="li_installation">
				<label class="description" >
					<fmt:message bundle="${msg}" key="deploy.content.installation" />
				</label>
				<div class="readonly_value">
					${dataObject.installation.name}
				</div>
			</li>
			</c:if>
			<!-- Installation count -->
			<c:if test="${empty dataObject.installation}">
			<li id="li_count">
				<label class="description" >
					<fmt:message bundle="${msg}" key="deploy.installation.count" />
				</label>
				<div class="readonly_value">
					${dataObject.installationCount}
				</div>
			</li>
			</c:if>
			<li id="li_name">
				<label class="description" for="name">
					<fmt:message bundle="${msg}" key="deploy.name" />
				</label>
				<div>
					<input type="text" id="name" name="name"
						cssClass="element text" maxlength="50" size="25" />
				</div>
				<p class="guidelines" id="guide_name">
					<small> <fmt:message bundle="${msg}"
							key="deploy.name.help" /> </small>
				</p>
				<form:errors path="name" cssClass="error_message_title" />
			</li>
			<li id="li_contentFile">
				<label class="description" for="contentFile">
					<fmt:message bundle="${msg}" key="deploy.content.file" />
				</label>
				<div>
					<input type="file" id="contentFile" name="contentFile"
						cssClass="element text medium" size="40" />
				</div>
				<p class="guidelines" id="guide_contentFile">
					<small> <fmt:message bundle="${msg}"
							key="deploy.content.file.help" /> </small>
				</p>
				<form:errors path="contentFile" cssClass="error_message_title" />
			</li>
			<li id="li_policy">
				<label class="description" for="policy">
					<fmt:message bundle="${msg}" key="deploy.content.policy" />
				</label>
				<div>
					<form:select cssClass="element select large" multiple="false" 
					id="policy" path="policy">
						<form:option value="0"><fmt:message bundle="${msg}" key="deploy.content.policy.keep" /></form:option>
						<form:option value="1"><fmt:message bundle="${msg}" key="deploy.content.policy.overwrite" /></form:option>
						<form:option value="2"><fmt:message bundle="${msg}" key="deploy.content.policy.crc" /></form:option>
					</form:select>
				</div>
				<p class="guidelines" id="guide_policy">
					<small> <fmt:message bundle="${msg}" key="deploy.content.policy.help" />
					</small>
				</p>
				<form:errors path="policy" cssClass="error_message_title" />
			</li>

			<li class="buttons">
				<input id="submit" class="button_text" type="submit" name="submit"
					value='<fmt:message bundle="${msg}" key="button.deploy"/>' />
				&nbsp;
				<input id="reset" class="button_text" type="reset" name="reset"
					value='<fmt:message bundle="${msg}" key="button.reset"/>' />
			</li>
		</ul>
	</form:form>
</div>
