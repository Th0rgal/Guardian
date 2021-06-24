package io.th0rgal.guardian.punishers;

public record SerializedPunisherTrigger(String name, boolean concurrent, double trigger, double addition, double multiply) {
}