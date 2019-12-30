package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MultiThreadWordCount4 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {
        ForkJoinPool forkJoinPool = new ForkJoinPool(threadNum);

        ForkJoinTask<Map<String, Integer>> submit = forkJoinPool.submit(new MyFork(files));
        return submit.get();
    }

    static class MyFork extends RecursiveTask<Map<String, Integer>> {
        List<File> files;

        MyFork(List<File> files) {
            this.files = files;
        }

        @Override
        protected Map<String, Integer> compute() {
            Map<String, Integer> wordCountTemp = new HashMap<>();
            try {
                File file = files.get(0);
                List<String> readAllLines = Files.readAllLines(Paths.get(file.getPath()));
                for (String lines : readAllLines) {
                    String[] words = lines.split("\\s");
                    for (String word : words) {
                        wordCountTemp.put(word, wordCountTemp.getOrDefault(word, 0) + 1);
                    }
                }
                if (files.size() == 1) {
                    return wordCountTemp;
                }
                ForkJoinTask<Map<String, Integer>> fork = new MyFork(files.subList(1, files.size())).fork();
                merge(wordCountTemp, fork.get());
            } catch (IOException | InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return wordCountTemp;
        }
    }

    public static void merge(Map<String, Integer> temp1, Map<String, Integer> temp2) {
        synchronized (MyFork.class) {
            temp2.forEach((key, value) -> temp1.put(key, temp1.getOrDefault(key, 0) + value));
        }
    }
}
