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
					<fmt:message bundle="${msg}" key="home.total.fuel" />
				</label>
			</td>
			<td>
				<div class="readonly_value">
					${statusBean.totalFuel}
				</div>
			</td>
		</tr>
		<!-- antennas on backup -->
		<tr>
			<td class="label">
				<label class="description">
					<fmt:message bundle="${msg}" key="home.antennas.backup" />
				</label>
			</td>
			<td>
				<div class="readonly_value">
					${statusBean.antennasOnBackup}
				</div>
			</td>
		</tr>
		<!-- antennas down -->
		<tr>
			<td class="label">
				<label class="description">
					<fmt:message bundle="${msg}" key="home.antennas.down" />
				</label>
			</td>
			<td>
				<div class="readonly_value">
					${statusBean.antennasDown}
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
					<a href="${pageContext.request.contextPath}/secure/antenna/create.do">
						<fmt:message bundle="${msg}" key="button.create.antenna"/>
					</a>
				</td>
			</tr>
	</table>
</div>
