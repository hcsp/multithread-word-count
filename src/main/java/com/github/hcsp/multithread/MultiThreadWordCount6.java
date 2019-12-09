package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 使用并发流实现线程间协作
 */
public class MultiThreadWordCount6 {
    public static void main(String[] args) {
        List<File> files = Arrays.asList(
                new File("1.txt"),
                new File("2.txt")
        );

        Map<String, Integer> finalResultMap = new HashMap<>();
        files.parallelStream()
                .forEach(file -> {
                    Map<String, Integer> wordCount = count(file);
                    merge(finalResultMap, wordCount);
                });

//        Map<String, Integer> finalResultMap = files.parallelStream()
//                .map(MultiThreadWordCount6::count)
//                .reduce(new HashMap<>(), MultiThreadWordCount6::merge);

        System.out.println(finalResultMap);
    }

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(File file) {
        Map<String, Integer> wordToCountMap = new HashMap<>();
        List<String> lines = null;
        try {
            lines = Files.readAllLines(file.toPath());
            for (String line : lines) {
                String[] words = line.split("\\s+");
                for (String word : words) {
                    int count = wordToCountMap.getOrDefault(word, 0);
                    wordToCountMap.put(word, count + 1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return wordToCountMap;
    }

    private static Map<String, Integer> merge(Map<String, Integer> map1, Map<String, Integer> map2) {
        for (String word : map2.keySet()) {
            map1.put(word,
                    map1.getOrDefault(word, 0) + map2.getOrDefault(word, 0));
        }
        return map1;
    }
}
