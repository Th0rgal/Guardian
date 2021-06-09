package io.th0rgal.guardian.exceptions;

public class ParsingException extends GuardianException {

    public ParsingException(String errorMessage) {
        super("This error is related to an invalid configuration. It is very unlikely that this is a Guardian problem, please read the additional information carefully.", errorMessage);
    }

}
