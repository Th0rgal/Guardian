package io.th0rgal.guardian.punishers;

public record SerializedPunisher(String name, boolean concurrent, double addition, double multiply) {
}