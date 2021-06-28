package io.th0rgal.guardian;

public enum Permission {


    USE_COMMAND_FREEZE("guardian.freeze"),
    USE_COMMAND_INFO("guardian.info"),
    USE_COMMAND_INSPECT("guardian.inspect"),
    USE_COMMAND_LOGS("guardian.logs"),;

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String toString() {
        return this.permission;
    }


}
