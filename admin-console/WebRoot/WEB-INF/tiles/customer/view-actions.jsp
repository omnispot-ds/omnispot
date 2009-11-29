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
			 	<a href="${pageContext.request.contextPath}/secure/customer/view.do?id=${param['customer.id']}">
					<fmt:message bundle="${msg}" key="customer.tab.view"/>
				</a>
			 </li>
			 <li>
			 	<a href="${pageContext.request.contextPath}/secure/customer/view-images.do?customer.id=${param['customer.id']}">
					<fmt:message bundle="${msg}" key="customer.tab.images"/>
				</a>
			</li>
			 <li class="selected_tab">
				<fmt:message bundle="${msg}" key="customer.tab.actions"/>
			</li>
		</ul>
	</div>

	<form:form id="form" 
		action="${pageContext.request.contextPath}/secure/customer/view-actions.do"
		commandName="dataObject" method="post" cssClass="appnitro">

		<input type="hidden" name="customer.id" value="${param['customer.id']}" />

		<div class="form_description">
			<h2>
				${dataObject.entityName}
			</h2>
			<p>
				<fmt:message bundle="${msg}" key="page.refresh"/>
				<span id="pageRefreshTimer">&nbsp;</span>
				<a href="#" 
					title='<fmt:message bundle="${msg}" key="button.refresh.now"/>'
					onclick="window.location.reload();return true;"><img 
						src="${pageContext.request.contextPath}/resources/images/reload.gif"/></a>
			</p>
		</div>

		<c:if test="${empty dataObject.perTypeAndStatus}">
			<li class="maxWidth">
				<span class="description">
					<fmt:message bundle="${msg}" key="action.noActions" />
				</span>
			</li>
		</c:if>

		<c:forEach items="${dataObject.perTypeAndStatus}" var="mapEntry">
			<!-- Action type -->
			<li class="maxWidth">
				<label class="description">
					<fmt:message bundle="${msg}" key="action.label_${mapEntry.key}" />
				</label>

				<table width="100%">
					<c:if test="${not empty mapEntry.value}">
						<!-- Action date -->
						<tr>
							<td colspan="2">
								<span class="description">
									${mapEntry.value[0].action.dateString}
								</span>
							</td>
						</tr>
					</c:if>
					<c:forEach items="${mapEntry.value}" var="actionInstallationListBean">
						<!-- Action status -->
						<tr>
							<td>
								<span class="description">
									<fmt:message bundle="${msg}" key="action.status_${actionInstallationListBean.action.status}" /> 
								</span>
							</td>
							<td>
								<c:forEach items="${actionInstallationListBean.installations}" var="installation">
									<a href="${pageContext.request.contextPath}/secure/installation/view.do?id=${installation.id}">
									${installation.site.name}-${installation.name}</a>
								</c:forEach>
							</td>
						<tr>
					</c:forEach>
				</table>				
			</li>
		</c:forEach>
	</form:form>
	
	<script type="text/javascript">
		autoRefreshPage();
	</script>
</div>
