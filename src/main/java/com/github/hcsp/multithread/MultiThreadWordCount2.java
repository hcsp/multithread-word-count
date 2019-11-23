package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class MultiThreadWordCount2 {
    // 使用future线程池
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException,
            InterruptedException {
        Map<String, Integer> result = new HashMap<>();
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        for (File file : files) {
            Future<Map<String, Integer>> wordCounts = threadPool.submit(new CountFile(file));
            Map<String, Integer> single = wordCounts.get();
            for (Map.Entry<String, Integer> entry : single.entrySet()) {
                result.put(entry.getKey(), result.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
        }

        return result;
    }
}

class CountFile implements Callable<Map<String, Integer>> {

    private File file;

    CountFile(File file) {
        this.file = file;
    }

    @Override
    public Map<String, Integer> call() throws Exception {
        return wordsCount(file);
    }

    static Map<String, Integer> wordsCount(File file) throws IOException {
        Map<String, Integer> wordsResult = new HashMap<>();
        List<String> words = Files.readAllLines(file.toPath()).stream().flatMap(line -> Arrays.stream(line.split("\\s" +
                "+"))).collect(Collectors.toList());
        for (String word : words) {
            wordsResult.put(word, wordsResult.getOrDefault(word, 0) + 1);
        }
        return wordsResult;
    }
}
