package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;

public class MultiThreadWordCount4 {
    // (Future/threadPool) 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        List<BufferedReader> readers = getReaders(files);
        for (int i = 0; i < threadNum; i++) {
            futures.add(executorService.submit(new CountWord(readers)));
        }

        Map<String, Integer> finalMap = collectFinalMap(futures);
        executorService.shutdown();
        closeReaders(readers);
        return finalMap;
    }

    private static class CountWord implements Callable<Map<String, Integer>> {
        private List<BufferedReader> readers;

        private CountWord(List<BufferedReader> readers) {
            this.readers = readers;
        }

        @Override
        public Map<String, Integer> call() {
            String s;
            Map<String, Integer> map = new LinkedHashMap<>();
            while ((s = getReadLine(readers)) != null) {
                for (String word : s.split(" ")) {
                    if (!"".equals(word)) {
                        map.put(word, map.getOrDefault(word, 0) + 1);
                    }
                }
            }
            return map;
        }
    }

    private static Map<String, Integer> collectFinalMap(List<Future<Map<String, Integer>>> futures) {
        Map<String, Integer> finalMap = new LinkedHashMap<>();
        try {
            for (Future<Map<String, Integer>> future : futures) {
                Map<String, Integer> partMap = future.get();
                for (Map.Entry<String, Integer> entry : partMap.entrySet()) {
                    String word = entry.getKey();
                    finalMap.put(word, finalMap.getOrDefault(word, 0) + entry.getValue());
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return finalMap;
    }

    private static List<BufferedReader> getReaders(List<File> files) throws IOException {
        List<BufferedReader> readers = new ArrayList<>(files.size());
        for (File file : files) {
            readers.add(new BufferedReader(new FileReader(file)));
        }
        return readers;
    }

    private static String getReadLine(List<BufferedReader> readers) {
        try {
            for (BufferedReader bReader : readers) {
                String line;
                if ((line = bReader.readLine()) != null && !"".equals(line)) {
                    return line;
                }
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IOException when BufferedReader readLine!");
        }
    }

    private static void closeReaders(List<BufferedReader> readers) throws IOException {
        for (BufferedReader bReader : readers) {
            bReader.close();
        }
    }
}
