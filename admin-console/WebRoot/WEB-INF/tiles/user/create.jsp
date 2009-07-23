<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="controls" uri="http://www.kesdip.com/admin-console/taglib"%>

<fmt:setBundle basename="messages" scope="application" var="msg" />

<div id="form_container">

	<form:form id="form"
		action="${pageContext.request.contextPath}/secure/user/create.do"
		commandName="dataObject" method="post" cssClass="appnitro">

		<form:hidden path="affiliation.id"/>
		
		<div class="form_description">
			<h2>
				<fmt:message bundle="${msg}" key="user.create.title" />
			</h2>
			<p>
				<fmt:message bundle="${msg}" key="user.create.description" />
			</p>
			<p class="error_message_title">
				<form:errors />
			</p>
		</div>
		<ul>
			<!-- Customer -->
			<c:if test="${not empty dataObject.affiliation}">
			<li id="li_customer">
				<label class="description" >
					<fmt:message bundle="${msg}" key="user.customer" />
				</label>
				<div class="readonly_value">
					${dataObject.affiliation.name}
				</div>
			</li>
			</c:if>
			<!-- Username -->
			<li id="li_username">
				<label class="description" for="username">
					<fmt:message bundle="${msg}" key="user.username" />
				</label>
				<div>
					<form:input id="username" path="username" cssClass="element text medium"
						maxlength="50" size="25" />
				</div>
				<p class="guidelines" id="guide_username">
					<small> <fmt:message bundle="${msg}"
							key="user.username.help" /> </small>
				</p>
				<form:errors path="username" cssClass="error_message_title" />
			</li>
			<!-- Password -->
			<li id="li_password">
				<label class="description" for="password">
					<fmt:message bundle="${msg}" key="user.password" />
				</label>
				<div>
					<form:password id="password" path="password" cssClass="element password medium"
						maxlength="50" size="25" />
				</div>
				<p class="guidelines" id="guide_password">
					<small> <fmt:message bundle="${msg}"
							key="user.password.help" /> </small>
				</p>
				<form:errors path="password" cssClass="error_message_title" />
			</li>
			<!-- Last name -->
			<li id="li_lastName">
				<label class="description" for="lastName">
					<fmt:message bundle="${msg}" key="user.lastName" />
				</label>
				<div>
					<form:input id="lastName" path="lastName"
						cssClass="element text medium" maxlength="50" size="25" />
				</div>
				<p class="guidelines" id="guide_lastName">
					<small> <fmt:message bundle="${msg}" key="user.lastName.help" />
					</small>
				</p>
				<form:errors path="lastName" cssClass="error_message_title" />
			</li>
			<!-- First name -->
			<li id="li_firstName">
				<label class="description" for="firstName">
					<fmt:message bundle="${msg}" key="user.firstName" />
				</label>
				<div>
					<form:input id="firstName" path="firstName"
						cssClass="element text medium" maxlength="50" size="25" />
				</div>
				<p class="guidelines" id="guide_firstName">
					<small> <fmt:message bundle="${msg}" key="user.firstName.help" />
					</small>
				</p>
				<form:errors path="firstName" cssClass="error_message_title" />
			</li>
			<!-- Rights -->
			<li id="li_rights">
				<label class="description" for="rights">
					<fmt:message bundle="${msg}" key="user.rights" />
				</label>
				<div>
					<form:select cssClass="element select medium" id="rights"
						path="rights" multiple="true" size="3">
						<form:options items="${listGenerator.accessRights}" itemValue="name" itemLabel="name"/>
					</form:select>
				</div>
				<p class="guidelines" id="guide_rights">
					<small> <fmt:message bundle="${msg}"
							key="user.rights.help" /> </small>
				</p>
				<form:errors path="rights" cssClass="error_message_title" />
			</li>

			<li class="buttons">
				<input id="submit" class="button_text" type="submit" name="submit"
					value='<fmt:message bundle="${msg}" key="button.create"/>' />
				&nbsp;
				<input id="reset" class="button_text" type="reset" name="reset"
					value='<fmt:message bundle="${msg}" key="button.reset"/>' />
			</li>
		</ul>
	</form:form>
</div>
