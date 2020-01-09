package com.github.hcsp.multithread;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadWordCount3 {
    // 使用threadNum个线程，并发统计文件中各单词的数量

    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {
        CountDownLatch doneSignal = new CountDownLatch(files.size());
        ExecutorService e = Executors.newFixedThreadPool(threadNum);
        Map<String, Integer> result = new HashMap<>();

        for (File file : files) { // create and start threads
            e.submit(new Thread(() -> {
                Map<String, Integer> workerResult = MultiThreadWordCount4.getWorkerResult(file);
                synchronized (MultiThreadWordCount3.class) {
                    MultiThreadWordCount4.mergeWorkerResult(result, workerResult);
                }
                doneSignal.countDown();
            }));
        }

        doneSignal.await();      // wait for all threads to finish

        return result;
    }


}

