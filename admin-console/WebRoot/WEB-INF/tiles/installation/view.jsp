<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="controls" uri="http://www.kesdip.com/admin-console/taglib"%>

<fmt:setBundle basename="messages" scope="application" var="msg" />

<div id="form_container">

	<form:form id="form"
		action="${pageContext.request.contextPath}/secure/installation/delete.do"
		commandName="dataObject" method="post" cssClass="appnitro">

		<form:hidden path="id" />

		<div class="form_description">
			<h2>
				<fmt:message bundle="${msg}" key="installation.view.title" />
			</h2>
			<p>
				<fmt:message bundle="${msg}" key="installation.view.description" />
			</p>
			<p class="error_message_title">
				<form:errors />
			</p>
		</div>
		<table width="100%">
			<!-- Customer -->
			<tr>
				<td class="label">
					<label class="description">
						<fmt:message bundle="${msg}" key="installation.customer" />
					</label>
				</td>
				<td>
					<div class="readonly_value">
						${dataObject.site.customer.name}
					</div>
				</td>
			</tr>
			<!-- Site -->
			<tr>
				<td class="label">
					<label class="description">
						<fmt:message bundle="${msg}" key="installation.site" />
					</label>
				</td>
				<td>
					<div class="readonly_value">
						${dataObject.site.name}
					</div>
				</td>
			</tr>
			<!-- Name -->
			<tr>
				<td class="label">
					<label class="description">
						<fmt:message bundle="${msg}" key="installation.name" />
					</label>
				</td>
				<td>
					<div class="readonly_value">
						${dataObject.name}
					</div>
				</td>
			</tr>
			<!-- Status -->
			<tr>
				<td class="label">
					<label class="description">
						<fmt:message bundle="${msg}" key="installation.status" />
					</label>
				</td>
				<td>
					<div class="readonly_value">
						<c:choose>
							<c:when test="${dataObject.currentStatus == 0}">
								<fmt:message bundle="${msg}" key="status.ok" />
							</c:when>
							<c:when test="${dataObject.currentStatus == -1}">
								<fmt:message bundle="${msg}" key="status.playerDown" />
							</c:when>
							<c:when test="${dataObject.currentStatus == -2}">
								<fmt:message bundle="${msg}" key="status.machineDown" />
							</c:when>
						</c:choose>
					</div>
				</td>
			</tr>
			<!-- UUID -->
			<tr>
				<td class="label">
					<label class="description">
						<fmt:message bundle="${msg}" key="installation.uuid" />
					</label>
				</td>
				<td>
					<div class="readonly_value">
						${dataObject.uuid}
					</div>
				</td>
			</tr>
			<!-- Content -->
			<tr>
				<td class="label">
					<label class="description">
						<fmt:message bundle="${msg}" key="installation.content" />
					</label>
				</td>
				<td>
					<div class="readonly_value">
						<c:forEach items="${dataObject.deployments}" var="deployment" varStatus="status">
							${deployment.name}
							<c:if test="${not status.last}">
								<br/>							
							</c:if>
						</c:forEach>
					</div>
				</td>
			</tr>
			<!-- Screen type -->
			<tr>
				<td class="label">
					<label class="description">
						<fmt:message bundle="${msg}" key="installation.screen.type" />
					</label>
				</td>
				<td>
					<div class="readonly_value">
						${dataObject.screenType}
					</div>
				</td>
			</tr>
			<!-- Comments -->
			<tr>
				<td class="label">
					<label class="description">
						<fmt:message bundle="${msg}" key="installation.comments" />
					</label>
				</td>
				<td>
					<div class="readonly_value">
						${dataObject.comments}
					</div>
				</td>
			</tr>
			<!-- Printscreen -->
			<tr>
				<td class="label">
					<label class="description">
						<fmt:message bundle="${msg}" key="installation.lastImage" />
					</label>
				</td>
				<td>
					<div class="readonly_value">
						<a class="thumbnail" 
							href="#"><img 
								class="thumb" src="${dataObject.printScreen.fileUrl}" id="${dataObject.uuid}" 
								alt="${dataObject.printScreen.fileDateString}" 
								title="${dataObject.printScreen.fileDateString}"/><span><label 
									for="${dataObject.uuid}">${dataObject.name}</label><img 
									class="full" src="${dataObject.printScreen.fileUrl}" id="${dataObject.uuid}" 
								alt="${dataObject.printScreen.fileDateString}" 
								title="${dataObject.printScreen.fileDateString}"/></span></a>
					</div>
				</td>
			</tr>
			<!-- Buttons -->
			<tr>
				<td class="section_break" colspan="2">
					<h3>
						<fmt:message bundle="${msg}" key="title.actions" />
					</h3>
				</td>
			</tr>
			<tr>
				<td class="label" colspan="2">
					<a href="${pageContext.request.contextPath}/secure/installation/edit.do?id=${dataObject.id}">
						<fmt:message bundle="${msg}" key="button.edit"/></a>
					&nbsp;
					<a href="#" onclick="return confirmDelete('<fmt:message bundle="${msg}" key="installation.delete.prompt"/>')">
						<fmt:message bundle="${msg}" key="button.delete"/></a>
					<br/>
					<!-- Actions -->
					<a href="${pageContext.request.contextPath}/secure/action/deploy-content.do?installation.id=${dataObject.id}">
						<fmt:message bundle="${msg}" key="button.deploy.content"/></a>
					&nbsp;
					<a href="${pageContext.request.contextPath}/secure/action/schedule.do?action.type=4&installation.id=${dataObject.id}">
						<fmt:message bundle="${msg}" key="button.restart.installation"/></a>
					&nbsp;
					<a href="${pageContext.request.contextPath}/secure/action/schedule.do?action.type=2&installation.id=${dataObject.id}">
						<fmt:message bundle="${msg}" key="button.reboot.installation"/></a>
					&nbsp;
					<a href="${pageContext.request.contextPath}/secure/action/schedule.do?action.type=5&installation.id=${dataObject.id}">
						<fmt:message bundle="${msg}" key="button.reconf.installation"/></a>
					&nbsp;
					<a href="${pageContext.request.contextPath}/secure/action/schedule.do?action.type=6&installation.id=${dataObject.id}">
						<fmt:message bundle="${msg}" key="button.fetch.log"/></a>
					&nbsp;
				</td>
			</tr>
		</table>
	</form:form>
</div>
