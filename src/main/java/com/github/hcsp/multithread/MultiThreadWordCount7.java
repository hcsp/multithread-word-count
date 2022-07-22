package com.github.hcsp.multithread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

public class MultiThreadWordCount7 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {
        /**
         * 安全:
         * 协同:
         * ForkJoinPool => submit & Callable & Future
         */
        int step = files.size() / threadNum;
        List<ForkJoinTask<Map<String, Integer>>> forkJoinTaskList = new ArrayList<>();
        ForkJoinPool forkJoinPool = new ForkJoinPool();

        for (int i = 0; i < threadNum; i++) {
            forkJoinTaskList.add(forkJoinPool.submit(new WordCounter7(files.subList(i * step, i == threadNum - 1 ? files.size() : (i + 1) * step))));
        }

        Map<String, Integer> result = new ConcurrentHashMap<>();
        for (ForkJoinTask<Map<String, Integer>> task : forkJoinTaskList) {
            Map<String, Integer> stringIntegerMap = task.get();
            stringIntegerMap.forEach((key, val) -> result.merge(key, val, Integer::sum));
        }

        forkJoinPool.shutdown();

        return result;
    }
}
