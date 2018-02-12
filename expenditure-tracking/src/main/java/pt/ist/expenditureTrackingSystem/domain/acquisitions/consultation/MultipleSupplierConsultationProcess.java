package pt.ist.expenditureTrackingSystem.domain.acquisitions.consultation;

import java.util.ArrayList;
import java.util.List;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.messaging.core.domain.Message;
import org.joda.time.LocalDate;

import module.workflow.activities.ActivityInformation;
import module.workflow.activities.GiveProcess;
import module.workflow.activities.ReleaseProcess;
import module.workflow.activities.StealProcess;
import module.workflow.activities.TakeProcess;
import module.workflow.activities.WorkflowActivity;
import module.workflow.domain.WorkflowProcess;
import module.workflow.domain.WorkflowSystem;
import pt.ist.expenditureTrackingSystem.domain.ExpenditureTrackingSystem;
import pt.ist.expenditureTrackingSystem.domain.acquisitions.Material;
import pt.ist.expenditureTrackingSystem.domain.acquisitions.PaymentProcessYear;
import pt.ist.fenixframework.Atomic;

public class MultipleSupplierConsultationProcess extends MultipleSupplierConsultationProcess_Base {

    private static List<WorkflowActivity<? extends MultipleSupplierConsultationProcess, ? extends ActivityInformation<? extends MultipleSupplierConsultationProcess>>> activities =
            new ArrayList<WorkflowActivity<? extends MultipleSupplierConsultationProcess, ? extends ActivityInformation<? extends MultipleSupplierConsultationProcess>>>();
    static {
        activities.add(new TakeProcess<MultipleSupplierConsultationProcess>());
        activities.add(new GiveProcess<MultipleSupplierConsultationProcess>());
        activities.add(new ReleaseProcess<MultipleSupplierConsultationProcess>());
        activities.add(new StealProcess<MultipleSupplierConsultationProcess>());
    }

    public MultipleSupplierConsultationProcess() {
        final PaymentProcessYear paymentProcessYear = PaymentProcessYear.getPaymentProcessYearByYear(LocalDate.now().getYear());
        setYear(paymentProcessYear);
        setProcessNumber(generateProcessNumber(paymentProcessYear));
        setCreator(Authenticate.getUser());
        setWorkflowSystem(WorkflowSystem.getInstance());
        new MultipleSupplierConsultation(this);
    }

    private String generateProcessNumber(final PaymentProcessYear paymentProcessYear) {
        final Integer number = paymentProcessYear.nextAcquisitionProcessYearNumber();
        final ExpenditureTrackingSystem system = ExpenditureTrackingSystem.getInstance();
        return system.getInstitutionalProcessNumberPrefix() + "/" + paymentProcessYear.getYear() + "/PC" + number;
    }

    @Override
    public <T extends WorkflowActivity<? extends WorkflowProcess, ? extends ActivityInformation>> List<T> getActivities() {
        return (List<T>) activities;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public User getProcessCreator() {
        return getCreator();
    }

    @Override
    public void notifyUserDueToComment(final User user, final String comment) {
        Message.fromSystem().to(Group.users(user)).template("expenditures.consultation.comment")
        .parameter("process", getProcessNumber())
        .parameter("commenter", Authenticate.getUser().getProfile().getFullName())
        .parameter("comment", comment)
        .parameter("applicationUrl", CoreConfiguration.getConfiguration().applicationUrl())
        .and()
        .send();
    }

    @Atomic
    public static MultipleSupplierConsultationProcess create(final String description, final Material material, final String justification,
            final String contractType) {
        final MultipleSupplierConsultationProcess process = new MultipleSupplierConsultationProcess();
        return process;
    }
    
}
