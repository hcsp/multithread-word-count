package com.github.hcsp.multithread;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MultiThreadWordCount9 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {
        /**
         * 安全: ConcurrentHashMap
         * 协同: Collection.parallelStream().forEach(item => // do something)
         */
        return files.parallelStream().map(ProcessFile::processFile).reduce(new HashMap<>(), MultiThreadWordCount9::merge);
    }

    public static Map<String, Integer> merge(Map<String, Integer> stringIntegerMap, Map<String, Integer> stringIntegerMap2) {
        Map<String, Integer> result = new HashMap<>();
        stringIntegerMap.forEach((key, value) -> result.merge(key, value, Integer::sum));
        stringIntegerMap2.forEach((key, value) -> result.merge(key, value, Integer::sum));
        return result;
    }
}
