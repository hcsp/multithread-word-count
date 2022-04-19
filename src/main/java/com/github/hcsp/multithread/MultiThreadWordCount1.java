package com.github.hcsp.multithread;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    // 使用 Object.wait / notify 实现
    private static final Lock LOCK = new ReentrantLock();
    private static int numOfMergedFiles = 0;
    private static int numOfRunningThread = 0;

    public static Map<String, Integer> count(int threadNum, List<File> files) {
        Map<String, Integer> finalCountResult = new HashMap<>();
        synchronized (LOCK) {
            for (File file : files) {
                if (numOfRunningThread >= threadNum) {
                    safeWait();
                }
                new Thread(() -> countSingleFileAndMergeResultToFinalCountResult(file, finalCountResult)).start();
                numOfRunningThread++;
            }
            while (true) {
                if (numOfMergedFiles < files.size()) {
                    safeWait();
                } else if (numOfMergedFiles == files.size()) {
                    return finalCountResult;
                } else {
                    throw new RuntimeException("合并的次数大于文件总数");
                }
            }
        }
    }

    private static void countSingleFileAndMergeResultToFinalCountResult(File file, Map<String, Integer> finalCountResult) {
        synchronized (LOCK) {
            CountUtil.mergeSingleCountResultToFinalCountResult(
                    CountUtil.getCountResultFromSingleFile(file), finalCountResult);
            numOfMergedFiles++;
            numOfRunningThread--;
            LOCK.notify();
        }
    }

    private static void safeWait() {
        try {
            LOCK.wait();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
