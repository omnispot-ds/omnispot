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
				<fmt:message bundle="${msg}" key="group.tab.view"/>
			 </li>
			 <li>
			 	<a href="${pageContext.request.contextPath}/secure/installationGroup/view-images.do?installationGroup.id=${dataObject.id}">
					<fmt:message bundle="${msg}" key="group.tab.images"/>
				</a>
			</li>
			<li>
			 	<a href="${pageContext.request.contextPath}/secure/installationGroup/view-actions.do?installationGroup.id=${dataObject.id}">
					<fmt:message bundle="${msg}" key="group.tab.actions"/>
				</a>
			</li>
		</ul>
	</div>

	<form:form id="form"
		action="${pageContext.request.contextPath}/secure/installationGroup/delete.do"
		commandName="dataObject" method="post" cssClass="appnitro">

		<form:hidden path="id" />

		<div class="form_description">
			<h2>
				<fmt:message bundle="${msg}" key="group.view.title" />
			</h2>
			<p>
				<fmt:message bundle="${msg}" key="group.view.description" />
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
				<a href="${pageContext.request.contextPath}/secure/installationGroup/edit.do?id=${dataObject.id}">
					<fmt:message bundle="${msg}" key="button.edit"/>
				</a>

				<a href="#" onclick="return confirmDelete('<fmt:message bundle="${msg}" key="group.delete.prompt"/>')">
					<fmt:message bundle="${msg}" key="button.delete"/>
				</a>
			</div>
		
			<!-- Actions menu -->                                                   
			<div id="action_menu" class="dropmenudiv">
				<a href="${pageContext.request.contextPath}/secure/action/deploy-content.do?installationGroup.id=${dataObject.id}">
					<fmt:message bundle="${msg}" key="button.deploy.content"/>
				</a>

				<a href="${pageContext.request.contextPath}/secure/action/schedule.do?action.type=1&installationGroup.id=${dataObject.id}">
					<fmt:message bundle="${msg}" key="button.start.installations"/>
				</a>

				<a href="${pageContext.request.contextPath}/secure/action/schedule.do?action.type=2&installationGroup.id=${dataObject.id}">
					<fmt:message bundle="${msg}" key="button.stop.installations"/>
				</a>

				<a href="${pageContext.request.contextPath}/secure/action/schedule.do?action.type=3&installationGroup.id=${dataObject.id}">
					<fmt:message bundle="${msg}" key="button.reboot.installations"/>
				</a>

				<a href="${pageContext.request.contextPath}/secure/action/schedule.do?action.type=4&installationGroup.id=${dataObject.id}">
					<fmt:message bundle="${msg}" key="button.reconf.installations"/>
				</a>

				<a href="${pageContext.request.contextPath}/secure/action/schedule.do?action.type=5&installationGroup.id=${dataObject.id}">
					<fmt:message bundle="${msg}" key="button.fetch.logs"/>
				</a>
			</div>
		
			<script type="text/javascript">
				cssdropdown.startchrome("viewmenu");
			</script>
		</div>
--%>
		<table width="100%">
			<!-- Customer -->
			<c:if test="${not empty dataObject.customer}">
			<tr>
				<td class="label">
					<label class="description">
						<fmt:message bundle="${msg}" key="group.customer" />
					</label>
				</td>
				<td>
					<div class="readonly_value">
						${dataObject.customer.name}
					</div>
				</td>
			</tr>
			</c:if>
			
			<!-- Name -->
			<tr>
				<td class="label">
					<label class="description">
						<fmt:message bundle="${msg}" key="group.name" />
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
						<fmt:message bundle="${msg}" key="group.comments" />
					</label>
				</td>
				<td>
					<div class="readonly_value">
						${dataObject.comments}
					</div>
				</td>
			</tr>
			<!-- Installations -->
			<tr>
				<td class="label">
					<label class="description">
						<fmt:message bundle="${msg}" key="group.installations" />
					</label>
				</td>
				<td>
					<div class="readonly_value">
						<ul>
						<c:forEach items="${dataObject.installations}" var="installation">
							<li>${installation.name}</li>	
						</c:forEach>
						</ul>
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
					<a href="${pageContext.request.contextPath}/secure/installationGroup/edit.do?id=${dataObject.id}">
						<fmt:message bundle="${msg}" key="button.edit"/></a>
					&nbsp;
					<a href="#" onclick="return confirmDelete('<fmt:message bundle="${msg}" key="group.delete.prompt"/>')">
						<fmt:message bundle="${msg}" key="button.delete"/></a>
					<br/>
					<!-- Actions -->
					<a href="${pageContext.request.contextPath}/secure/action/deploy-content.do?installationGroup.id=${dataObject.id}">
						<fmt:message bundle="${msg}" key="button.deploy.content"/></a>
					&nbsp;
					<a href="${pageContext.request.contextPath}/secure/action/schedule.do?action.type=4&installationGroup.id=${dataObject.id}">
						<fmt:message bundle="${msg}" key="button.restart.installations"/></a>
					&nbsp;
					<a href="${pageContext.request.contextPath}/secure/action/schedule.do?action.type=2&installationGroup.id=${dataObject.id}">
						<fmt:message bundle="${msg}" key="button.reboot.installations"/></a>
					&nbsp;
					<a href="${pageContext.request.contextPath}/secure/action/schedule.do?action.type=5&installationGroup.id=${dataObject.id}">
						<fmt:message bundle="${msg}" key="button.reconf.installations"/></a>
					&nbsp;
					<a href="${pageContext.request.contextPath}/secure/action/schedule.do?action.type=6&installationGroup.id=${dataObject.id}">
						<fmt:message bundle="${msg}" key="button.fetch.logs"/></a>
					&nbsp;
				</td>
			</tr>
		</table>
	</form:form>
</div>
