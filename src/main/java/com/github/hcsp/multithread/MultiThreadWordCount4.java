package com.github.hcsp.multithread;

import java.io.File;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import static java.util.stream.Collectors.toList;

public class MultiThreadWordCount4 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(List<File> files) {
        List<File> concurrentFiles = Collections.synchronizedList(files);
        Map<String, Integer> wordCount = new ConcurrentHashMap<>();
        List<Map<String, Integer>> wordCountList = concurrentFiles.parallelStream().map(file -> new WordCountTask().task(file)).collect(toList());
        for (Map<String, Integer> resultFromWorker : wordCountList) {
            new MergeWorker().mergeWorkerResultIntoFinalResult(resultFromWorker, wordCount);
        }
        return wordCount;
    }
}
