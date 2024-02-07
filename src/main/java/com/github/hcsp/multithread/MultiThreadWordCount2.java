package com.github.hcsp.multithread;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MultiThreadWordCount2 {
    //Lock/Condition, 每个线程并发执行被分配的任务块, 由最后一个线程通知完成
    private static final Map<String, Integer> result = new HashMap<>(); //输出最后的结果,互斥访问
    private static final ReentrantLock lock = new ReentrantLock();
    private static final Condition done = lock.newCondition();

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        int chunkNum = files.size() / threadNum;
        lock.lock();
        try {
            for (int i = 0; i < threadNum; i++) {
                int begin = i * chunkNum;
                int end = i != threadNum - 1 ? (i + 1) * chunkNum : files.size();
                new Thread(new ReadAndCount(files.subList(begin, end), i == threadNum - 1)).start();
            }
            done.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private static class ReadAndCount implements Runnable {
        private final List<File> inputFiles;
        private final boolean last;

        ReadAndCount(List<File> inputFiles, boolean last) {
            this.inputFiles = inputFiles;
            this.last = last;
        }

        @Override
        public void run() {
            lock.lock();
            try {//确保每个线程都能按顺序执行完
                for (File file :
                        inputFiles) {
                    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            mergeLineIntoResult(line);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } finally {
                if (last) {
                    done.signal();
                }
                lock.unlock();
            }
        }

        private void mergeLineIntoResult(String line) {
            String[] words = line.split(" ");
            for (String word :
                    words) {
                result.put(word, result.getOrDefault(word, 0) + 1);
            }
        }
    }
}
