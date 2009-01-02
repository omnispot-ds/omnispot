<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="controls" uri="http://www.kesdip.com/admin-console/taglib"%>

<fmt:setBundle basename="messages" scope="application" var="msg" />

<div id="form_container">

	<form:form id="form"
		action="${pageContext.request.contextPath}/secure/site/delete.do"
		commandName="dataObject" method="post" cssClass="appnitro">

		<form:hidden path="id" />

		<div class="form_description">
			<h2>
				<fmt:message bundle="${msg}" key="site.view.title" />
			</h2>
			<p>
				<fmt:message bundle="${msg}" key="site.view.description" />
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
						<fmt:message bundle="${msg}" key="site.customer" />
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
						<fmt:message bundle="${msg}" key="site.name" />
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
						<fmt:message bundle="${msg}" key="site.comments" />
					</label>
				</td>
				<td>
					<div class="readonly_value">
						${dataObject.comments}
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
					<a href="${pageContext.request.contextPath}/secure/installation/create.do?site.id=${dataObject.id}">
						<fmt:message bundle="${msg}" key="button.create.installation"/>
					</a>
					&nbsp;
					<a href="${pageContext.request.contextPath}/secure/site/edit.do?id=${dataObject.id}">
						<fmt:message bundle="${msg}" key="button.edit"/>
					</a>
					&nbsp;
					<a href="#" onclick="return confirmDelete('<fmt:message bundle="${msg}" key="site.delete.prompt"/>')">
						<fmt:message bundle="${msg}" key="button.delete"/>
					</a>
				</td>
			</tr>
		</table>
	</form:form>
</div>
