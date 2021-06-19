package io.th0rgal.guardian.punisher;

public record SerializedPunisherTrigger(String name, boolean concurrent, double trigger, double addition, double multiply) {
}