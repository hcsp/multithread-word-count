package com.github.hcsp.multithread;


import java.io.File;
import java.util.*;
import java.util.concurrent.*;

/**
 * Future
 */
public class MultiThreadWordCount4 {


    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException, ExecutionException {
        Map<String, Integer> freqMap = new HashMap<>();
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);

        List<Future<Map<String, Integer>>> workerList = new ArrayList<>();
        for (File file : files) {
            Work work = new Work(file);
            Future<Map<String, Integer>> result = executorService.submit(work);
            workerList.add(result);
        }
        for (Future<Map<String, Integer>> future : workerList) {
            FileUtil.merge(future.get(), freqMap);
        }
        return freqMap;
    }

    static class Work implements Callable<Map<String, Integer>> {

        File file;

        Work(File file) {
            this.file = file;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            return FileUtil.count(file);
        }
    }
}
