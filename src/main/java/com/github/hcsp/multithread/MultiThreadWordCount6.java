package com.github.hcsp.multithread;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MultiThreadWordCount6 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    // 使用 parallelStream() 实现
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        Map<String, Integer> finalCountResult = new ConcurrentHashMap<>();
        files.parallelStream().forEach(file -> CountUtil.mergeSingleCountResultToFinalCountResult(
                CountUtil.getCountResultFromSingleFile(file), finalCountResult));
        return finalCountResult;
    }
}
