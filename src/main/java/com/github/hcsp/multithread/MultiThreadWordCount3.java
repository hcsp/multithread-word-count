package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import static com.github.hcsp.multithread.Utils.countFile;
import static com.github.hcsp.multithread.Utils.mergeIntoFirstMap;

public class MultiThreadWordCount3 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        Map<String, Integer> result = new ConcurrentHashMap<>();
        CountDownLatch latch = new CountDownLatch(files.size());
        for (File file : files) {
            new Thread(() -> {
                try {
                    Map<String, Integer> countResult = countFile(file);
                    synchronized (result) {
                        mergeIntoFirstMap(result, countResult); //这里的map全是concurrentHashMap
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    latch.countDown();
                }
            }).start();
        }

        latch.await();

        return result;
    }

    //从这里看，特定场景下，使用这些现有的例如countdownlatch，大大简化代码复杂度；
}
