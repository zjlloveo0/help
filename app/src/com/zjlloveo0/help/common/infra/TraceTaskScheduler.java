package com.zjlloveo0.help.common.infra;

public class TraceTaskScheduler extends WrapTaskScheduler {
    public TraceTaskScheduler(com.zjlloveo0.help.common.infra.TaskScheduler wrap) {
        super(wrap);
    }

    @Override
    public void reschedule(com.zjlloveo0.help.common.infra.Task task) {
        trace("reschedule " + task.dump(true));

        super.reschedule(task);
    }

    private final void trace(String msg) {

    }
}
