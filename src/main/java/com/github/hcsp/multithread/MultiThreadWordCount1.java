package com.github.hcsp.multithread;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadWordCount1 {
    // 单个线程操作files，但使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws FileNotFoundException, ExecutionException, InterruptedException {
        Map<String, Integer> finalResult = new HashMap<>();

        for (File file : files) {
            WordCount fileWordCount = new WordCount(threadNum);
            Map<String, Integer> resultFromWordCount = fileWordCount.count(file);

            WordCount.mergeWorkerResultIntoFinalResult(resultFromWordCount, finalResult);
        }

        return finalResult;
    }
}
