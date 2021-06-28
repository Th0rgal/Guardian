package io.th0rgal.guardian.storage.config;

import java.util.List;

public class PunisherAction {

    private String log;
    private List<String> commands;
    private final double threshold;
    public final boolean concurrent;

    public PunisherAction(double threshold, boolean concurrent) {
        this.threshold = threshold;
        this.concurrent = concurrent;
    }

    public boolean hasLog() {
        return log != null;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getLog() {
        return log;
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
