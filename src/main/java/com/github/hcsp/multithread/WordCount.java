package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public class WordCount {
    public WordCount(int threadNum) {}

    // 统计文件中各单词的数量
    public Map<String, Integer> count(List<File> file) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        List<Future<Map<String, Integer>>> futureList = new ArrayList<>();
        for (File oneFile:
             file) {
            final Future<Map<String, Integer>> submit = executor.submit(() -> {
                try {
                    FileReader fileReader = new FileReader(oneFile);
                    BufferedReader br = new BufferedReader(fileReader);

                    String line;
                    Map<String, Integer> wordsMap = new HashMap<>();
                    while ((line = br.readLine()) != null) {
                        String[] words = line.split(" ");
                        for (String word :
                                words) {
                            wordsMap.put(word, wordsMap.getOrDefault(word, 0) + 1);
                        }
                    }

                    return wordsMap;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            });
            futureList.add(submit);
        }
        Map<String, Integer> finalWords = new HashMap<>();
        for (Future<Map<String, Integer>> future:
             futureList) {
            try {
                Map<String, Integer> words = future.get();
                for (String key:
                    words.keySet()) {
                    finalWords.put(key, finalWords.containsKey(key) ? finalWords.get(key) + words.get(key) : words.get(key));
                }

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return finalWords;
    }
}
