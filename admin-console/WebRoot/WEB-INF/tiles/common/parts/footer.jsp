<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="messages" scope="application" var="msg"/>

<div id="footer">
	<fmt:message bundle="${msg}" key="version"/> -
	<fmt:message bundle="${msg}" key="footer.copyright"/><br/>
	<fmt:message bundle="${msg}" key="footer.development"/>
</div>

