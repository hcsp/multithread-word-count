package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量 Lock/Condition

    public static void main(String[] args) throws Exception {
        List<File> files = Arrays.asList(new File("C:/Users/sunp/git/test-files/test1.txt"),
                new File("C:/Users/sunp/git/test-files/test2.txt"), new File("C:/Users/sunp/git/test-files/test3.txt"),
                new File("C:/Users/sunp/git/test-files/test4.txt"));
        System.out.println(count(4, files));
    }

    public static Map<String, Integer> count(int threadNum, List<File> files) throws Exception {
        final Object lock = new Object();
        AtomicInteger chucksCount = new AtomicInteger(0);
        List<List<File>> fileChucks = MultiThreadWordUtility.fileSplit(threadNum, files);
        Map<String, Integer> result = new HashMap<>();
        List<Map<String, Integer>> middleList = new ArrayList<>();
        for (List<File> fileChuck : fileChucks) {
            new Thread(() -> {
                Map<String, Integer> middleResult = null;
                try {
                    middleResult = MultiThreadWordUtility.fileChuckCount(fileChuck);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (lock) {
                    middleList.add(middleResult);
                    while (chucksCount.addAndGet(1) == fileChucks.size()) {
                        lock.notify();
                    }
                }
            }).start();
        }
        synchronized (lock) {
            while (chucksCount.get() - 1 != fileChucks.size()) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (Map<String, Integer> stringIntegerMap : middleList) {
                stringIntegerMap.forEach((key, value) -> {
                    result.put(key, result.getOrDefault(key, 0) + value);
                });
            }
        }
        return result;
    }
}
