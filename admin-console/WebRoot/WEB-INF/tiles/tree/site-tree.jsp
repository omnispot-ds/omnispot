<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="controls" uri="http://www.kesdip.com/admin-console/taglib"%>

<div class="panel">
	<div class="heading">
		<fmt:message bundle="${msg}" key="tree.by.site"/>
	</div>
	<controls:treeView 
		id="siteTree"
		nodeTypes="
			Customer,all:icon-customer |
			Site,all:icon-site,ok:icon-site-ok,nok:icon-site-nok,down:icon-site-down, :icon-site  |
			Installation,all:icon-installation,ok:icon-installation-ok,nok:icon-installation-nok,down:icon-installation-down 
			" 
		beanProperty="customers"
		renderDisabled="false"
	/>
</div>
<%--
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
--%>

