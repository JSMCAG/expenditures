package pt.ist.expenditureTrackingSystem.domain.acquisitions.simplified.activities;

import pt.ist.expenditureTrackingSystem.domain.acquisitions.RegularAcquisitionProcess;
import pt.ist.expenditureTrackingSystem.domain.acquisitions.activities.GenericAcquisitionProcessActivity;
import pt.ist.expenditureTrackingSystem.domain.organization.Person;
import pt.ist.fenixWebFramework.services.Command;
import pt.ist.fenixWebFramework.services.ServiceManager;

public class SubmitForApproval extends GenericAcquisitionProcessActivity {

    @Override
    protected boolean isAccessible(RegularAcquisitionProcess process) {
	final Person loggedPerson = getLoggedPerson();
	return loggedPerson != null && loggedPerson.equals(process.getRequestor());
    }

    @Override
    protected boolean isAvailable(RegularAcquisitionProcess process) {
	return  super.isAvailable(process) && process.getAcquisitionProcessState().isInGenesis() && process.getAcquisitionRequest().isFilled()
		&& process.getAcquisitionRequest().isEveryItemFullyAttributedToPayingUnits();
    }

    @Override
    protected void process(RegularAcquisitionProcess process, Object... objects) {
	process.submitForApproval();
    }

    @Override
    protected void notifyUsers(final RegularAcquisitionProcess process) {
	ServiceManager.registerAfterCommitCommand(new Command() {
	    public void execute() {
		notifyUnitsResponsibles(process);
	    }
	});
    }
}
