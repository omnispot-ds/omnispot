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
			 	<a href="${pageContext.request.contextPath}/secure/site/view.do?id=${param['site.id']}">
					<fmt:message bundle="${msg}" key="site.tab.view"/>
				</a>
			 </li>
			 <li class="selected_tab">
				<fmt:message bundle="${msg}" key="site.tab.images"/>
			</li>
		 	<li>
			 	<a href="${pageContext.request.contextPath}/secure/site/view-actions.do?site.id=${param['site.id']}">
					<fmt:message bundle="${msg}" key="site.tab.actions"/>
				</a>
			</li>
		</ul>
	</div>

	<form:form id="form" 
		action="${pageContext.request.contextPath}/secure/site/view-images.do"
		commandName="dataObject" method="post" cssClass="appnitro">

		<input type="hidden" name="site.id" value="${param['site.id']}" />

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

		<div class="thumbnail-area">
		
			<c:forEach items="${dataObject.printScreens}" var="ps" varStatus="status">
				<c:if test="${status.first == true || (status.count % 5 == 0)}">
					<span class="thumbnail-group">
				</c:if>
				<a class="thumbnail" 
					href="${pageContext.request.contextPath}/secure/installation/view.do?id=${ps.installation.id}"><img 
						class="thumb" src="${ps.fileUrl}" id="${ps.installation.uuid}" 
						alt="${ps.installation.uuid} - ${ps.fileDateString}" 
						title="${ps.installation.uuid} - ${ps.fileDateString}"/><span><label 
							for="${ps.installation.uuid}">${ps.installation.name}</label><img 
							class="full" src="${ps.fileUrl}" id="${ps.installation.uuid}" 
						alt="${ps.installation.uuid} - ${ps.fileDateString}" 
						title="${ps.installation.uuid} - ${ps.fileDateString}"/></span></a>
				<c:if test="${status.count % 4 != 0}">
					<span class="thumbnail-spacer">&nbsp;</span>
				</c:if>		
				<c:if test="${status.last == true || (status.count % 4 == 0)}">
					<span>
				</c:if>
			</c:forEach>			
		</div>

	</form:form>
	
	<script type="text/javascript">
		autoRefreshPage();
	</script>
</div>
