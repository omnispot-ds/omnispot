<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="controls" uri="http://www.kesdip.com/admin-console/taglib"%>

<fmt:setBundle basename="messages" scope="application" var="msg" />

<div id="form_container">

	<form:form id="form"
		action="${pageContext.request.contextPath}/secure/user/delete.do"
		commandName="dataObject" method="post" cssClass="appnitro">

		<form:hidden path="username" />
		<!-- The following hidden is needed for the delete action -->
		<input type="hidden" name="id" value="${dataObject.username}"/>

		<div class="form_description">
			<h2>
				<fmt:message bundle="${msg}" key="user.view.title" />
			</h2>
			<p>
				<fmt:message bundle="${msg}" key="user.view.description" />
			</p>
			<p class="error_message_title">
				<form:errors />
			</p>
		</div>

		<table width="100%">
			<!-- Customer -->
			<c:if test="${not empty dataObject.affiliation}">
			<tr>
				<td class="label">
					<label class="description">
						<fmt:message bundle="${msg}" key="user.customer" />
					</label>
				</td>
				<td>
					<div class="readonly_value">
						${dataObject.affiliation.name}
					</div>
				</td>
			</tr>
			</c:if>
			<!-- Username -->
			<tr>
				<td class="label">
					<label class="description">
						<fmt:message bundle="${msg}" key="user.username" />
					</label>
				</td>
				<td>
					<div class="readonly_value">
						${dataObject.username}
					</div>
				</td>
			</tr>
			<!-- first name -->
			<tr>
				<td class="label">
					<label class="description">
						<fmt:message bundle="${msg}" key="user.firstName" />
					</label>
				</td>
				<td>
					<div class="readonly_value">
						${dataObject.firstName}
					</div>
				</td>
			</tr>
			<!-- Last name -->
			<tr>
				<td class="label">
					<label class="description">
						<fmt:message bundle="${msg}" key="user.lastName" />
					</label>
				</td>
				<td>
					<div class="readonly_value">
						${dataObject.lastName}
					</div>
				</td>
			</tr>
			<!-- Rights -->
			<tr>
				<td class="label">
					<label class="description">
						<fmt:message bundle="${msg}" key="user.rights" />
					</label>
				</td>
				<td>
					<div class="readonly_value">
						<ul>
						<c:forEach items="${dataObject.rights}" var="right">
							<li>${right.name}</li>	
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
					<a href="${pageContext.request.contextPath}/secure/user/edit.do?id=${dataObject.username}">
						<fmt:message bundle="${msg}" key="button.edit"/>
					</a>
					&nbsp;
					<a href="#" onclick="return confirmDelete('<fmt:message bundle="${msg}" key="user.delete.prompt"/>')">
						<fmt:message bundle="${msg}" key="button.delete"/>
					</a>
				</td>
			</tr>
		</table>
	</form:form>
</div>
