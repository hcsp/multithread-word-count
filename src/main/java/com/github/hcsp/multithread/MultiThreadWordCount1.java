package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * {@link java.util.concurrent.CountDownLatch} implements
 */
public class MultiThreadWordCount1 {
    static BlockingQueue<Map<String, Integer>> resultList = new LinkedBlockingQueue<>();
    static CountDownLatch countDownLatch = null;

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        BlockingQueue<File> fileBlockingQueue = new LinkedBlockingQueue<>(files);
        countDownLatch = new CountDownLatch(threadNum);

        for (int i = 0; i < threadNum; ++i) {
            new Thread(() -> {
                while (!fileBlockingQueue.isEmpty()) {
                    File file = fileBlockingQueue.poll();
                    if (file == null) {
                        break;
                    }
                    resultList.add(countOneFile(file));
                }
                countDownLatch.countDown();
            }).start();
        }

        countDownLatch.await();

        return flat(resultList);
    }

    public static Map<String, Integer> countOneFile(File file) {
        Map<String, Integer> map = new HashMap<>();
        List<String> lines;
        try {
            lines = Files.readAllLines(file.toPath());
            for (String line : lines) {
                String[] words = line.split("\\s+");

                for (String word : words) {
                    int count = map.getOrDefault(word, 0);
                    map.put(word, count + 1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return map;
    }

    public static Map<String, Integer> flat(Collection<Map<String, Integer>> mapList) {
        return mapList.stream()
                .reduce(new HashMap<>(), (m1, m2) -> {
                    m1.forEach((k, v) -> m2.merge(k, v, Integer::sum));
                    return m2;
                });
    }
}
