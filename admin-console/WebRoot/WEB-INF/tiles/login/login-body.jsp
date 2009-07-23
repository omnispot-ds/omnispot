<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<fmt:setBundle basename="messages" scope="application" var="msg" />

<div id="form_container">

	<form:form id="form" 
		action="${pageContext.request.contextPath}/login.do" 
		commandName="dataObject" method="post"
		cssClass="appnitro">
		<div class="form_description">
			<h2>
				<fmt:message bundle="${msg}" key="login.title" />
			</h2>
			<p>
				<fmt:message bundle="${msg}" key="login.description" />
			</p>
			<p class="error_message_title">
				<form:errors/>
			</p>
		</div>
		<ul>
			<li id="li_username">
				<label class="description" for="username">
					<fmt:message bundle="${msg}" key="login.username" />
				</label>
				<div>
					<input id="username" name="username" class="element text medium"
						type="text" maxlength="50" size="25" />
				</div>
				<p class="guidelines" id="guide_username">
					<small> <fmt:message bundle="${msg}"
							key="login.username.help" />
					</small>
				</p>
				<form:errors path="username" cssClass="error_message_title"/>				
			</li>
			<li id="li_password">
				<label class="description" for="password">
					<fmt:message bundle="${msg}" key="login.password" />
				</label>
				<div>
					<input id="password" name="password" class="element text medium"
						type="password" maxlength="50" size="25" />
				</div>
				<p class="guidelines" id="guide_password">
					<small> <fmt:message bundle="${msg}"
							key="login.password.help" /> 
					</small>
				</p>
				<form:errors path="password" cssClass="error_message_title"/>
			</li>
			<li class="buttons">
				<input id="submit" class="button_text" type="submit" name="submit"
					value='<fmt:message bundle="${msg}" key="button.connect"/>' />
				&nbsp;
				<input id="reset" class="button_text" type="reset" name="reset"
					value='<fmt:message bundle="${msg}" key="button.reset"/>' />
			</li>
		</ul>
	</form:form>
</div>