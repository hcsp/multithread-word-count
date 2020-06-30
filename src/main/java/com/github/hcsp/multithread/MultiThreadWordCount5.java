package com.github.hcsp.multithread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ForkJoinPool
 */
public class MultiThreadWordCount5 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        List<Future<?>> futures = new ArrayList<>();
        files.forEach(file ->
                futures.add(forkJoinPool.submit(() ->
                        MultiThreadWordCount2.getThreadCountMap(file))));

        //合并线程Map与主线程Map到一个临时Map
        Map<String, Integer> allResult = new ConcurrentHashMap<>();
        for (Future<?> future : futures) {
            Map<String, Integer> tempResult = (Map<String, Integer>) future.get();
            allResult = Stream.of(allResult, tempResult)
                    .flatMap(map -> map.entrySet().stream())
                    .collect(Collectors.toMap(Map.Entry::getKey,
                            value -> Integer.valueOf(String.valueOf(value.getValue())),
                            Integer::sum));
        }
        forkJoinPool.shutdown();
        return allResult;
    }
}
