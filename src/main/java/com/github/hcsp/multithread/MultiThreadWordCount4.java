package com.github.hcsp.multithread;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class MultiThreadWordCount4 {

    public static ReentrantLock lock = new ReentrantLock();

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {

        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        ArrayList<Thread> threads = new ArrayList<>();

        for (int i = 0; i < threadNum; i++) {
            WordCountTask2 task = new WordCountTask2(files.get(i), map);

            Thread thread = new Thread(task);

            threads.add(thread);

            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return map;
    }

    static class WordCountTask2 implements Runnable {

        private final File file;
        private final ConcurrentHashMap<String, Integer> map;

        WordCountTask2(File file, ConcurrentHashMap<String, Integer> map) {
            this.file = file;
            this.map = map;
        }

        @Override
        public void run() {
            try (
                    InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file));
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            ) {

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    line = line.trim();

                    String[] words = line.split("\\s+");

                    for (String word : words) {

                        lock.lock();

                        Integer cnt = map.getOrDefault(word, 0);

                        map.put(word, cnt + 1);

                        lock.unlock();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
