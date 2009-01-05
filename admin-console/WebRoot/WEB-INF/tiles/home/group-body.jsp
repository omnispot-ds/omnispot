<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="messages" scope="application" var="msg"/>

<div class="panel">
	<div class="heading">
		<fmt:message bundle="${msg}" key="home.status.title"/>
	</div>
	<table width="100%">
		<!-- total fuel -->
		<tr>
			<td class="label">
				<label class="description">
					<fmt:message bundle="${msg}" key="home.some.system.status" />
				</label>
			</td>
			<td>
				<div class="readonly_value">
					Some system status value
				</div>
			</td>
		</tr>
		<!-- antennas on backup -->
		<tr>
			<td class="label">
				<label class="description">
					<fmt:message bundle="${msg}" key="home.another.system.status" />
				</label>
			</td>
			<td>
				<div class="readonly_value">
					Another system status value
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
				<a href="${pageContext.request.contextPath}/secure/customer/create.do">
					<fmt:message bundle="${msg}" key="button.create.customer"/>
				</a>
			</td>
		</tr>
	</table>
</div>
