<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="controls" uri="http://www.kesdip.com/admin-console/taglib"%>

<fmt:setBundle basename="messages" scope="application" var="msg" />

<div id="form_container">

	<div class="tab-navigation">
		<ul class="tabs">
			<li>
			 	<a href="${pageContext.request.contextPath}/secure/site/view.do?id=${dataObject.id}">
					<fmt:message bundle="${msg}" key="site.tab.view"/>
				</a>
			 </li>
			 <li class="selected_tab">
				<fmt:message bundle="${msg}" key="site.tab.images"/>
			</li>
		</ul>
	</div>

	<form:form id="form" 
		action="${pageContext.request.contextPath}/secure/site/viewPs.do"
		commandName="dataObject" method="post" cssClass="appnitro">

		<form:hidden path="id" />

		<div class="form_description">
			<h2>
				<fmt:message bundle="${msg}" key="site.printscreen.title" />
			</h2>
			<button>Recycle</button>
		</div>

		<div style="overflow: visible; width: 100%">
		
			<div style="width: 50; height: 50; background-color: red;">
			</div>
		
			<div style="width: 50; height: 50; background-color: red;">
			</div>
			<div style="width: 50; height: 50; background-color: red;">
			</div>
			<div style="width: 50; height: 50; background-color: red;">
			</div>
			<div style="width: 50; height: 50; background-color: red;">
			</div>
			<div style="width: 50; height: 50; background-color: red;">
			</div>
			<div style="width: 50; height: 50; background-color: red;">
			</div>
			<div style="width: 50; height: 50; background-color: red;">
			</div>
			<div style="width: 50; height: 50; background-color: red;">
			</div>
			<div style="width: 50; height: 50; background-color: red;">
			</div>
			<div style="width: 50; height: 50; background-color: red;">
			</div>
			<div style="width: 50; height: 50; background-color: red;">
			</div>
			<div style="width: 50; height: 50; background-color: red;">
			</div>
			<div style="width: 50; height: 50; background-color: red;">
			</div>
			<div style="width: 50; height: 50; background-color: red;">
			</div>
			<div style="width: 50; height: 50; background-color: red;">
			</div>
			<div style="width: 50; height: 50; background-color: red;">
			</div>
			<div style="width: 50; height: 50; background-color: red;">
			</div>
			<div style="width: 50; height: 50; background-color: red;">
			</div>
			<div style="width: 50; height: 50; background-color: red;">
			</div>
		</div>

	</form:form>
</div>
