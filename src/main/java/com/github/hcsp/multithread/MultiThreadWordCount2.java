package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.counting;

/**
 * CountDownLatch
 */
public class MultiThreadWordCount2 {
    /**
     * 用于收集各个线程的结果
     */
    private static Map<String, Integer> result = new ConcurrentHashMap<>();

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(files.size());
        ExecutorService executors = Executors.newFixedThreadPool(threadNum);
        files.forEach(file -> executors.submit(() -> {
            Map<String, Long> threadResult = getThreadCountMap(file);
            synchronized (result) {
                //合并线程Map与主线程Map到一个临时Map
                Map<String, Integer> collect = Stream.of(result, threadResult)
                        .flatMap(map -> map.entrySet().stream())
                        .collect(Collectors.toConcurrentMap(Map.Entry::getKey,
                                value -> Integer.valueOf(String.valueOf(value.getValue())),
                                Integer::sum));
                //将临时Map覆盖到主线程Map中
                result.putAll(collect);
                latch.countDown();
            }
        }));
        latch.await();
        executors.shutdown();
        return result;
    }

    public static Map<String, Long> getThreadCountMap(File file) {
        try (Stream<String> lines = Files.lines(Paths.get(file.getAbsolutePath()), Charset.defaultCharset())) {
            //获得线程负责统计的结果Map
            return lines.flatMap(i -> Arrays.stream(i.split(" ")))
                    .map(String::toLowerCase)
                    .collect(Collectors.groupingBy(key -> key, counting()));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
