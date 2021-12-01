package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        Map<String, Integer> total = new HashMap<>();

        int num = files.size() % threadNum == 0 ?
                files.size() / threadNum : files.size() / threadNum + 1;
        List<Thread> workers = new ArrayList<>(threadNum);
        for (int i = 0; i < files.size(); i+= num) {
            CountWorker countWorker = new CountWorker(files.subList(i, i + num), total);
            workers.add(countWorker);
            countWorker.start();
        }
        for (Thread worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return total;
    }

    public static class CountWorker extends Thread {
        List<File> files;
        final Map<String, Integer> total;

        public CountWorker(List<File> files, Map<String, Integer> total) {
            this.files = files;
            this.total = total;
        }

        @Override
        public void run() {
            try {
                for (File file : files) {
                    List<String> strings = Files.readAllLines(file.toPath());
                    strings.stream()
                                    .flatMap(v -> Arrays.stream(v.split(" ")))
                                    .forEach(word -> {
                                synchronized (total) {
                                    Integer integer = total.get(word);
                                    if (integer == null) {
                                        total.put(word, 1);
                                    } else {
                                        int newValue = integer + 1;
                                        total.put(word, newValue);
                                    }
                                }
                            });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
