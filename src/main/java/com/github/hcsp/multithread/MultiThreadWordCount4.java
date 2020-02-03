package com.github.hcsp.multithread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static com.github.hcsp.multithread.Utils.countFile;
import static com.github.hcsp.multithread.Utils.mergeIntoFirstMap;

public class MultiThreadWordCount4 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {
        Map<String, Integer> result = new ConcurrentHashMap<>();
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();

        //这里是用线程池，多线程处理
        for (File file : files) {
            futures.add(executorService.submit(() -> countFile(file)));
        }
        //这里是主线程，等待汇总结果，get实现了等待；
        for (Future<Map<String, Integer>> future : futures) {
            mergeIntoFirstMap(result, future.get());
        }

        return result;
    }
}
