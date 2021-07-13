package com.github.hcsp.multithread;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class MultiThreadWordCount3 {
    // TODO: use ConcurrentHashMap
    private static final Object lock = new Object();
    private static final Map<String, Integer> resultMap = new HashMap<>();

    public static Map<String, Integer> count(int threadNum, List<File> files) {
        CountDownLatch doneSignal = new CountDownLatch(threadNum);

        for (int i = 0; i < threadNum; ++i) {
            File file = files.get(i);
            new Thread(new Worker(file, doneSignal)).start();
        }

        try {
            doneSignal.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return resultMap;
    }

    static class Worker implements Runnable {
        private final File file;
        private final CountDownLatch doneSignal;

        Worker(File file, CountDownLatch doneSignal) {
            this.file = file;
            this.doneSignal = doneSignal;
        }

        public void run() {
            synchronized (lock) {
                ConcurrentHashMap<String, Integer> result = new ProcessFile(file).processFile();
                result.keySet().forEach(key -> {
                    if (resultMap.get(key) != null) {
                        resultMap.put(key, resultMap.get(key) + result.get(key));
                    } else {
                        resultMap.put(key, result.get(key));
                    }
                });
                doneSignal.countDown();
            }
        }
    }
}
