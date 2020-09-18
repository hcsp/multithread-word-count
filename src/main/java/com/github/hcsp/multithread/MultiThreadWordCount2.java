package com.github.hcsp.multithread;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Lock + CountDownLatch
 */
public class MultiThreadWordCount2 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
        public static Map<String, Integer> count(int threadNum, List<File> files) {
            HashMap<String, Integer> map = new HashMap<>();
            ReentrantLock lock = new ReentrantLock();
            final CountDownLatch latch;
            // 判断是文件数多还是线程多，哪个少就以哪个为基准for循环
            int min = Math.min(threadNum, files.size());
            latch = new CountDownLatch(min);
            for (int i = 0; i < min; i++) {
                new Thread(doCount(files.get(i), map, lock, latch)).start();
            }
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return map;
        }

    public static Runnable doCount(File file, Map<String, Integer> map, ReentrantLock lock, CountDownLatch latch) {
        return () -> {
            List<String> list = FileUtils.readFileContent(file.getAbsolutePath());
            for (String line : list) {
                String[] words = line.split(" ");
                for (String word : words) {
                    lock.lock();
                    map.put(word, map.getOrDefault(word, 0) + 1);
                    lock.unlock();
                }
            }
            latch.countDown();
        };
    }
}
