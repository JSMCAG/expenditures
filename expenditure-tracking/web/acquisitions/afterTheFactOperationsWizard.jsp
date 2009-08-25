<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<h2><bean:message key="title.registerAcquisitions" bundle="EXPENDITURE_RESOURCES"/></h2>
<p class="mtop05">
	<bean:message key="message.info.afterTheFactProcessIntroduction" bundle="EXPENDITURE_RESOURCES"/>.
</p>
<div class="infobox_dotted">
	<ul>
		<li>
			<html:link action="/acquisitionAfterTheFactAcquisitionProcess.do?method=prepareCreateAfterTheFactAcquisitionProcess">
				<bean:message key="link.sideBar.afterTheFactAcquisitionProcess.create" bundle="EXPENDITURE_RESOURCES"/>
			</html:link>
		</li>
		<li>
			<html:link action="/acquisitionAfterTheFactAcquisitionProcess.do?method=prepareImport">
				<bean:message key="link.sideBar.importAfterTheFactAcquisitions" bundle="EXPENDITURE_RESOURCES"/>
			</html:link>
		</li>
		<li>
			<html:link action="/acquisitionAfterTheFactAcquisitionProcess.do?method=listImports">
				<bean:message key="link.sideBar.listAfterTheFactAcquisitions" bundle="EXPENDITURE_RESOURCES"/>
			</html:link>
		</li>
	</ul>
</div>