package com.github.hcsp.multithread;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.github.hcsp.multithread.MultiThreadWordCount5.count;
import static com.github.hcsp.multithread.MultiThreadWordCount5.merge;

/**
 * 使用lock/condition完成Word Count
 */
public class MultiThreadWordCount1 {
    private static Lock lock = new ReentrantLock();
    private static Condition condition = lock.newCondition();

    public static void main(String[] args) throws InterruptedException {
        List<File> files = Arrays.asList(
                new File("src/main/java/com/github/hcsp/multithread/1.txt"),
                new File("src/main/java/com/github/hcsp/multithread/2.txt"),
                new File("src/main/java/com/github/hcsp/multithread/3.txt")
        );
        AtomicInteger endNum = new AtomicInteger();
        endNum.set(files.size());
        Map<String, Integer> resultMap = new HashMap<>();
        List<Map<String, Integer>> resultList = new ArrayList<>();
        resultList = Collections.synchronizedList(resultList);
        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            List<Map<String, Integer>> finalResultList = resultList;
            new Thread(() -> {
                Map<String, Integer> map = count(file);
                finalResultList.add(map);
                try {
                    lock.lock();
                    endNum.decrementAndGet();
                    condition.signal();
                } finally {
                    lock.unlock();
                }
            }).start();
        }
        try {
            lock.lock();
            while (endNum.get() > 0) {
                condition.await();
            }
        } finally {
            lock.unlock();
        }
        for (Map<String, Integer> map : resultList) {
            resultMap = merge(resultMap, map);
        }
        System.out.println(resultMap);
    }
}
