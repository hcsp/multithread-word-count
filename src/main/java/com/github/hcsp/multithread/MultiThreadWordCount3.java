package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class MultiThreadWordCount3 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws Exception {
        TaskCenter taskCenter = new TaskCenter(threadNum, files);
        taskCenter.run();
        return taskCenter.getResult();
    }

    public static class TaskCenter {
        private final List<File> files;
        private final int threadNum;
        private int currentFileIndex = 0;
        private final CountDownLatch latch;
        private final List<Map<String, Integer>> workerResults = new ArrayList<>();
        private Exception e;

        private class Worker implements Runnable {
            @Override
            public void run() {
                File file;
                while ((file = getTask()) != null) {
                    Map<String, Integer> result = new HashMap<>();
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            String[] words = line.split(" ");
                            for (String word : words) {
                                result.put(word, result.getOrDefault(word, 0) + 1);
                            }
                        }
                        latch.countDown();
                        workerResults.add(result);
                    } catch (IOException e) {
                        if (TaskCenter.this.e != null) {
                            TaskCenter.this.e = new RuntimeException(TaskCenter.this.e);
                        } else {
                            TaskCenter.this.e = new RuntimeException(e);
                        }
                    }
                }
            }
        }

        public TaskCenter(int threadNum, List<File> files) {
            this.files = files;
            this.threadNum = threadNum;
            this.latch = new CountDownLatch(files.size());
        }

        public void run() {
            for (int i = 0; i < threadNum; i++) {
                new Thread(new Worker()).start();
            }
        }

        public Map<String, Integer> getResult() throws Exception {
            latch.await();
            if (this.e != null) {
                throw this.e;
            } else {
                return CommonUtils.mergeWorkerResults(workerResults);
            }
        }

        private synchronized File getTask() {
            if (currentFileIndex >= files.size()) {
                return null;
            } else {
                return files.get(currentFileIndex++);
            }
        }
    }
}
