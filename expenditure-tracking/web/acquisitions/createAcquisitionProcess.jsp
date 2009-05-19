<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>


<%@page import="java.util.ResourceBundle"%>
<%@page import="myorg.util.BundleUtil"%><h2><bean:message key="acquisitionProcess.title.createAcquisitionRequest" bundle="ACQUISITION_RESOURCES"/></h2>

<div class="infoop2">
	<bean:message key="acquisitionProcess.message.note" bundle="ACQUISITION_RESOURCES" />
</div>

<p class="mtop15 mbottom05"><strong><bean:message key="link.create.simplifiedAcquisitionProcedure" bundle="EXPENDITURE_RESOURCES"/></strong></p>

<fr:form id="createForm" action="/acquisitionSimplifiedProcedureProcess.do?method=createNewAcquisitionProcess">
	<fr:edit id="acquisitionProcessBean"
			name="acquisitionProcessBean"
			type="pt.ist.expenditureTrackingSystem.domain.dto.CreateAcquisitionProcessBean"
			schema="createAcquisitionRequest">
		<fr:layout name="tabular">
			<fr:property name="classes" value="form"/>
			<fr:property name="columnClasses" value=",,tderror"/>
		</fr:layout>
	</fr:edit>
	<html:submit styleClass="inputbutton"><bean:message key="button.submit" bundle="EXPENDITURE_RESOURCES"/></html:submit>
</fr:form>

<script type="text/javascript">
	$("input[id$='supplier_AutoComplete']").change(function() {
				<%= "$.getJSON(\"" + request.getContextPath() + "/acquisitionSimplifiedProcedureProcess.do?method=checkSupplierLimit&supplierOid=\" + $(this).attr('value'),function(data, textStatus) {dealWith(data)})" %>
			}); 

	function dealWith(data) {

		$("#limitInformation").remove();
		

		if(data['status'] == 'SOK') {

			<%
				String message = BundleUtil.getStringFromResourceBundle("resources.AcquisitionResources","label.supplier.allocationInfo.notFormatted");
			%>
			
			var text = "<%= message %>";
			text = formatString(text,[data['softLimit'], data['supplierLimit']]);
		
			$("#createForm").before("<div id=\"limitInformation\"><p class=\"mbottom05\"><span>" + text + "</span><br/>");
		} else {

			<%
				String message2 = BundleUtil.getStringFromResourceBundle("resources.AcquisitionResources","label.attention.supplier.supplierOverLimit.notFormatted");
			%>
		
			var text = "<%= message2 %>";
			text = formatString(text,[data['softLimit'],data['supplierLimit']]);
	
			<bean:define id="messageExtra">
				<bean:message key="label.attention.supplier.explanation" bundle="ACQUISITION_RESOURCES"/>
			</bean:define>

			$("#createForm").before("<div id=\"limitInformation\"><div class=\"infoop4\">" + text + "</p><p><%= messageExtra %></p>");
		}
	}	
</script>

	