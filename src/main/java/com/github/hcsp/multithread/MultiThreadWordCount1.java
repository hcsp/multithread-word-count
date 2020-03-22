package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MultiThreadWordCount1 {

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        // 使用Synchronized来实现
        ConcurrentHashMap<String, Integer> result = new ConcurrentHashMap<>();
        List<List<File>> taskGroup = new ArrayList<>();

        // 分配任务
        if (files.size() < threadNum) {
            threadNum = files.size();
        } else if (files.size() > threadNum) {
            int averageNumber = files.size() / threadNum;
            int index = files.size() % threadNum;
            for (int i = 0; i < threadNum; i++) {
                if (i >= index - 1) {
                    taskGroup.add(files.subList(i, i + averageNumber + 1));
                } else {
                    taskGroup.add(files.subList(i, i + averageNumber));
                }
            }
        }

        System.out.println(taskGroup.toString());
        for (int i = 0; i < threadNum; i++) {
            Counter counter = new Counter(result, taskGroup.get(i));
            counter.start();
            counter.join();
        }
        return result;
    }

    static class Counter extends Thread {

        private ConcurrentHashMap<String, Integer> result;
        private List<File> targetFiles;

        public Counter(ConcurrentHashMap<String, Integer> result, List<File> targetFiles) {
            this.result = result;
            this.targetFiles = targetFiles;
        }

        @Override
        public void run() {
            try {
                for (File targetFile : targetFiles) {
                    List<String> lines = Files.readAllLines(targetFile.toPath());
                    for (String line : lines) {
                        String[] split = line.split("\\s");
                        for (String word : split) {
                            Integer wordNumber = result.getOrDefault(word, 0);
                            result.put(word, ++wordNumber);
                        }
                    }
                }
                System.out.println(Thread.currentThread().getName() + ":result" + result.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private static void mergeOneLineCountToOneFileCountByMap(Map<String, Integer> resultMap,
        Map<String, Integer> oneFileResultMap) {
        Set<String> keys = oneFileResultMap.keySet();
        for (String key : keys) {
            resultMap.put(key, oneFileResultMap.getOrDefault(key, oneFileResultMap.get(key)));
        }
    }
//
//    public static void main(String[] args) throws InterruptedException {
//        List<File> files = Arrays.asList(
//            new File("1.txt"),
//            new File("1.txt"),
//            new File("1.txt"),
//            new File("1.txt"),
//            new File("1.txt"),
//            new File("1.txt"),
//            new File("1.txt"),
//            new File("1.txt")
//        );
//        Map<String, Integer> count = count(3, files);
//        System.out.println(count);
//
//
//    }

}
