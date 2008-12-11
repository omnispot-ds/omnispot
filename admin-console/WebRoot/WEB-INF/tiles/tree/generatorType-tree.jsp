<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="controls" uri="http://www.kesdip.com/admin-console/taglib"%>

<div class="panel">
	<div class="heading">
		<fmt:message bundle="${msg}" key="tree.by.genType"/>
	</div>
	<controls:treeView 
		id="generatorTypeTree"
		nodeTypes="
			GeneratorType,all:icon-generator
			" 
		beanProperty="generatorTypes"
		renderDisabled="false"
	/>
</div>


