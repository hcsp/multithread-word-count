package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WordCount {

    private final int nThread;

    private final ExecutorService executor;

    private CountDownLatch countDownLatch;

    private final Object lock = new Object();

    private final Map<String, Integer> result = new HashMap<>();

    public WordCount(int threadNum) {
        nThread = threadNum;
        executor = Executors.newFixedThreadPool(nThread);
    }

    // 统计文件中各单词的数量
    public Map<String, Integer> count(List<File> files) throws InterruptedException {
        // validate
        if (null == files || 0 == files.size()) {
            return new HashMap<>();
        }
        countDownLatch = new CountDownLatch(files.size());
        for (File file : files) {
            executor.submit(new CountTask(file));
        }
        countDownLatch.await();
        return result;
    }

    class CountTask implements Runnable {

        private final File file;

        CountTask(File file) {
            this.file = file;
        }

        @Override
        public void run() {
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(new FileReader(file));
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    String[] words = line.split(" ");
                    for (String word : words) {
                        synchronized (lock) {
                            // 组合操作并不是线程安全的
                            result.put(word, result.getOrDefault(word, 0) + 1);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != bufferedReader) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                countDownLatch.countDown();
            }
        }
    }

}
