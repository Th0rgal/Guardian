package io.th0rgal.guardian.config;

import java.util.List;

public class PunisherAction {

    private String alert;
    private List<String> commands;

    public PunisherAction(double threshold) {

    }

    public boolean hasAlert() {
        return alert != null;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public String getAlert() {
        return alert;
    }

    public boolean hasCommands() {
        return commands != null && !commands.isEmpty();
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    public List<String> getCommands() {
        return commands;
    }

}
