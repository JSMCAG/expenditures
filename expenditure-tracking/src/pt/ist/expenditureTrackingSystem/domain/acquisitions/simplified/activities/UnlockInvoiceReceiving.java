package pt.ist.expenditureTrackingSystem.domain.acquisitions.simplified.activities;

import pt.ist.expenditureTrackingSystem.domain.RoleType;
import pt.ist.expenditureTrackingSystem.domain.acquisitions.AcquisitionProcessState;
import pt.ist.expenditureTrackingSystem.domain.acquisitions.RegularAcquisitionProcess;
import pt.ist.expenditureTrackingSystem.domain.acquisitions.activities.GenericAcquisitionProcessActivity;

public class UnlockInvoiceReceiving extends GenericAcquisitionProcessActivity {

    @Override
    protected boolean isAccessible(RegularAcquisitionProcess process) {
	return userHasRole(RoleType.ACQUISITION_CENTRAL);
    }

    @Override
    protected boolean isAvailable(RegularAcquisitionProcess process) {
	AcquisitionProcessState acquisitionProcessState = process.getAcquisitionProcessState();
	return super.isAvailable(process)
		&& (acquisitionProcessState.isInvoiceReceived() || acquisitionProcessState.isPendingInvoiceConfirmation());
    }

    @Override
    protected void process(RegularAcquisitionProcess process, Object... objects) {
	process.processAcquisition();
    }

}