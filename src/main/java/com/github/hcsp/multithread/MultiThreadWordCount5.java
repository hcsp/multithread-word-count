package com.github.hcsp.multithread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class MultiThreadWordCount5 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        ForkJoinPool forkJoinPool = new ForkJoinPool(threadNum);
        List<CountWordsWork> works = new ArrayList<>();
        List<Map<String, Integer>> results = new ArrayList<>();

        for (File file : files) {
            CountWordsWork countWordsJob = new CountWordsWork(file);
            forkJoinPool.submit(countWordsJob);
            works.add(countWordsJob);
        }

        for (CountWordsWork work : works) {
            results.add(work.join());
        }

        return FileUtil.sumWords(results);
    }


    private static class CountWordsWork extends RecursiveTask<Map<String, Integer>> {
        private File file;

        CountWordsWork(File file) {
            this.file = file;
        }

        @Override
        protected Map<String, Integer> compute() {
            return FileUtil.countWords(file);
        }
    }
}
