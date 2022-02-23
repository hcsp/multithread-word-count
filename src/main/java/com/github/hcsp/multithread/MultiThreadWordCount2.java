package com.github.hcsp.multithread;


import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;


public class MultiThreadWordCount2 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    // 使用CountDownLatch

    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        List<Map<String, Integer>> mapList = new CopyOnWriteArrayList<>();
        for (File file : files) {
            new Thread(() -> {
                Map<String, Integer> oneMap = CountUtil.countOneFile(file);
                mapList.add(oneMap);
                countDownLatch.countDown();
            }).start();
        }
        countDownLatch.await();
        return CountUtil.mergeFileResult(mapList);

    }
}


