package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;

/**
 * 用线程池与feature的方式实现Word Count
 */
public class MultiThreadWordCount5 {
    public static Map<String, Integer> count(File file) {
        Map<String, Integer> map = new HashMap<>();
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (String line : lines) {
                String[] words = line.split("\\s+");
                for (String word : words) {
                    int count = map.getOrDefault(word, 0);
                    map.put(word, count + 1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static Map<String, Integer> merge(Map<String, Integer> result, Map<String, Integer> map){
        Set<String> words = new HashSet<>(result.keySet());
        words.addAll(map.keySet());
        Map<String, Integer> newResult = new HashMap<>();
        for (String word : words) {
            newResult.put(word, result.getOrDefault(word, 0)+map.getOrDefault(word, 0));
        }
        return newResult;
    }

    public static void main(String[] args) {
        List<File> files = Arrays.asList(
            new File("src/main/java/com/github/hcsp/multithread/1.txt"),
            new File("src/main/java/com/github/hcsp/multithread/2.txt"),
            new File("src/main/java/com/github/hcsp/multithread/3.txt")
        );
        //ExecutorService threadPool = Executors.newFixedThreadPool(files.size());
        ExecutorService threadPool = new ThreadPoolExecutor(files.size(), files.size(),
                120, TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
                (runnable)->{
                    Thread factory = new Thread(runnable);
                    factory.setDaemon(true);
                    return factory;});
        Map<String, Integer> resultMap = new HashMap<>();
        for (File file : files) {
            try {
                resultMap = merge(resultMap, threadPool.submit(() -> count(file)).get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        //threadPool.shutdown();
        System.out.println(resultMap);
    }
}
