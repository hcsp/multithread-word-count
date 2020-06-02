package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MultiThreadWordCount2 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        Map<String, Integer> result = new ConcurrentHashMap<>();
        files.parallelStream().forEach(file -> {
            try {
                Map<String, Integer> subResult = WordCounts.countSingleFile(file);
                WordCounts.mergeSubResult2Result(subResult, result);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
        return result;
    }
}
