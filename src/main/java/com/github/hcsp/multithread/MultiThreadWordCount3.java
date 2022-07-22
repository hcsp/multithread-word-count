package com.github.hcsp.multithread;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class MultiThreadWordCount3 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    private static final Result result = new Result(new ConcurrentHashMap<>());

    public static Map<String, Integer> count(int threadNum, List<File> files) {
        /**
         * 安全: JUC 包 ConcurrentHashMap
         * 协同: JUC 包 CountDownLatch
         */
        int step = files.size() / threadNum;

        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(threadNum);

        for (int i = 0; i < threadNum; ++i) {
            // create and start threads
            new WordCounter3(result, files.subList(i * step, i == threadNum - 1 ? files.size() : (i + 1) * step), startSignal, doneSignal).start();
        }

        startSignal.countDown();      // let all threads proceed
        try {
            doneSignal.await();           // wait for all to finish
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return result.value;
    }
}
