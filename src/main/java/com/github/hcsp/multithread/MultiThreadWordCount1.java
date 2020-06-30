package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.counting;

/**
 * Future与线程池
 */
public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {
        ExecutorService executors = Executors.newFixedThreadPool(threadNum);
        List<Future<?>> results = new ArrayList<>();
        files.forEach(file -> results.add(executors.submit((Callable<Object>) () -> wordCount(file))));

        Map<String, Integer> allResult = new ConcurrentHashMap<>();
        for (Future<?> future : results) {
            Map<String, Integer> tempResult = (Map<String, Integer>) future.get();
            allResult = Stream.of(allResult, tempResult)
                    .flatMap(map -> map.entrySet().stream())
                    .collect(Collectors.toMap(Map.Entry::getKey,
                            value -> Integer.valueOf(String.valueOf(value.getValue())),
                            Integer::sum));
        }
        executors.shutdown();
        return allResult;
    }

    public static Map<String, Long> wordCount(File file) {
        try (Stream<String> lines = Files.lines(Paths.get(file.getAbsolutePath()), Charset.defaultCharset())) {
            return lines.flatMap(i -> Arrays.stream(i.split(" ")))
                    .map(String::toLowerCase)
                    .collect(Collectors.groupingBy(key -> key, counting()));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        MultiThreadWordCount7.count(10, Arrays.asList(
                new File("C:/Users/Geass/AppData/Local/Temp/tmp1304366037002665857"),
                new File("C:/Users/Geass/AppData/Local/Temp/tmp129166811928310870")
        ));
    }
}
