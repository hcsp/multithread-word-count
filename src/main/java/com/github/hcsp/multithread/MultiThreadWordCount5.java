package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

// fork-join
public class MultiThreadWordCount5 {
    //使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {
        ForkJoinPool pool = new ForkJoinPool();
        return pool.submit(new WordCountTask(files)).get();
    }

    static class WordCountTask extends RecursiveTask<Map<String, Integer>> {
        // 2个文件时,不再分解任务
        private static final int THRESHOLD_SIZE = 2;
        private List<File> files;
        private int fileNumber;

        WordCountTask(List<File> files) {
            this.files = files;
            fileNumber = files.size();
        }

        @Override
        protected Map<String, Integer> compute() {
            if (files.size() <= THRESHOLD_SIZE) {
                return computeSequentially();
            }

            WordCountTask leftTask = new WordCountTask(files.subList(0, fileNumber / 2));
            // 在forkJoinPool中启用一个新线程异步执行
            leftTask.fork();
            WordCountTask rightTask = new WordCountTask(files.subList(fileNumber / 2, fileNumber));
            Map<String, Integer> rightResult = rightTask.compute();
            Map<String, Integer> lefrResult = leftTask.join();
            return Common.mergeMaps(lefrResult, rightResult);

        }

        private Map<String, Integer> computeSequentially() {
            if (files.isEmpty()) {
                return Collections.emptyMap();
            }

            return files.stream().map(file -> {
                try {
                    return Common.countOneFile(file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).reduce(new HashMap<>(), Common::mergeMaps);
        }

    }
}
