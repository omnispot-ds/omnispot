<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="controls" uri="http://www.kesdip.com/admin-console/taglib"%>

<fmt:setBundle basename="messages" scope="application" var="msg" />

<div id="form_container">

	<div class="tab-navigation">
		<ul class="tabs">
			<li>
			 	<a href="${pageContext.request.contextPath}/secure/installation/view.do?id=${param['installation.id']}">
					<fmt:message bundle="${msg}" key="installation.tab.view"/>
				</a>
			 </li>
			 <li>
			 	<a href="${pageContext.request.contextPath}/secure/installation/view-actions.do?installation.id=${param['installation.id']}">
					<fmt:message bundle="${msg}" key="installation.tab.actions"/>
				</a>
			</li>
			 <li class="selected_tab">
				<fmt:message bundle="${msg}" key="installation.tab.log"/>
			</li>
		</ul>
	</div>

	<form:form id="form" 
		action="${pageContext.request.contextPath}/secure/instalaltionGroup/view-log.do"
		commandName="dataObject" method="post" cssClass="appnitro">

		<input type="hidden" name="installation.id" value="${param['installation.id']}" />

		<div class="form_description">
			<h2>
				${dataObject.entityName}
			</h2>
		</div>

		<c:if test="${empty dataObject.logAction}">
			<span class="maxWidth">
				<span class="description">
					<fmt:message bundle="${msg}" key="installation.noLog" />
				</span>
			</span>
		</c:if>

		<c:if test="${not empty dataObject.logAction}">
			<table width="100%">
				<tr>
					<td class="label">
						<label class="description">
							${dataObject.logAction.dateString} 
							<sub><fmt:message bundle="${msg}" key="installation.log.100lines"/></sub>
						</label>
					</td>
				</tr>
				<tr>
					<td>
						<div style="height: 250px; width: 550px; overflow: scroll;">
							<pre>${dataObject.logAction.message}</pre>
						</div>
					</td>
				</tr>
			</table>
		</c:if>
	</form:form>
</div>
