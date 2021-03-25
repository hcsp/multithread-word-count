package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

public class MultiThreadWordCount2 {

    private static Map<String, Integer> result = new ConcurrentHashMap<>();

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws Exception {
        ExecutorService threadPool = MultiThreadWordCount1.getThreadPool(threadNum);
        CountDownLatch latch = new CountDownLatch(files.size());
        for (File file : files) {
            threadPool.submit(() -> {
                try {
                    countOneFile(file, latch);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        latch.await();
        return result;
    }

    private static void countOneFile(File file, CountDownLatch latch) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String[] s = line.split(" ");
            Arrays.stream(s).forEach(MultiThreadWordCount2::add);
        }
        latch.countDown();
    }

    private static synchronized void add(String word) {
        Integer c = result.getOrDefault(word, 0);
        result.put(word, ++c);
    }
}
