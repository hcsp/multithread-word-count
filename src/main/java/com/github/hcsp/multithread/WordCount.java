package com.github.hcsp.multithread;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WordCount {
    final ExecutorService THREADPOOL;
    Map<String, Integer> finallyResult = new HashMap<>();

    public WordCount(int threadNum) {
        THREADPOOL = Executors.newFixedThreadPool(threadNum);
    }

    public Map<String, Integer> count(List<File> files) throws ExecutionException, InterruptedException {
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        files.forEach(file -> futures.add(THREADPOOL.submit(() -> computationOneFileWordCount(file))));

        for (Future<Map<String, Integer>> future : futures) {
            mergeFilesWordCount(future.get());
        }

        return finallyResult;
    }

    public void mergeFilesWordCount(Map<String, Integer> map) {
        for (String key : map.keySet()) {
            int count = finallyResult.getOrDefault(key, 0) + map.get(key);
            finallyResult.put(key, count);
        }
    }

    public Map<String, Integer> computationOneFileWordCount(File file) throws FileNotFoundException {
        Map<String, Integer> result = new HashMap<>();
        Set<String> words = new HashSet<>();
        Scanner reader = new Scanner(file);

        while (reader.hasNext()) {
            words.add(reader.next());
        }

        words.forEach(word -> result.put(word, result.getOrDefault(word, 1)));

        return result;
    }
}
