package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        Map<String, Integer> resultMap = new HashMap<>();
        List<BufferedReader> bufferedReaderList = getBufferedReaderList(files);
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        for (BufferedReader bufferedReader : bufferedReaderList) {
            futures.add(executorService.submit(new ComputeFileWord(bufferedReader)));
        }
        for (Future<Map<String, Integer>> future : futures) {
            try {
                Map<String, Integer> stringIntegerMap = future.get();
                for (Map.Entry<String, Integer> stringIntegerEntry : stringIntegerMap.entrySet()) {
                    resultMap.put(stringIntegerEntry.getKey(), resultMap.getOrDefault(stringIntegerEntry.getKey(), 0) + stringIntegerEntry.getValue());
                }

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return resultMap;
    }

    public static List<BufferedReader> getBufferedReaderList(List<File> files) {
        List<BufferedReader> bufferedReaderList = new ArrayList<>();
        for (File file : files) {
            try {
                bufferedReaderList.add(new BufferedReader(new FileReader(file)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return bufferedReaderList;
    }

    static class ComputeFileWord implements Callable<Map<String, Integer>> {
        private final BufferedReader bufferedReader;

        ComputeFileWord(BufferedReader bufferedReader) {
            this.bufferedReader = bufferedReader;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            Map<String, Integer> mapList = new HashMap<>();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                String[] strings = line.split(" ");
                for (String string : strings) {
                    mapList.put(string, mapList.getOrDefault(string, 0) + 1);
                }
            }
            return mapList;
        }
    }
}
