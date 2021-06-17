package io.th0rgal.guardian.nodes.combat.highcps;

import java.util.Arrays;

final class CPSQueue {
    private final long[] queue;
    private int index;

    public CPSQueue(int size) {
        queue = new long[size];
        Arrays.fill(queue, 0);
        index = -1;
    }

    public double getCPS() {
        double sum = 0;
        for (int i = 1; i < queue.length - 1; i++) {
            long a = queue[(index + i) % queue.length];
            long b = queue[(index + i + 1) % queue.length];
            if (a == 0)
                return 0;
            sum += b - a;
        }
        return (1000 * (queue.length - 1)) / sum;
    }

    public void update() {
        index = (index + 1) % queue.length;
        queue[index] = System.currentTimeMillis();
    }
}
