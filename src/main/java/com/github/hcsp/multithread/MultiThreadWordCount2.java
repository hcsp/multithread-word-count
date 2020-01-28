package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MultiThreadWordCount2 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    //    public static Map<String, Integer> count(int threadNum, List<File> files) {
    //        return null;
    //    }
    public static Map<String, Integer> count(int threadNum, List<File> files) throws IOException, ExecutionException, InterruptedException {
        ExecutorService threadPool2 = Executors.newFixedThreadPool(files.size());
        Map<String, Integer> finalResult = new HashMap<>();
        List<Future<Map<String, Integer>>> fileFutures = new ArrayList<>();

        for (File file : files) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            fileFutures.add(threadPool2.submit(new MultiThreadWordCount1.WorkerJob(reader)));
        }
        MultiThreadWordCount1.getFinalResult(finalResult, fileFutures);
        return finalResult;
    }
}
