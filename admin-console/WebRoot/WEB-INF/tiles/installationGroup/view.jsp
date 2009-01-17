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
			<li class="selected_tab">
				<fmt:message bundle="${msg}" key="group.tab.view"/>
			 </li>
			 <li>
			 	<a href="${pageContext.request.contextPath}/secure/installationGroup/view-images.do?installationGroup.id=${dataObject.id}">
					<fmt:message bundle="${msg}" key="group.tab.images"/>
				</a>
			</li>
		</ul>
	</div>

	<form:form id="form"
		action="${pageContext.request.contextPath}/secure/installationGroup/delete.do"
		commandName="dataObject" method="post" cssClass="appnitro">

		<form:hidden path="id" />

		<div class="form_description">
			<h2>
				<fmt:message bundle="${msg}" key="group.view.title" />
			</h2>
			<p>
				<fmt:message bundle="${msg}" key="group.view.description" />
			</p>
			<p class="error_message_title">
				<form:errors />
			</p>
		</div>

		<table width="100%">
			<!-- Customer -->
			<c:if test="${not empty dataObject.customer}">
			<tr>
				<td class="label">
					<label class="description">
						<fmt:message bundle="${msg}" key="group.customer" />
					</label>
				</td>
				<td>
					<div class="readonly_value">
						${dataObject.customer.name}
					</div>
				</td>
			</tr>
			</c:if>
			
			<!-- Name -->
			<tr>
				<td class="label">
					<label class="description">
						<fmt:message bundle="${msg}" key="group.name" />
					</label>
				</td>
				<td>
					<div class="readonly_value">
						${dataObject.name}
					</div>
				</td>
			</tr>
			<!-- Comments -->
			<tr>
				<td class="label">
					<label class="description">
						<fmt:message bundle="${msg}" key="group.comments" />
					</label>
				</td>
				<td>
					<div class="readonly_value">
						${dataObject.comments}
					</div>
				</td>
			</tr>
			<!-- Installations -->
			<tr>
				<td class="label">
					<label class="description">
						<fmt:message bundle="${msg}" key="group.installations" />
					</label>
				</td>
				<td>
					<div class="readonly_value">
						<ul>
						<c:forEach items="${dataObject.installations}" var="installation">
							<li>${installation.name}</li>	
						</c:forEach>
						</ul>
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
					<a href="${pageContext.request.contextPath}/secure/deploy/content.do?installationGroup.id=${dataObject.id}">
						<fmt:message bundle="${msg}" key="button.deploy.content"/>
					</a>
					&nbsp;
					<a href="${pageContext.request.contextPath}/secure/installationGroup/edit.do?id=${dataObject.id}">
						<fmt:message bundle="${msg}" key="button.edit"/>
					</a>
					&nbsp;
					<a href="#" onclick="return confirmDelete('<fmt:message bundle="${msg}" key="group.delete.prompt"/>')">
						<fmt:message bundle="${msg}" key="button.delete"/>
					</a>
				</td>
			</tr>
		</table>
	</form:form>
</div>
