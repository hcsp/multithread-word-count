package com.github.hcsp.multithread;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class MultiThreadWordCount4 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        Map<String, Integer> count = new ConcurrentHashMap<>();
        long t0 = System.currentTimeMillis();
        List<Map<String, Integer>> result = files
                .parallelStream()
                .map(GetWordInFile::getWordCountFormFile)
                .collect(Collectors.toList());
        for (Map<String, Integer> map : result) {
            GetWordInFile.mapAdd(count, map);
        }
        System.out.println("time" + (System.currentTimeMillis() - t0));
        return count;
    }
}
