package com.github.hcsp.multithread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * parallelStream()
 */
public class MultiThreadWordCount6 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        List<Map<String, Long>> resultArray = new ArrayList();
        files.parallelStream().forEach(file -> resultArray.add(MultiThreadWordCount2.getThreadCountMap(file)));
        Map<String, Integer> allResult = new ConcurrentHashMap<>();
        //合并线程Map结果
        for (Map<String, Long> tempResult : resultArray) {
            allResult = Stream.of(allResult, tempResult)
                    .flatMap(map -> map.entrySet().stream())
                    .collect(Collectors.toMap(Map.Entry::getKey,
                            value -> Integer.valueOf(String.valueOf(value.getValue())),
                            Integer::sum));
        }
        return allResult;
    }
}
