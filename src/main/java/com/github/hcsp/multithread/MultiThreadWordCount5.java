package com.github.hcsp.multithread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class MultiThreadWordCount5 {
    // 使用threadNum个线程，并发统计文件中各单词的数量

    public static Map<String, Integer> count(List<File> files) {
        Map<String, Integer> wordCount = new ConcurrentHashMap<>();

        CountDownLatch latch = new CountDownLatch(files.size());
        List<Map<String, Integer>> wordCountList = new ArrayList<>();
        for (File file : files) {
            new Thread(() -> {
                wordCountList.add(new WordCountTask().task(file));
                latch.countDown();
            }).start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        for (Map<String, Integer> resultFromWorker : wordCountList) {
            new MergeWorker().mergeWorkerResultIntoFinalResult(resultFromWorker, wordCount);
        }
        return wordCount;
    }
}
