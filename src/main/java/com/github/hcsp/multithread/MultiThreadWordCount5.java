package com.github.hcsp.multithread;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

public class MultiThreadWordCount5 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    private static final Map<String, Integer> resultMap = new ConcurrentHashMap<>();

    public static Map<String, Integer> count(int threadNum, List<File> files) {
        ForkJoinPool forkJoinPool = new ForkJoinPool(threadNum);

        for (File file : files) {
            Future submit = forkJoinPool.submit(new Temp(file));
            try {
                submit.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        return resultMap;
    }

    private static class Temp implements Callable {
        private final File FILE;

        Temp(File file) {
            this.FILE = file;
        }

        @Override
        public Map<String, Integer> call() {
            return ProcessFile.convertWordsInFileToMap(FILE, resultMap);
        }
    }
}
