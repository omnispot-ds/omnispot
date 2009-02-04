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
			<li class="selected_tab">
				<fmt:message bundle="${msg}" key="customer.tab.view"/>
			 </li>
			 <li>
			 	<a href="${pageContext.request.contextPath}/secure/customer/view-images.do?customer.id=${dataObject.id}">
					<fmt:message bundle="${msg}" key="customer.tab.images"/>
				</a>
			</li>
			 <li>
			 	<a href="${pageContext.request.contextPath}/secure/customer/view-actions.do?customer.id=${dataObject.id}">
					<fmt:message bundle="${msg}" key="customer.tab.actions"/>
				</a>
			</li>
		</ul>
	</div>

	<form:form id="form"
		action="${pageContext.request.contextPath}/secure/customer/delete.do"
		commandName="dataObject" method="post" cssClass="appnitro">

		<form:hidden path="id" />

		<div class="form_description">
			<h2>
				<fmt:message bundle="${msg}" key="customer.view.title" />
			</h2>
			<p>
				<fmt:message bundle="${msg}" key="customer.view.description" />
			</p>
			<p class="error_message_title">
				<form:errors />
			</p>
		</div>
<%--
		<div class="heading">
			<!-- Menu headers -->
			<div class="chromestyle" id="viewmenu">
				<ul>
					<li><a href="#" rel="entity_menu">
						<fmt:message key="menu.entity" bundle="${msg}"/>
					</a></li>
					<li><a href="#" rel="action_menu">
						<fmt:message key="menu.actions" bundle="${msg}"/>
					</a></li>
				</ul>
			</div>
		
			<!-- Entity menu -->                                                   
			<div id="entity_menu" class="dropmenudiv">
				<a href="${pageContext.request.contextPath}/secure/user/create.do?affiliation.id=${dataObject.id}">
					<fmt:message bundle="${msg}" key="button.create.user"/></a>

				<a href="${pageContext.request.contextPath}/secure/site/create.do?customer.id=${dataObject.id}">
					<fmt:message bundle="${msg}" key="button.create.site"/></a>

				<a href="${pageContext.request.contextPath}/secure/installationGroup/create.do?customer.id=${dataObject.id}">
					<fmt:message bundle="${msg}" key="button.create.group"/></a>

				<a href="${pageContext.request.contextPath}/secure/customer/edit.do?id=${dataObject.id}">
					<fmt:message bundle="${msg}" key="button.edit"/></a>

				<a href="#" onclick="return confirmDelete('<fmt:message bundle="${msg}" key="customer.delete.prompt"/>')">
					<fmt:message bundle="${msg}" key="button.delete"/></a>
			</div>
		
			<!-- Actions menu -->                                                   
			<div id="action_menu" class="dropmenudiv">
				<a href="${pageContext.request.contextPath}/secure/action/deploy-content.do?customer.id=${dataObject.id}">
					<fmt:message bundle="${msg}" key="button.deploy.content"/></a>

				<a href="${pageContext.request.contextPath}/secure/action/schedule.do?action.type=1&customer.id=${dataObject.id}">
					<fmt:message bundle="${msg}" key="button.start.installations"/>
				</a>

				<a href="${pageContext.request.contextPath}/secure/action/schedule.do?action.type=2&customer.id=${dataObject.id}">
					<fmt:message bundle="${msg}" key="button.stop.installations"/>
				</a>

				<a href="${pageContext.request.contextPath}/secure/action/schedule.do?action.type=3&customer.id=${dataObject.id}">
					<fmt:message bundle="${msg}" key="button.reboot.installations"/>
				</a>

				<a href="${pageContext.request.contextPath}/secure/action/schedule.do?action.type=4&customer.id=${dataObject.id}">
					<fmt:message bundle="${msg}" key="button.reconf.installations"/>
				</a>

				<a href="${pageContext.request.contextPath}/secure/action/schedule.do?action.type=5&customer.id=${dataObject.id}">
					<fmt:message bundle="${msg}" key="button.fetch.logs"/>
				</a>
			</div>
		
			<script type="text/javascript">
				cssdropdown.startchrome("viewmenu");
			</script>
		</div>
--%>
		<table width="100%">
			<!-- Name -->
			<tr>
				<td class="label">
					<label class="description">
						<fmt:message bundle="${msg}" key="customer.name" />
					</label>
				</td>
				<td>
					<div class="readonly_value">
						${dataObject.name}
					</div>
				</td>
			</tr>
			<!-- Comments -->
			<tr>
				<td class="label">
					<label class="description">
						<fmt:message bundle="${msg}" key="customer.comments" />
					</label>
				</td>
				<td>
					<div class="readonly_value">
						${dataObject.comments}
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
					<a href="${pageContext.request.contextPath}/secure/user/create.do?affiliation.id=${dataObject.id}">
						<fmt:message bundle="${msg}" key="button.create.user"/></a>
					&nbsp;
					<a href="${pageContext.request.contextPath}/secure/site/create.do?customer.id=${dataObject.id}">
						<fmt:message bundle="${msg}" key="button.create.site"/></a>
					&nbsp;
					<a href="${pageContext.request.contextPath}/secure/installationGroup/create.do?customer.id=${dataObject.id}">
						<fmt:message bundle="${msg}" key="button.create.group"/></a>
					&nbsp;
					<a href="${pageContext.request.contextPath}/secure/customer/edit.do?id=${dataObject.id}">
						<fmt:message bundle="${msg}" key="button.edit"/></a>
					&nbsp;
					<a href="#" onclick="return confirmDelete('<fmt:message bundle="${msg}" key="customer.delete.prompt"/>')">
						<fmt:message bundle="${msg}" key="button.delete"/></a>
					<br/>
					<!-- Actions -->
					<a href="${pageContext.request.contextPath}/secure/action/deploy-content.do?customer.id=${dataObject.id}">
						<fmt:message bundle="${msg}" key="button.deploy.content"/></a>
					&nbsp;
					<a href="${pageContext.request.contextPath}/secure/action/schedule.do?action.type=4&customer.id=${dataObject.id}">
						<fmt:message bundle="${msg}" key="button.restart.installations"/></a>
					&nbsp;
					<a href="${pageContext.request.contextPath}/secure/action/schedule.do?action.type=2&customer.id=${dataObject.id}">
						<fmt:message bundle="${msg}" key="button.reboot.installations"/></a>
					&nbsp;
					<a href="${pageContext.request.contextPath}/secure/action/schedule.do?action.type=5&customer.id=${dataObject.id}">
						<fmt:message bundle="${msg}" key="button.reconf.installations"/></a>
					&nbsp;
					<a href="${pageContext.request.contextPath}/secure/action/schedule.do?action.type=6&customer.id=${dataObject.id}">
						<fmt:message bundle="${msg}" key="button.fetch.logs"/></a>
					&nbsp;
				</td>
			</tr>
		</table>
	</form:form>
</div>
