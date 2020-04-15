package com.github.hcsp.multithread;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 使用 Object.wait/notify 实现
 */
public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        Object object = new Object();
        AtomicInteger count = new AtomicInteger(files.size());
        List<Map<String, Integer>> result = new CopyOnWriteArrayList<>();
        for (File file : files) {
            new Thread(() -> {
                try {
                    result.add(Util.countWordFromOneFile(file));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                synchronized (object) {
                    count.decrementAndGet();
                    object.notify();
                }
            }).start();
        }
        synchronized (object) {
            while (count.get() > 0) {
                object.wait();
            }
        }
        return Util.mergeMapsFromList(result);
    }
}
