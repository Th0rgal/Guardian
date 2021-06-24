package io.th0rgal.guardian.storage.config;

import java.util.List;

public class PunisherAction {

    private String alert;
    private List<String> commands;
    private final double threshold;
    public final boolean concurrent;

    public PunisherAction(double threshold, boolean concurrent) {
        this.threshold = threshold;
        this.concurrent = concurrent;
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

    public double getThreshold() {
        return threshold;
    }
}
