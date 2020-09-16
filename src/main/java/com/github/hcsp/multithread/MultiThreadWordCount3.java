package com.github.hcsp.multithread;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MultiThreadWordCount3 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        final CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        ExecutorService executor = Executors.newFixedThreadPool(5);
        List<Future<Map<String, Integer>>> futureList = new ArrayList<>(threadNum);
        for (File file : files) {
            futureList.add(executor.submit(new WorkCountTask3(file, countDownLatch)));
        }


        try {
            countDownLatch.await();

            System.out.println("main thread running...");

            Map<String, Integer> result = new HashMap<>();
            for (Future<Map<String, Integer>> future : futureList) {
                MultiThreadWordCount1.mergeMap(result, future.get());
            }
            return result;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    static class WorkCountTask3 extends WorkCountTask {
        private CountDownLatch countDownLatch;

        public WorkCountTask3(File file) {
            super(file);
        }

        public WorkCountTask3(File file, CountDownLatch countDownLatch) {
            super(file);
            this.countDownLatch = countDownLatch;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            final Map<String, Integer> map = super.call();
            countDownLatch.countDown();
            return map;
        }
    }
}


