<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="controls" uri="http://www.kesdip.com/admin-console/taglib"%>

<fmt:setBundle basename="messages" scope="application" var="msg" />

<div id="form_container">

	<form:form id="form"
		action="${pageContext.request.contextPath}/secure/customer/delete.do"
		commandName="dataObject" method="post" cssClass="appnitro">

		<form:hidden path="id" />

		<div class="form_description">
			<h2>
				<fmt:message bundle="${msg}" key="customer.view.title" />
			</h2>
			<p>
				<fmt:message bundle="${msg}" key="customer.view.description" />
			</p>
			<p class="error_message_title">
				<form:errors />
			</p>
		</div>

		<table width="100%">
			<!-- Name -->
			<tr>
				<td class="label">
					<label class="description">
						<fmt:message bundle="${msg}" key="customer.name" />
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
						<fmt:message bundle="${msg}" key="customer.comments" />
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
					<a href="${pageContext.request.contextPath}/secure/user/create.do?affiliation.id=${dataObject.id}">
						<fmt:message bundle="${msg}" key="button.create.user"/>
					</a>
					&nbsp;
					<a href="${pageContext.request.contextPath}/secure/customer/edit.do?id=${dataObject.id}">
						<fmt:message bundle="${msg}" key="button.edit"/>
					</a>
					&nbsp;
					<a href="#" onclick="return confirmDelete('<fmt:message bundle="${msg}" key="customer.delete.prompt"/>')">
						<fmt:message bundle="${msg}" key="button.delete"/>
					</a>
				</td>
			</tr>
		</table>
	</form:form>
</div>
