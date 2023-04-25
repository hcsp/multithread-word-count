package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class MultiThreadWordCount3 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    //    public static Map<String, Integer> count(int threadNum, List<File> files) {
    //        return null;
    //    }
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        Map<String, Integer> results = new HashMap<>();
        for (final File file : files) {
            readSingleFile(file, results);
        }
        return results;
    }

    private static void readSingleFile(File file, Map<String, Integer> result) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            Stream<String> linesStream = br.lines();
            linesStream.parallel().forEach(s -> countSingleLine(result, s));
            linesStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void countSingleLine(Map<String, Integer> result, String line) {
        String[] words = line.split(" ");
        for (String word : words) {
            result.put(word, result.getOrDefault(word, 0) + 1);
        }
    }
}
