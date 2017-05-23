package com.zjlloveo0.help.common.infra;

import java.util.Collection;

public interface TaskRegistry {
    /**
     * register task
     *
     * @param task
     * @return task registered
     */
    Task register(Task task);

    /**
     * unregister task
     *
     * @param task
     * @return task unregistered
     */
    Task unregister(Task task);

    /**
     * task registered
     *
     * @param task
     * @return registered
     */
    boolean registered(Task task);

    /**
     * query task
     *
     * @param key
     * @return task
     */
    Task query(String key);

    /**
     * query all tasks registered
     *
     * @return
     */
    Collection<Task> queryAll();

    /**
     * count
     *
     * @return count
     */
    int count();
}