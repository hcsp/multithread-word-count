package com.github.hcsp.multithread;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MultiThreadWordCount2 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    private static final Result result = new Result(new HashMap<>());
    private static final ReentrantLock lock = new ReentrantLock();
    private static final Condition completed = lock.newCondition();

    public static Map<String, Integer> count(int threadNum, List<File> files) {
        /**
         * 安全: JUC 包 Lock
         * 协同: JUC 包 Condition
         */
        int step = files.size() / threadNum;

        lock.lock();  // Acquires the lock.

        try {
            for (int i = 0; i < threadNum; i++) {
                new WordCounter2(result, files.subList(i * step, i == threadNum - 1 ? files.size() : (i + 1) * step), lock, i == threadNum - 1 ? completed : null).start();
            }
            completed.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }


        return result.value;

    }
}


