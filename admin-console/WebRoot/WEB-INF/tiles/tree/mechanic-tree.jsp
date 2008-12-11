<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="controls" uri="http://www.kesdip.com/admin-console/taglib"%>

<div class="panel">
	<div class="heading">
		<fmt:message bundle="${msg}" key="tree.by.mechanic"/>
	</div>
	<controls:treeView 
		id="userTree"
		nodeTypes="
			Mechanic,all:icon-mechanic |
			Antenna,all:icon-antenna,ok:icon-antenna-ok,backup:icon-antenna-backup,down:icon-antenna-down 
			" 
		beanProperty="mechanics"
		renderDisabled="false"
	/>
</div>
<!-- 
<div class="panel">
	<div class="heading">Contact Details</div>
	Company Name<br>
	Address Line 1<br>
	Address Line 2<br>
	Town<br>
	Country<br>
	Postcode<br><br>
	
	T: +44 123 456 7890<br>
	F: +44 123 456 7890<br>
	E: <a href="mailto:support@company.com">support@company.com</a>
</div>
-->

