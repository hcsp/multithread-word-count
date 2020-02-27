package com.github.hcsp.multithread;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link java.util.stream.Stream} implements
 */
public class MultiThreadWordCount3 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        return files.parallelStream()
                .map(MultiThreadWordCount1::countOneFile)
                .reduce(new ConcurrentHashMap<>(), (m1, m2) -> {
                    m1.forEach((k, v) -> m2.merge(k, v, Integer::sum));
                    return m2;
                });
    }
}
