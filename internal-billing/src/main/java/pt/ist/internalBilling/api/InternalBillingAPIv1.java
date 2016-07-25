package pt.ist.internalBilling.api;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.fenixedu.bennu.InternalBillingConfiguration;
import org.fenixedu.bennu.InternalBillingConfiguration.ConfigurationProperties;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.UserProfile;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import pt.ist.expenditureTrackingSystem.domain.organization.Unit;
import pt.ist.internalBilling.BillingInformationHook;
import pt.ist.internalBilling.domain.Billable;
import pt.ist.internalBilling.domain.BillableService;
import pt.ist.internalBilling.domain.BillableStatus;
import pt.ist.internalBilling.domain.CurrentBillableHistory;
import pt.ist.internalBilling.domain.PrintService;
import pt.ist.internalBilling.domain.UserBeneficiary;

@Path("/internalBilling/v1")
public class InternalBillingAPIv1 {

    public final static String JSON_UTF8 = "application/json; charset=utf-8";

    @GET
    @Produces(JSON_UTF8)
    @Path("print/user/{username}/info")
    public String userInfo(final @PathParam("username") String username, final @QueryParam("token") String token) {
        checkAppCredentials(token);
        final User user = User.findByUsername(username);
        return toJson(user).toString();
    }

    @POST
    @Produces(JSON_UTF8)
    @Path("print/user/{username}/setUnit/{unitId}")
    public String setUnit(final @PathParam("username") String username, final @PathParam("unitId") String unitId, final @QueryParam("token") String token) {
        checkAppCredentials(token);
        final User user = User.findByUsername(username);
        if (user != null) {
            final UserBeneficiary beneficiary = user.getUserBeneficiary();
            if (beneficiary != null) {
                beneficiary.getBillableSet().stream()
                    .filter(b -> b.getBillableStatus() == BillableStatus.AUTHORIZED)
                    .filter(b -> b.getUnit().getExternalId().equals(unitId))
                    .filter(b -> b.getBillableService() instanceof PrintService)
                    .peek(b -> BillingInformationHook.HOOKS.forEach(h -> h.signalUnitChange(user, b.getUnit())))
                    .forEach(b -> b.setUserFromCurrentBillable(user)); // only 1 o 0 ... but this will do the job
                    ;
            }
        }
        return toJson(user).toString();
    }

    private JsonObject toJson(final User user) {
        final JsonObject jo = new JsonObject();
        if (user != null) {
            final UserProfile profile = user.getProfile();
            jo.addProperty("username", user.getUsername());
            jo.addProperty("avatarUrl", profile.getAvatarUrl());
            jo.addProperty("name", profile.getDisplayName());

            final JsonObject currentBillingUnit = toJson(currentBillingUnitFor(user));
            jo.add("currentBillingUnit", currentBillingUnit);

            final JsonArray billingUnits = billingUnitsFor(user);
            jo.add("billingUnits", billingUnits);

            BillingInformationHook.HOOKS.forEach(h -> h.addInfoFor(jo, user));
        }
        return jo;
    }

    private JsonArray billingUnitsFor(final User user) {
        final JsonArray result = new JsonArray();
        if (user != null) {
            final UserBeneficiary beneficiary = user.getUserBeneficiary();
            if (beneficiary != null) {
                beneficiary.getBillableSet().stream()
                    .filter(b -> b.getBillableStatus() == BillableStatus.AUTHORIZED)
                    .filter(b -> b.getBillableService() instanceof PrintService)
                    .forEach(b -> result.add(toJson(b)));
            }
        }
        return result;
    }

    private Billable currentBillingUnitFor(final User user) {
        final CurrentBillableHistory currentBillableHistory = user.getCurrentBillableHistory();
        if (currentBillableHistory != null) {
            final Billable billable = currentBillableHistory.getBillable();
            if (billable != null) {
                final BillableStatus status = billable.getBillableStatus();
                if (status == BillableStatus.AUTHORIZED) {
                    final BillableService service = billable.getBillableService();
                    if (service instanceof PrintService) {
                        return billable;
                    }
                }
            }
        }
        return null;
    }

    private JsonObject toJson(final Billable billable) {
        if (billable != null) {
            final JsonObject jo = billable.getConfigurationAsJson();
            final Unit unit = billable.getUnit();
            jo.addProperty("id", unit.getExternalId());
            jo.addProperty("shortIdentifier", unit.getShortIdentifier());
            jo.addProperty("name", unit.getName());
            jo.addProperty("presentationName", unit.getPresentationName());

            BillingInformationHook.HOOKS.forEach(h -> h.addInfoFor(jo, billable));
            return jo;
        }
        return null;
    }

    private void checkAppCredentials(final String token) {
        final ConfigurationProperties confifg = InternalBillingConfiguration.getConfiguration();
        final String printAppToken = confifg.printAppToken();
        if (printAppToken == null || printAppToken.isEmpty() || !printAppToken.equals(token)) {
            JsonObject errorObject = new JsonObject();
            errorObject.addProperty("error", "not.authorized");
            errorObject.addProperty("description", "You are not authorized to access this endpoint. Check your credentials.");
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).entity(errorObject.toString()).build());
        }
    }

}
