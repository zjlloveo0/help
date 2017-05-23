package com.zjlloveo0.help.common.infra;

public interface TaskScheduler {
    /**
     * schedule
     *
     * @param background
     * @param task
     * @param key
     * @param params
     * @return scheduled task
     */
    Task schedule(boolean background, String key, Task task, Object... params);

    /**
     * reschedule
     *
     * @param task
     */
    void reschedule(Task task);

    /**
     * unschedule
     *
     * @param task
     */
    void unschedule(Task task);

    /**
     * scheduled
     *
     * @param key
     * @return Task
     */
    Task scheduled(String key);

    /**
     * count
     *
     * @return count
     */
    int count();

    /**
     * cancelAll
     */
    void cancelAll();
}