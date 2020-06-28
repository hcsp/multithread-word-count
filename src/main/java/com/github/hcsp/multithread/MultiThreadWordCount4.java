package com.github.hcsp.multithread;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Lock/Condition
 */
public class MultiThreadWordCount4 {
    /**
     * 用于收集各个线程的结果
     */
    private static Map<String, Integer> result = new ConcurrentHashMap<>();

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        Lock lock = new ReentrantLock();//~=synchronized
        Condition condition = lock.newCondition();//~=Object.wait/notify
        AtomicInteger queue = new AtomicInteger(files.size());
        ExecutorService executors = Executors.newFixedThreadPool(threadNum);
        files.forEach(file -> executors.submit(() -> {
            Map<String, Long> threadResult = MultiThreadWordCount2.getThreadCountMap(file);
            try {
                lock.lock();
                //合并线程Map与主线程Map到一个临时Map
                Map<String, Integer> collect = Stream.of(result, threadResult)
                        .flatMap(map -> map.entrySet().stream())
                        .collect(Collectors.toConcurrentMap(Map.Entry::getKey,
                                value -> Integer.valueOf(String.valueOf(value.getValue())),
                                Integer::sum));
                //将临时Map覆盖到主线程Map中
                result.putAll(collect);
                queue.decrementAndGet();
                condition.signal();
            } finally {
                lock.unlock();
            }
        }));

        try {
            lock.lock();
            while (queue.get() > 0) {
                condition.await();
            }
        } finally {
            lock.unlock();
        }
        executors.shutdown();
        return result;
    }
}
