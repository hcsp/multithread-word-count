package com.github.hcsp.multithread;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static com.github.hcsp.multithread.MultiThreadWordCount2.CountTask.geWordCountMap;

public class MultiThreadWordCount1 {

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        Object lock = new Object();
        ConcurrentHashMap<String, Integer> result = new ConcurrentHashMap<>();
        ArrayList<File> handingFiles = new ArrayList<>();
        ArrayList<Thread> threads = new ArrayList<>();
        for (File file : files) {
            Thread thread = new Thread() {
                public void run() {
                    synchronized (lock) {
                        try {
                            while (handingFiles.size() >= threadNum) {
                                lock.wait();
                            }
                            handingFiles.add(file);
                            Map<String, Integer> singleFileResult = countSingleFileWord(file);
                            mergeSubmitResultIntoFinalResult(singleFileResult, result);
                            handingFiles.remove(file);
                        } catch (InterruptedException | IOException e) {
                            e.printStackTrace();
                        } finally {
                            lock.notify();
                        }
                    }
                }
            };
            thread.start();
            threads.add(thread);
        }
        for (Thread thead : threads) {
            thead.join();
        }
        return result;
    }

    public static void mergeSubmitResultIntoFinalResult(Map<String, Integer> resultFromSubmit, Map<String, Integer> finalResult) {
        for (Map.Entry<String, Integer> entry : resultFromSubmit.entrySet()) {
            String word = entry.getKey();
            finalResult.put(word, finalResult.getOrDefault(word, 0) + entry.getValue());
        }
    }

    public static Map<String, Integer> countSingleFileWord(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        return geWordCountMap(reader);
    }
}
