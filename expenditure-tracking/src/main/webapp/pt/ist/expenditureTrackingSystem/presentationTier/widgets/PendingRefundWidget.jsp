<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic"%>

<logic:notEmpty name="refundCounters">
	<table class="width100pc">
		<logic:iterate id="counter" name="refundCounters">
			<tr>
				<bean:define id="state" name="counter" property="countableObject" />
				<td><fr:view name="counter" property="countableObject" /></td>
				<td>
				<bean:define id="showResponsabilities" value="false" toScope="request" />
				<logic:present name="person">
					<logic:notEmpty name="person" property="authorizations">
						<bean:define id="showResponsabilities" value="true" toScope="request" />
					</logic:notEmpty>
				</logic:present>
				<html:link
					page="<%="/search.do?method=searchJump&searchProcessValue=REFUND&hasAvailableAndAccessibleActivityForUser=true&checkForUserAwareness=true&responsibleUnitSetOnly="
								    + showResponsabilities.toString() + "&refundProcessStateType=" + state%>">
					<fr:view name="counter" property="value" />
				</html:link>
				</td>
			</tr>
		</logic:iterate>
	</table>
</logic:notEmpty>
<logic:empty name="refundCounters">
	<p><em><bean:message key="label.no.pendingProcesses" bundle="EXPENDITURE_RESOURCES"/>.</em></p>
</logic:empty>
