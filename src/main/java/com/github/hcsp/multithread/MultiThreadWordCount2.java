package com.github.hcsp.multithread;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MultiThreadWordCount2 {
    // 使用 threadNum 个线程，并发统计文件中各单词的数量
    // 使用 Lock / Condition 实现
    private static final Lock LOCK = new ReentrantLock();
    private static final Condition CONDITION = LOCK.newCondition();
    private static int numOfMergedFiles = 0;
    private static int numOfRunningThread = 0;

    public static Map<String, Integer> count(int threadNum, List<File> files) {
        Map<String, Integer> finalCountResult = new HashMap<>();
        LOCK.lock();
        try {
            for (File file : files) {
                if (numOfRunningThread >= threadNum) {
                    safeAwait();
                }
                new Thread(() -> countSingleFileAndMergeResultToFinalCountResult(file, finalCountResult)).start();
                numOfRunningThread++;
            }
            while (true) {
                if (numOfMergedFiles < files.size()) {
                    safeAwait();
                } else if (numOfMergedFiles == files.size()) {
                    return finalCountResult;
                }
            }
        } finally {
            LOCK.unlock();
        }
    }

    private static void countSingleFileAndMergeResultToFinalCountResult(File file, Map<String, Integer> finalCountResult) {
        LOCK.lock();
        try {
            CountUtil.mergeSingleCountResultToFinalCountResult(
                    CountUtil.getCountResultFromSingleFile(file), finalCountResult);
            numOfMergedFiles++;
            numOfRunningThread--;
            CONDITION.signal();
        } finally {
            LOCK.unlock();
        }
    }

    private static void safeAwait() {
        try {
            CONDITION.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
