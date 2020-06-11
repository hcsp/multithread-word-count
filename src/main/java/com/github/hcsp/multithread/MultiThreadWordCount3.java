package com.github.hcsp.multithread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MultiThreadWordCount3 {
//     使用threadNum个线程，并发统计文件中各单词的数量
        public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {
            Map<String, Integer> count = new ConcurrentHashMap<>();
            ForkJoinPool forkJoinPool = new ForkJoinPool(threadNum);
            long t0 = System.currentTimeMillis();
            List<Future<Map<String, Integer>>> futures = new ArrayList<>();
            for (File file : files) {
                futures.add(forkJoinPool.submit(() -> GetWordInFile.getWordCountFormFile(file)));
            }
            for (Future<Map<String, Integer>> future: futures) {
                GetWordInFile.mapAdd(count, future.get());
            }
            System.out.println("time" + (System.currentTimeMillis() - t0));
            return count;
        }
}
