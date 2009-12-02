package pt.ist.expenditureTrackingSystem.domain.acquisitions.simplified.activities;

import myorg.domain.util.Money;

import org.joda.time.LocalDate;

import myorg.domain.exceptions.DomainException;
import pt.ist.expenditureTrackingSystem.domain.RoleType;
import pt.ist.expenditureTrackingSystem.domain.acquisitions.AcquisitionRequest;
import pt.ist.expenditureTrackingSystem.domain.acquisitions.RegularAcquisitionProcess;
import pt.ist.expenditureTrackingSystem.domain.acquisitions.activities.GenericAcquisitionProcessActivity;
import pt.ist.expenditureTrackingSystem.domain.organization.Person;
import pt.ist.expenditureTrackingSystem.domain.organization.Supplier;

public class FundAllocationExpirationDate extends GenericAcquisitionProcessActivity {

    public static class FundAllocationNotAllowedException extends DomainException {

	public FundAllocationNotAllowedException() {
	    super("acquisitionRequestItem.message.exception.fundAllocationNotAllowed");
	}
	
    }

    @Override
    protected boolean isAccessible(RegularAcquisitionProcess process) {
	final Person loggedPerson = getLoggedPerson();
	return loggedPerson != null
		//&& process.isResponsibleForUnit(loggedPerson)
		&& userHasRole(RoleType.ACQUISITION_CENTRAL) 
		;
    }

    @Override
    protected boolean isAvailable(RegularAcquisitionProcess process) {
	return  super.isAvailable(process)
		&& process.getAcquisitionProcessState().isActive()
		&& !process.isPendingFundAllocation()
		&& !process.getAcquisitionRequest().hasAnyFundAllocationId()
		&& process.getAcquisitionRequest().isSubmittedForFundsAllocationByAllResponsibles();
    }

    @Override
    protected void process(RegularAcquisitionProcess process, Object... objects) {
	if (process.getAcquisitionRequest().isSubmittedForFundsAllocationByAllResponsibles()) {
	   if (!process.getShouldSkipSupplierFundAllocation()) {
		checkSupplierLimit(process);
		LocalDate now = new LocalDate();
		process.setFundAllocationExpirationDate(now.plusDays(90));
	    }
	    else {
		process.skipFundAllocation();
	    }
	}

	process.allocateFundsToSupplier();
    }

    private void checkSupplierLimit(final RegularAcquisitionProcess process) {
	final AcquisitionRequest acquisitionRequest = process.getAcquisitionRequest();
	final Money forSupplierLimit = acquisitionRequest.getCurrentSupplierAllocationValue();
	for (final Supplier supplier : process.getSuppliers()) {
	    if (!supplier.isFundAllocationAllowed(forSupplierLimit)) {
		throw new FundAllocationNotAllowedException();
	    }
	}
    }

}
