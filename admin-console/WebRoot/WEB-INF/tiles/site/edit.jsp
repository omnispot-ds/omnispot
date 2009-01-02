<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="controls" uri="http://www.kesdip.com/admin-console/taglib"%>

<fmt:setBundle basename="messages" scope="application" var="msg" />

<div id="form_container">

	<form:form id="form"
		action="${pageContext.request.contextPath}/secure/site/edit.do"
		commandName="dataObject" method="post" cssClass="appnitro">

		<form:hidden path="id" />

		<div class="form_description">
			<h2>
				<fmt:message bundle="${msg}" key="site.edit.title" />
			</h2>
			<p>
				<fmt:message bundle="${msg}" key="site.edit.description" />
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
					<fmt:message bundle="${msg}" key="site.customer" />
				</label>
				<div class="readonly_value">
					${dataObject.customer.name}
				</div>
			</li>
			</c:if>
			<!-- Name -->
			<li id="li_name">
				<label class="description" for="name">
					<fmt:message bundle="${msg}" key="site.name" />
				</label>
				<div>
					<form:input id="name" path="name" cssClass="element text medium"
						maxlength="50" size="25" />
				</div>
				<p class="guidelines" id="guide_name">
					<small> <fmt:message bundle="${msg}"
							key="site.name.help" /> </small>
				</p>
				<form:errors path="name" cssClass="error_message_title" />
			</li>
			<!-- Comments -->
			<li id="li_comments">
				<label class="description" for="comments">
					<fmt:message bundle="${msg}" key="site.comments" />
				</label>
				<div>
					<form:textarea id="comments" path="comments"
						cssClass="element textarea medium" />
				</div>
				<p class="guidelines" id="guide_comments">
					<small> <fmt:message bundle="${msg}" key="site.comments.help" />
					</small>
				</p>
				<form:errors path="comments" cssClass="error_message_title" />
			</li>

			<li class="buttons">
				<input id="submit" class="button_text" type="submit" name="submit"
					value='<fmt:message bundle="${msg}" key="button.update"/>' />
				&nbsp;
				<input id="reset" class="button_text" type="reset" name="reset"
					value='<fmt:message bundle="${msg}" key="button.reset"/>' />
			</li>
		</ul>
	</form:form>
</div>
