<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<fmt:setBundle basename="messages" scope="application" var="msg"/>

<div id="banner">
<!-- 	<fmt:message bundle="${msg}" key="application.title"/> -->
</div>
<div class="heading-right">

 	<form:form id="searchForm"
		action="${pageContext.request.contextPath}/secure/quickSearch.do"
		commandName="searchDataObject" method="post"
		cssClass="searchForm">
		<label for="latitude">
			<fmt:message bundle="${msg}" key="menu.quick.search" />
		</label> 
		<form:input id="name" path="name" cssClass="element text tiny" />
		${user.firstName} ${user.lastName}
	</form:form>
</div>
<div class="heading">
	
	<!-- Menu headers -->
	<div class="chromestyle" id="headermenu">
		<ul>
			<li><a href="#" rel="data_menu">
				<fmt:message key="menu.manage" bundle="${msg}"/>
			</a></li>
		</ul>
	</div>

	<!-- Data menu -->                                                   
	<div id="data_menu" class="dropmenudiv">
		<a href="${pageContext.request.contextPath}/secure/home.do">
			<fmt:message key="menu.manage.sites" bundle="${msg}"/>
		</a>
		<a href="${pageContext.request.contextPath}/secure/group-home.do">
			<fmt:message key="menu.manage.groups" bundle="${msg}"/>
		</a>
		<a href="${pageContext.request.contextPath}/secure/user-home.do">
			<fmt:message key="menu.manage.users" bundle="${msg}"/>
		</a>
	</div>

	<script type="text/javascript">
		cssdropdown.startchrome("headermenu");
	</script>
</div>


