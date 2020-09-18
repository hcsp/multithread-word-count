package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MultiThreadWordCount6 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        return files.parallelStream()
                .map(MultiThreadWordCount6::countWord)
                .reduce(new HashMap<>(), (a, b) -> {
                    a.forEach((key, val) -> b.merge(key, val, Integer::sum));
                    return b;
                });
    }

    public static Map<String, Integer> countWord(File file) {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader bf = new BufferedReader(fileReader);
        Map<String, Integer> collect = new HashMap<>();
        final List<String> strList = bf.lines().collect(Collectors.toList());
        for (String line : strList) {
            final String[] keys = line.split("\\s+");
            for (String key : keys) {
                final Integer val = collect.getOrDefault(key, 0);
                collect.put(key, val + 1);
            }
        }
        return collect;
    }
}
