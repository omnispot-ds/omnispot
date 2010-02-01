<%@ page language="java" contentType="text/html;charset=UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:setBundle basename="messages" scope="application" var="msg" />

<html>
	<head>
		<title><fmt:message bundle="${msg}" key="frame.title" /></title>
	</head>
	<body onload="document.forms[0].submit()">

		<div style="text-align: center">
			<p>
				<fmt:message bundle="${msg}" key="application.connecting" />
			</p>
			<p>
				<img
					src="${pageContext.request.contextPath}/resources/images/login_loading.gif">
			</p>
		</div>

		<form method="post" action="j_security_check">
			<input type="hidden" name="j_username"
				value="${requestScope.userName}" />
			<input type="hidden" name="j_password"
				value="${requestScope.password}" />
		</form>
		<div id="footer">
			<fmt:message bundle="${msg}" key="version"/> -
			<fmt:message bundle="${msg}" key="footer.copyright"/><br/>
			<fmt:message bundle="${msg}" key="footer.development"/>
		</div>		
	</body>
</html>
