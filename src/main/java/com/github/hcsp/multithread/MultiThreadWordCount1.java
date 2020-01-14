package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

// Object.wait/notify
public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        Object object = new Object();
        Map<String, Integer> resultMap = new ConcurrentHashMap<>();
        AtomicInteger restFileNumber = new AtomicInteger(files.size());
        for (File file : files) {
            new Thread(() -> {
                try {
                    Map<String, Integer> map = Common.countOneFile(file);
                    synchronized (object) {
                        try {
                            Common.mergeToMap(resultMap, map);
                        } finally {
                            if (restFileNumber.decrementAndGet() == 0) {
                                object.notify();
                            }
                        }

                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }

        synchronized (object) {
            while (restFileNumber.intValue() > 0) {
                object.wait();
            }
        }
        return resultMap;
    }
}
