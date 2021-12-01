package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class MultiThreadWordCount4 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        ArrayDeque<File> fileDeque = new ArrayDeque<>(files);

        Map<String, Integer> result = new HashMap<>();
        List<Thread> workers = new ArrayList<>();
        for (int i = 0; i < threadNum; i++) {
            CountWorker countWorker = new CountWorker(fileDeque, result);
            countWorker.start();
            workers.add(countWorker);
        }
        for (Thread worker : workers) {
            worker.join();
        }

        return result;
    }

    public static class CountWorker extends Thread {
        final ArrayDeque<File> fileArrayDeque;
        final Map<String, Integer> total;

        public CountWorker(ArrayDeque<File> fileArrayDeque, Map<String, Integer> total) {
            this.fileArrayDeque = fileArrayDeque;
            this.total = total;
        }

        @Override
        public void run() {
            try {
                File file;
                while ((file = pollFile()) != null) {
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

        File pollFile() {
            File file;
            synchronized (fileArrayDeque) {
                file = fileArrayDeque.poll();
            }
            return file;
        }
    }

}
