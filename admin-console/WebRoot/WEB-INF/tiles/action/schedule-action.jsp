<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="controls" uri="http://www.kesdip.com/admin-console/taglib"%>

<fmt:setBundle basename="messages" scope="application" var="msg" />

<div id="form_container">

	<form:form id="form"
		action="${pageContext.request.contextPath}/secure/action/schedule.do"
		commandName="dataObject" method="post" cssClass="appnitro" 
		enctype="multipart/form-data">
		
		<input type="hidden" id="actionCommand" name="actionCommand"/>
		
		<c:if test="${not empty dataObject.customer}">
			<form:hidden path="customer.id" />
		</c:if>
		<c:if test="${not empty dataObject.installationGroup}">
			<form:hidden path="installationGroup.id" />
		</c:if>
		<c:if test="${not empty dataObject.site}">
			<form:hidden path="site.id" />
		</c:if>
		<c:if test="${not empty dataObject.installation}">
			<form:hidden path="installation.id" />
		</c:if>

		<form:hidden path="action.type"/>

		<div class="form_description">
			<h2>
				<fmt:message bundle="${msg}" key="schedule.action.title" />
			</h2>
			<p>
				<fmt:message bundle="${msg}" key="schedule.action.description" />
			</p>
			<p class="error_message_title">
				<form:errors />
			</p>
		</div>
		<ul>
			<!-- Customer -->
			<c:if test="${not empty dataObject.customer}">
			<li id="li_customer">
				<label class="description" >
					<fmt:message bundle="${msg}" key="schedule.action.customer" />
				</label>
				<div class="readonly_value">
					${dataObject.customer.name}
				</div>
			</li>
			</c:if>
			<!-- Site -->
			<c:if test="${not empty dataObject.site}">
			<li id="li_site">
				<label class="description" >
					<fmt:message bundle="${msg}" key="schedule.action.site" />
				</label>
				<div class="readonly_value">
					${dataObject.site.name}
				</div>
			</li>
			</c:if>
			<!-- Group -->
			<c:if test="${not empty dataObject.installationGroup}">
			<li id="li_group">
				<label class="description" >
					<fmt:message bundle="${msg}" key="schedule.action.group" />
				</label>
				<div class="readonly_value">
					${dataObject.installationGroup.name}
				</div>
			</li>
			</c:if>
			<!-- Installation -->
			<c:if test="${not empty dataObject.installation}">
			<li id="li_installation">
				<label class="description" >
					<fmt:message bundle="${msg}" key="schedule.action.installation" />
				</label>
				<div class="readonly_value">
					${dataObject.installation.name}
				</div>
			</li>
			</c:if>
			<!-- Installation count -->
			<c:if test="${empty dataObject.installation}">
			<li id="li_count">
				<label class="description" >
					<fmt:message bundle="${msg}" key="schedule.action.count" />
				</label>
				<div class="readonly_value">
					${dataObject.installationCount}
				</div>
			</li>
			</c:if>
			<li id="li_type">
				<label class="description" >
					<fmt:message bundle="${msg}" key="schedule.action.type" />
				</label>
				<div class="readonly_value">
					<fmt:message bundle="${msg}" key="action.label_${dataObject.action.type}" />
				</div>
			</li>
			
			<c:if test="${dataObject.action.type == 5}">
				<!-- Reconfiguration parameters -->
				<c:forEach items="${dataObject.action.parameters}" var="param" varStatus="status">
	
					<!-- Parameter -->
					<li id="li_param_${status.count - 1}_" class="section_break maxWidth">
						<span class="description">
							<fmt:message bundle="${msg}" key="schedule.action.param" /> [${status.count}]
						</span>
						<c:if test="${status.last}">
							<span id="addParamLinks">&nbsp;
								<a href="#" onclick="return doFormAction('actionCommand', 'addParam', 'form');" 
									title='<fmt:message bundle="${msg}" key="button.add.param" />'>
									<img src="${pageContext.request.contextPath}/resources/images/add.png" 
										border="0"/></a>&nbsp;
								<c:if test="${not status.first}">		
									<a href="#" onclick="return doFormAction('actionCommand', 'removeParam', 'form');" 
										title='<fmt:message bundle="${msg}" key="button.delete.param" />'>
										<img src="${pageContext.request.contextPath}/resources/images/delete.gif" 
											border="0"/></a>
								</c:if>
							</span>
						</c:if>
	
						<table width="100%" >
							<!-- Param name -->
							<tr>
								<td class="label">
									<label class="description">
										<fmt:message bundle="${msg}" key="schedule.action.paramName" />
									</label>
								</td>
								<td>
									<form:input id="paramName_${status.count - 1}_" 
										path="action.parameters[${status.count - 1}].name"
										cssClass="element text" size="30" maxlength="512" /> 
								</td>
								<td>
									<p class="tableGuidelines" id="guide_paramName">
										<small> 
											<fmt:message bundle="${msg}" key="schedule.action.paramName.help" />
										</small>
									</p>							
								</td>
							</tr>
							<!-- Param value -->
							<tr>
								<td class="label">
									<label class="description">
										<fmt:message bundle="${msg}" key="schedule.action.paramValue" />
									</label>
								</td>
								<td>
									<form:input id="paramValue_${status.count - 1}_" 
										path="action.parameters[${status.count - 1}].value"
										cssClass="element text" size="30" maxlength="512" /> 
								</td>
								<td>
									<p class="tableGuidelines" id="guide_paramValue">
										<small> 
											<fmt:message bundle="${msg}" key="schedule.action.paramValue.help" />
										</small>
									</p>							
								</td>
							</tr>
						</table>				
					</li>
				</c:forEach>
			</c:if>

			<li class="buttons">
				<input id="submitButton" class="button_text" type="submit" name="submitButton"
					value='<fmt:message bundle="${msg}" key="button.schedule"/>' />
				&nbsp;
				<input id="resetButton" class="button_text" type="reset" name="resetButton"
					value='<fmt:message bundle="${msg}" key="button.reset"/>' />
			</li>
		</ul>
	</form:form>
</div>
