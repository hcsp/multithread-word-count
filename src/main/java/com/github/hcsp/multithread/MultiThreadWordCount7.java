package com.github.hcsp.multithread;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * reduce
 */
public class MultiThreadWordCount7 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        return files.parallelStream()
                .map(MultiThreadWordCount2::getThreadCountMap)
                .map(map -> map.entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().intValue())))
                .reduce(new ConcurrentHashMap<>(), MultiThreadWordCount7::merge);
    }

    public static Map<String, Integer> merge(Map<String, Integer> map1, Map<String, Integer> map2) {
        return Stream.of(map1, map2)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Integer::sum));
    }
}
