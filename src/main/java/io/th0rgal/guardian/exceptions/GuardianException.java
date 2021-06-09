package io.th0rgal.guardian.exceptions;

public class GuardianException extends Exception {

    public final String advice;

    public GuardianException(String advice, String errorMessage) {
        super(errorMessage);
        this.advice = advice;
    }
}