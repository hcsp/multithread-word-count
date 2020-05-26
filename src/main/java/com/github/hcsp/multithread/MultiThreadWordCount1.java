package com.github.hcsp.multithread;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * CountDownLatch实现
 */
public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        List<Map<String, Integer>> countMapList = new CopyOnWriteArrayList<>();
        Map<String, Integer> finalResult = new HashMap<>();
        for (File file : files) {
            new Thread(() -> {
                Map<String, Integer> oneFileMap = Utils.countOneFile(file);
                countMapList.add(oneFileMap);
                countDownLatch.countDown();
            }).start();
        }
        try {
            countDownLatch.await();
            finalResult = Utils.mergeMapList(countMapList);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return finalResult;
    }
}
