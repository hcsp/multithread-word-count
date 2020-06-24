package com.github.hcsp.multithread;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class MultiThreadWordCount3 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    //    public static Map<String, Integer> count(int threadNum, List<File> files) {
    //        return null;
    //    }
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        List<Map<String, Integer>> results = new ArrayList<>();
        Map<String, Integer> countResult = new HashMap<>();
        buildDetector(threadNum, files, results, countDownLatch);
        for (Map<String, Integer> waitforMerge : results) {
            mergeResulttoFinal(waitforMerge, countResult);
        }
        return countResult;
    }

    private static void buildDetector(int threadNum, List<File> files, List<Map<String, Integer>> results, CountDownLatch countDownLatch) {
        for (int i = 0; i < threadNum; i++) {
            new FileDetector(results, null, files.get(i), countDownLatch).start();
        }
    }

    private static void mergeResulttoFinal(Map<String, Integer> waitforMerge, Map<String, Integer> countResult) {
        for (Map.Entry<String, Integer> entry : waitforMerge.entrySet()) {
            String word = entry.getKey();
            int i = countResult.getOrDefault(word, 0) + entry.getValue();
            countResult.put(word, i);
        }
    }
}
