package com.github.hcsp.multithread;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

//Future与线程池
public class MultiThreadWordCount3 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        Map<String, Integer> result = new HashMap<>();
        ForkJoinPool forkJoinPool = new ForkJoinPool(threadNum);
        List<WorkJob> workList = new ArrayList<>();
        for (File file : files) {
            WorkJob work = new WorkJob(file);
            forkJoinPool.submit(work);
            workList.add(work);
        }
        for (WorkJob work : workList) {
            FileUtil.merge(work.join(), result);
        }
        return result;
    }

    static class WorkJob extends RecursiveTask<Map<String, Integer>> {
        private File file;

        WorkJob(File file) {
            this.file = file;
        }

        @Override
        public Map<String, Integer> compute() {
            return FileUtil.work(file);
        }
    }
}
