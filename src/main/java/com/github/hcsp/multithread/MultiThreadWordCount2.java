package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class MultiThreadWordCount2 {

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        final Object lock = new Object();
        List<Map<String, Integer>> resultList = new ArrayList<>();
        BlockingQueue<File> todoFilesQueue = getFilesQueue(files);
        for (int i = 0; i < threadNum; i++) {
            new ReadFileTask(resultList, todoFilesQueue, lock).start();
        }
        synchronized (lock) {
            while (!isTasksComplete(resultList, files)) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return WordCountUtils.mergeMap(resultList);
        }
    }

    public static boolean isTasksComplete(List<Map<String, Integer>> results, List<File> files) {
        return results.size() == files.size();
    }

    public static BlockingQueue<File> getFilesQueue(List<File> files) {
        return new ArrayBlockingQueue<>(files.size(), false, files);
    }

    static class ReadFileTask extends Thread {
        private final List<Map<String, Integer>> resultList;
        private final BlockingQueue<File> todoFilesQueue;
        private final Object lock;

        ReadFileTask(List<Map<String, Integer>> resultList, BlockingQueue<File> todoFilesQueue, Object lock) {
            this.resultList = resultList;
            this.todoFilesQueue = todoFilesQueue;
            this.lock = lock;
        }

        @Override
        public void run() {
            File file;
            while ((file = todoFilesQueue.poll()) != null) {
                try {
                    Map<String, Integer> oneFileResult = WordCountUtils.statisticsFileWordCount(file);
                    synchronized (lock) {
                        resultList.add(oneFileResult);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            synchronized (lock) {
                lock.notify();
            }
        }
    }
}
