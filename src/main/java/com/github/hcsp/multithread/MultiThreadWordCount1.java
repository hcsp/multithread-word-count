package com.github.hcsp.multithread;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MultiThreadWordCount1 {
    private static ConcurrentHashMap<String, Integer> concurrentHashMap = new ConcurrentHashMap<>();

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws FileNotFoundException {
        for (int i = 0; i < threadNum; i++) {
            new Worker(files).start();
        }
        return concurrentHashMap;
    }

    static class Worker extends Thread {
        private List<File> files;

        Worker(List<File> files) {
            this.files = files;
        }

        @Override
        public void run() {
            try {
                readFiles(files);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private void readFiles(List<File> files) throws IOException {
            for (File file : files) {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
                    String[] words = line.split(" ");
                    for (String word : words) {
                        concurrentHashMap.put(word, concurrentHashMap.getOrDefault(word, 0) + 1);
                    }
                }
            }
        }
    }
}
