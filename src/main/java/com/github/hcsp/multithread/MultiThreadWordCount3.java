package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

public class MultiThreadWordCount3 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(files.size());
        List<Map<String, Integer>> mapList = new CopyOnWriteArrayList<>();
        files.forEach(file -> {
            new Thread(() -> {
                try {
                    mapList.add(MultiThreadWordCount1.countOneFile(file));
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            }).start();
        });

        latch.await();
        return MultiThreadWordCount1.merge(mapList);
    }
}
