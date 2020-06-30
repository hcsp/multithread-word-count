package com.github.hcsp.multithread;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Object.wait/notify
 */
public class MultiThreadWordCount3 {
    /**
     * 用于收集各个线程的结果
     */
    private static final Map<String, Integer> result = new ConcurrentHashMap<>();

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        ExecutorService executors = Executors.newFixedThreadPool(threadNum);
        AtomicInteger queue = new AtomicInteger(files.size());
        files.forEach(file -> executors.submit(() -> {
            Map<String, Long> threadResult = MultiThreadWordCount2.getThreadCountMap(file);
            synchronized (result) {
                //合并线程Map与主线程Map到一个临时Map
                Map<String, Integer> collect = Stream.of(result, threadResult)
                        .flatMap(map -> map.entrySet().stream())
                        .collect(Collectors.toConcurrentMap(Map.Entry::getKey,
                                value -> Integer.valueOf(String.valueOf(value.getValue())),
                                Integer::sum));
                //将临时Map覆盖到主线程Map中
                result.putAll(collect);
                //线程结束时队列-1
                queue.addAndGet(-1);
                result.notify();
            }
        }));
        //等待子线程开始执行
        synchronized (result) {
            while (queue.get() > 0) {
                //如果队列不为0则继续等待
                result.wait();
            }
        }
        executors.shutdown();
        return result;
    }
}
