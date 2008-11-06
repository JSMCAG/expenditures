<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>

<logic:present name="user">
	<bean:define id="person" name="user" property="person"></bean:define>
	<logic:equal name="person" property="options.displayAuthorizationPending" value="true">
		<h2><bean:message key="title.pendingAuthorizations" bundle="EXPENDITURE_RESOURCES"/></h2>
		<logic:present name="pendingAuthorizationAcquisitionProcesses">
			<fr:view name="pendingAuthorizationAcquisitionProcesses"
					schema="viewAcquisitionProcessInList">
				<fr:layout name="tabular">
					<fr:property name="classes" value="tstyle2"/>

					<fr:property name="linkFormat(view)" value="/acquisition${class.simpleName}.do?method=viewAcquisitionProcess&acquisitionProcessOid=${OID}"/>
					<fr:property name="bundle(view)" value="EXPENDITURE_RESOURCES"/>
					<fr:property name="key(view)" value="link.view"/>
					<fr:property name="order(view)" value="1"/>
				</fr:layout>
			</fr:view>
		</logic:present>
	</logic:equal>
</logic:present>
