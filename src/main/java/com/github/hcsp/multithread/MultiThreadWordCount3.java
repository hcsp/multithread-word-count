package com.github.hcsp.multithread;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ConcurrentHashMap + AtomicInter + ExecutorService
 */
public class MultiThreadWordCount3 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        ConcurrentHashMap<String, AtomicInteger> map = new ConcurrentHashMap<>();
        // 创建一个线程池
        ExecutorService threadPool = Executors.newScheduledThreadPool(threadNum);
        for (int i = 0; i < files.size(); i++) {
            threadPool.submit(doCount(files.get(i), map));
        }
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Map<String, Integer> resultMap = new HashMap<>();
        map.forEach((s, atomicInteger) -> resultMap.put(s, atomicInteger.get()));
        return resultMap;
    }

    public static Runnable doCount(File file, Map<String, AtomicInteger> map) {
        return () -> {
            List<String> list = FileUtils.readFileContent(file.getAbsolutePath());
            for (String line : list) {
                String[] words = line.split(" ");
                for (String word : words) {
                    AtomicInteger atomicInteger = map.getOrDefault(word, new AtomicInteger(0));
                    atomicInteger.incrementAndGet();
                    map.put(word, atomicInteger);
                }
            }
        };
    }
}
