package pt.ist.expenditureTrackingSystem.domain.announcements.activities;

import pt.ist.expenditureTrackingSystem.domain.RoleType;
import pt.ist.expenditureTrackingSystem.domain.announcement.AnnouncementProcessState;
import pt.ist.expenditureTrackingSystem.domain.announcements.AnnouncementProcess;
import pt.ist.expenditureTrackingSystem.domain.announcements.AnnouncementProcessStateType;
import pt.ist.expenditureTrackingSystem.domain.organization.Person;

public class RejectAnnouncementProcess extends GenericAnnouncementProcessActivity {

    @Override
    protected boolean isAccessible(AnnouncementProcess process) {
	final Person loggedPerson = getLoggedPerson();
	return loggedPerson != null && userHasRole(RoleType.ACQUISITION_CENTRAL_MANAGER);
    }

    @Override
    protected boolean isAvailable(AnnouncementProcess process) {
	return process.isProcessInState(AnnouncementProcessStateType.SUBMITTED_FOR_APPROVAL);
    }

    @Override
    protected void process(AnnouncementProcess process, Object... objects) {
	new AnnouncementProcessState(process, AnnouncementProcessStateType.REJECTED, (String) objects[0]);
    }

}
