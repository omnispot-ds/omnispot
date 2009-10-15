<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isErrorPage="true"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:setBundle basename="messages" scope="application" var="msg" />

<div class="panel">
	<div class="heading">
		<fmt:message key="errorPage.header" bundle="${msg}"/>
	</div>
	
	<fmt:message key="errorPage.application.execution" bundle="${msg}"/> <br/>
	
	<fmt:message key="errorPage.link.prompt" bundle="${msg}"/> 
		<a href="${pageContext.request.contextPath}">
		<fmt:message key="errorPage.link.text" bundle="${msg}"/>
		</a>
	<br/><br/>
	<fmt:message key="errorPage.message" bundle="${msg}"/> <br/>

	<code>
	<c:choose>
		<c:when test="${!empty requestScope['javax.servlet.error.exception']}">
			<c:set var="e" value="${requestScope['javax.servlet.error.exception']}"/>
			<c:out value="${e.class.name} ${e.message}"/>
			<c:forEach var="st1" items="${e.stackTrace}">
				&nbsp;&nbsp;&nbsp;<c:out value="${st1}"/>
			</c:forEach>
			<c:if test="${!empty e.cause}">
				<c:set var="c" value="${e.cause}"/>
				&nbsp;&nbsp;&nbsp;<c:out value="${c.class.name} ${c.message}"/>
				<c:forEach var="st2" items="${c.stackTrace}">
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<c:out value="${st2}"/>
				</c:forEach>
				<%-- 
				<c:if test="${!empty c.cause}">
					<c:set var="d" value="${c.cause}"/>
					<c:out value="${d.class.name} ${d.message}"/>
					<c:forEach var="st3" items="${d.stackTrace}">
						<c:out value="${st3}"/>
					</c:forEach>
				</c:if>
				--%>
			</c:if>
		</c:when>
		<c:otherwise>
			Unknown Error
		</c:otherwise>
	</c:choose>
	</code>
</div>
