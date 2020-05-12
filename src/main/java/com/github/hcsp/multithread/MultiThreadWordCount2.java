package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MultiThreadWordCount2 {
    // 使用threadNum个线程，并发统计文件中各单词的数量 Lock/Condition

    public static void main(String[] args) throws Exception {
        List<File> files = Arrays.asList(new File("C:/Users/sunp/git/test-files/test1.txt"),
                new File("C:/Users/sunp/git/test-files/test2.txt"), new File("C:/Users/sunp/git/test-files/test3.txt"),
                new File("C:/Users/sunp/git/test-files/test4.txt"));
        System.out.println(count(4, files));
    }

    public static Map<String, Integer> count(int threadNum, List<File> files) throws IOException, InterruptedException {
        final Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        Map<String, Integer> result = new HashMap<>();
        List<Map<String, Integer>> middleList = new ArrayList<>();
        AtomicInteger chucksCount = new AtomicInteger(0);
        List<List<File>> fileChucks = MultiThreadWordUtility.fileSplit(threadNum, files);
        for (List<File> fileChuck : fileChucks) {
            new Thread(() -> {
                Map<String, Integer> middleResult = null;
                lock.lock();
                try {
                    middleResult = MultiThreadWordUtility.fileChuckCount(fileChuck);
                    middleList.add(middleResult);
                    while (chucksCount.addAndGet(1) == fileChucks.size()) {
                        condition.signal();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }).start();
        }

        lock.lock();
        try {
            while (chucksCount.get() - 1 != fileChucks.size()) {
                condition.await();
            }
            for (Map<String, Integer> stringIntegerMap : middleList) {
                stringIntegerMap.forEach((key, value) -> {
                    result.put(key, result.getOrDefault(key, 0) + value);
                });
            }
        } finally {
            lock.unlock();
        }

        return result;
    }
}
