package io.th0rgal.guardian.nodes;

import io.th0rgal.guardian.GuardianPlayer;

public abstract class Node {

    protected final String name;

    public Node(String name) {
        this.name = name;
    }

    public abstract void enable();

    public abstract void disable();

    public abstract void enable(GuardianPlayer player);

    public abstract void disable(GuardianPlayer player);

}
