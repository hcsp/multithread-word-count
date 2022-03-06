package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        Object lock = new Object();
        ConcurrentHashMap<String, Integer> result = new ConcurrentHashMap<>();
        ArrayList<File> handlingFiles = new ArrayList<>();
        ArrayList<Thread> threadQueue = new ArrayList<>();
        for (File file : files) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized (lock) {
                        try {
                            while (handlingFiles.size() >= threadNum) {
                                lock.wait();
                            }
                            handlingFiles.add(file);
                            HashMap<String, Integer> singleFileWordCount = countSingleFileWord(file);
                            for (String word :
                                    singleFileWordCount.keySet()) {
                                result.put(word, result.getOrDefault(word, 0) + singleFileWordCount.get(word));
                            }
                            handlingFiles.remove(file);
                        } catch (InterruptedException | IOException e) {
                            e.printStackTrace();
                        } finally {
                            lock.notify();
                        }
                    }
                }
            });
            thread.start();
            threadQueue.add(thread);
        }
        for (Thread thread :
                threadQueue) {
            thread.join();
        }
        return result;
    }

    public static HashMap<String, Integer> countSingleFileWord(File file) throws IOException {
        HashMap<String, Integer> singleFileWordCount = new HashMap<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            for (String word :
                    line.split(" ")) {
                singleFileWordCount.put(word, singleFileWordCount.getOrDefault(word, 0) + 1);
            }
        }
        return singleFileWordCount;
    }
}
