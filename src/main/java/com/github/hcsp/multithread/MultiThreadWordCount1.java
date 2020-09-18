package com.github.hcsp.multithread;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 问题的核心有两个：
 * 1. 多线程的等待问题，多个线程执行完任务如何一起结束
 * 2. 多线程处理 map 时，如何保证正确性
 * 2.1 map的正确性
 * 2.2 map中的Integer 累加的正确性
 * 3. HashMap 多线程导致的死循环及数据丢失
 * synchronized + Future
 */
public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
//        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        HashMap<String, Integer> map = new HashMap<>();


        // 判断是文件数多还是AtomicInteger线程多，哪个少就以哪个为基准for循环
        int min = Math.min(threadNum, files.size());
        // 创建一个线程池
        ExecutorService threadPool = Executors.newScheduledThreadPool(threadNum);
        List<Future> futureList = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            futureList.add(threadPool.submit(doCount(files.get(i), map)));
        }
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static Runnable doCount(File file, Map<String, Integer> map) {
        return () -> {
            List<String> list = FileUtils.readFileContent(file.getAbsolutePath());
            for (String line : list) {
                String[] words = line.split(" ");
                for (String word : words) {
                    synchronized (MultiThreadWordCount1.class) {
                        map.put(word, map.getOrDefault(word, 0) + 1);
                    }
                }
            }
        };
    }
}
