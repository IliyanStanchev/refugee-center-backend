package bg.tuvarna.diploma_work.storages;

import bg.tuvarna.diploma_work.models.Refugee;

public class RefugeeRegistrationData {

    private long employeeId;

    private Refugee refugee;

    public RefugeeRegistrationData() {
    }

    public long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(long employeeId) {
        this.employeeId = employeeId;
    }

    public Refugee getRefugee() {
        return refugee;
    }

    public void setRefugee(Refugee refugee) {
        this.refugee = refugee;
    }
}
