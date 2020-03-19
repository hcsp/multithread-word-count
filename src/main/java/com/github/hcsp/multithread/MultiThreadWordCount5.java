package com.github.hcsp.multithread;


import java.io.File;
import java.util.*;
import java.util.concurrent.*;

/**
 * ForkJoinPool
 */
public class MultiThreadWordCount5 {

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException, ExecutionException {
        Map<String, Integer> freqMap = new HashMap<>();
        ForkJoinPool forkJoinPool = new ForkJoinPool(threadNum);
        List<Work> workList = new ArrayList<>();
        for (File file : files) {
            Work work = new Work(file);
            forkJoinPool.submit(work);
            workList.add(work);
        }
        for (Work work : workList) {
            FileUtil.merge(work.join(), freqMap);
        }
        return freqMap;
    }

    static class Work extends RecursiveTask<Map<String, Integer>> {

        File file;

        Work(File file) {
            this.file = file;
        }

        @Override
        protected Map<String, Integer> compute() {
            return  FileUtil.count(file);
        }
    }
}
