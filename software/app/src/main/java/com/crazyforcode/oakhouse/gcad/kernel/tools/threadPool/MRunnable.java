package com.crazyforcode.oakhouse.gcad.kernel.tools.threadPool;

public class MRunnable implements Runnable {

    private boolean cancelTask = false;

    private MHandler handler = null;

    private long time = Integer.MAX_VALUE;

    public MRunnable(MHandler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        runBefore();

        if(!cancelTask)
            running();

        runAfter();
    }

    public void runAfter() {

    }

    public void runBefore() {

    }

    public void setMillisecond(long time) {
        this.time = time;
    }

    public long getMillisecond() {
        return time;
    }

    public boolean hasTimeLimit() {
        return time != Integer.MAX_VALUE;
    }

    public MHandler getMHandler() {
        return handler;
    }

    public void running() {
        throw new UnsupportedOperationException();
    }

    public void setCancelTaskUnit(boolean cancelTask) {
        this.cancelTask = cancelTask;
    }
}
