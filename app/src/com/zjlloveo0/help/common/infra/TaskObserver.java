package com.zjlloveo0.help.common.infra;

public interface TaskObserver {
    /**
     * on task result
     *
     * @param task
     * @param results
     */
    void onTaskResult(com.zjlloveo0.help.common.infra.Task task, Object[] results);

    /**
     * on task progress
     *
     * @param task
     * @param params
     */
    void onTaskProgress(com.zjlloveo0.help.common.infra.Task task, Object[] params);
}
