<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:setBundle basename="messages" scope="application" var="msg" />

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title><fmt:message bundle="${msg}" key="frame.title" /></title>
		<link rel="stylesheet" type="text/css"
			href="${pageContext.request.contextPath}/resources/css/layout.css" />
		<link rel="stylesheet" type="text/css"
			href="${pageContext.request.contextPath}/resources/css/form.css" />
		<link rel="stylesheet" type="text/css"
			href="${pageContext.request.contextPath}/resources/css/chrome-menu.css" />
		<script type="text/javascript"
			src="${pageContext.request.contextPath}/resources/js/form.js"></script>
		<script type="text/javascript"
			src="${pageContext.request.contextPath}/resources/js/calendar.js"></script>
		<script type="text/javascript"
			src="${pageContext.request.contextPath}/resources/js/chrome.js"></script>
		<script type="text/javascript"
			src="${pageContext.request.contextPath}/resources/js/util.js"></script>
	</head>
	<body>

		<div id="outer">
			<div id="inner">

				<%-- The banner and header --%>
				<tiles:insert attribute="header" flush="false" />

				<div id="pagebody">
					<div id="leftcol">

						<%-- The navigation  tree on the left --%>
						<tiles:insert attribute="tree" flush="false" />

					</div>
					<div id="maincol">

						<%-- The body  --%>
						<tiles:insert attribute="body" flush="false" />

					</div>
				</div>

				<%-- The footer --%>
				<tiles:insert attribute="footer" flush="false" />
			</div>
		</div>
	</body>
</html>
