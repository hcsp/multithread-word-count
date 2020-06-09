package com.github.hcsp.multithread;

import static com.github.hcsp.multithread.WordCounts.countSingleFile;
import static com.github.hcsp.multithread.WordCounts.mergeSubResult2Result;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MultiThreadWordCount2 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    //    public static Map<String, Integer> count(int threadNum, List<File> files) {
    //        return null;
    //    }
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException, ExecutionException {
      ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
      Map<String, Integer> result = new HashMap<>();
      List<Future<Map<String, Integer>>> futures = new ArrayList<>();
      for (File file : files) {
        futures.add(threadPool.submit(() -> countSingleFile(file)));
      }
      for (Future<Map<String, Integer>> future : futures) {
        Map<String, Integer> subResult = future.get();
        mergeSubResult2Result(subResult, result);
      }
      return result;
    }
}
