package pt.ist.expenditureTrackingSystem.domain.acquisitions.activities.commons;

import module.workflow.activities.WorkflowActivity;
import myorg.domain.User;
import myorg.util.BundleUtil;
import pt.ist.expenditureTrackingSystem.domain.acquisitions.PaymentProcess;
import pt.ist.expenditureTrackingSystem.domain.dto.FundAllocationBean;

public class AllocateFundsPermanently<P extends PaymentProcess> extends
	WorkflowActivity<P, AllocateFundsPermanentlyActivityInformation<P>> {

    @Override
    public boolean isActive(P process, User user) {
	return process.isAccountingEmployee(user.getExpenditurePerson()) && isUserProcessOwner(process, user)
		&& process.hasAllocatedFundsPermanentlyForAllProjectFinancers() && !process.hasAllInvoicesAllocated();
    }

    @Override
    protected void process(AllocateFundsPermanentlyActivityInformation<P> activityInformation) {
	for (FundAllocationBean fundAllocationBean : activityInformation.getBeans()) {
	    fundAllocationBean.getFinancer().addEffectiveFundAllocationId(fundAllocationBean.getEffectiveFundAllocationId());
	}
	P process = activityInformation.getProcess();
	if (process.isInvoiceConfirmed()) {
	    process.allocateFundsPermanently();
	}

    }

    public AllocateFundsPermanentlyActivityInformation<P> getActivityInformation(P process) {
	return new AllocateFundsPermanentlyActivityInformation<P>(process, this, true);
    }

    @Override
    public boolean isDefaultInputInterfaceUsed() {
	return false;
    }

    @Override
    public String getLocalizedName() {
	return BundleUtil.getStringFromResourceBundle(getUsedBundle(), "label." + getClass().getName());
    }

    @Override
    public String getUsedBundle() {
	return "resources/AcquisitionResources";
    }

}
