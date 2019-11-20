package com.github.hcsp.multithread;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.github.hcsp.multithread.MultiThreadWordCount1.countSingleFile;

/**
 * 使用{@link Lock} 和 {@link Condition}实现多线程的WordCount
 */
public class MultiThreadWordCount3 {

    private static AtomicInteger numsOfRunningThread;
    private static Lock lock = new ReentrantLock();
    private static Condition allFinished = lock.newCondition();

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        numsOfRunningThread = new AtomicInteger(files.size());
        ExecutorService service = Executors.newFixedThreadPool(threadNum);
        List<Map<String, Integer>> results = new CopyOnWriteArrayList<>();

        for (File file : files) {
            service.submit(() -> {
                results.add(countSingleFile(file));
                lock.lock();
                try {
                    numsOfRunningThread.decrementAndGet();
                    allFinished.signalAll();
                } finally {
                    lock.unlock();
                }
            });
        }

        lock.lock();
        try {
            while (numsOfRunningThread.get() > 0) {
                allFinished.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        service.shutdown();
        return aggMap(results);
    }

    /**聚合Map的结果*/
    private static Map<String, Integer> aggMap(List<Map<String, Integer>> results) {
        Map<String, Integer> finalResult = new HashMap<>();
        for (Map<String, Integer> map : results) {
            MultiThreadWordCount1.merge(finalResult, map);
        }
        return finalResult;
    }
}
