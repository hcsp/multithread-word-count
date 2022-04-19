package com.github.hcsp.multithread;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreadWordCount3 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    // 使用 CountDownLatch 实现
    private static CountDownLatch latch;
    private static final AtomicInteger numOfTotalThread = new AtomicInteger(0);

    public static Map<String, Integer> count(int threadNum, List<File> files) {
        latch = new CountDownLatch(Math.min(files.size(), threadNum));
        Map<String, Integer> finalCountResult = new ConcurrentHashMap<>();
        for (File file : files) {
            new Thread(() -> countSingleFileAndMergeResultToFinalCountResult(file, finalCountResult)).start();
            numOfTotalThread.incrementAndGet();
            if (numOfTotalThread.get() % threadNum == 0 && numOfTotalThread.get() < files.size()) {
                safeAwait();
                int numOfRemainingFiles = files.size() - numOfTotalThread.get();
                latch = new CountDownLatch(Math.min(numOfRemainingFiles, threadNum));
            }
        }
        safeAwait();
        return finalCountResult;
    }

    private static void safeAwait() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void countSingleFileAndMergeResultToFinalCountResult(File file, Map<String, Integer> finalCountResult) {
        CountUtil.mergeSingleCountResultToFinalCountResult(
                CountUtil.getCountResultFromSingleFile(file), finalCountResult);
        latch.countDown();
    }
}
