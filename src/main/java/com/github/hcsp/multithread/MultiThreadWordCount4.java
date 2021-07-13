package com.github.hcsp.multithread;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MultiThreadWordCount4 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> resultMap = new HashMap<>();
    private static final ReentrantLock lock = new ReentrantLock();
    private static final Condition isModifyingMap = lock.newCondition();

    public static Map<String, Integer> count(int threadNum, List<File> files) {
        lock.lock();
        try {
            for (int i = 0; i < threadNum; ++i) {
                int finalI = i;
                new Thread(() -> insertContentToMapFromFile(files.get(finalI), finalI == files.size() - 1)).start();
            }
            isModifyingMap.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return resultMap;
    }

    private static void insertContentToMapFromFile(File file, boolean isLast) {
        lock.lock();
        try {
            Map<String, Integer> result = new ProcessFile(file).processFile();
            result.keySet().forEach(key -> {
                if (resultMap.containsKey(key)) {
                    resultMap.put(key, resultMap.get(key) + result.get(key));
                } else {
                    resultMap.put(key, result.get(key));
                }
            });
            if (isLast) {
                isModifyingMap.signal();
            }
        } finally {
            lock.unlock();
        }
    }
}
