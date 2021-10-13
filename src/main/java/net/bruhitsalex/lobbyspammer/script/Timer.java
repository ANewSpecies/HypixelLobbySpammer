package net.bruhitsalex.lobbyspammer.script;

public class Timer {

    public final int tickLength;
    public final Runnable action;
    public int tick;

    public Timer(int tickLength, Runnable action) {
        this.tickLength = tickLength;
        this.action = action;
        tick = 0;
    }

}
