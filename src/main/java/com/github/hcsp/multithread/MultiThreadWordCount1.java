package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MultiThreadWordCount1 {

    public static void main(String[] args) throws FileNotFoundException, ExecutionException, InterruptedException {
        List<File> fileList = new ArrayList<>();
        File file1 = new File("/Users/chenxiaole/learnTrain/temp/testJava.txt");
        File file2 = new File("/Users/chenxiaole/learnTrain/temp/testJava1.txt");
        if (!file1.exists() || !file2.exists()) {
            System.out.println("file is not exit");
        }
        fileList.add(file1);
        fileList.add(file2);
        System.out.println(count(111, fileList));
    }


    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws FileNotFoundException, ExecutionException, InterruptedException {

        List<Future<Map<String, Integer>>> totalFutures = new ArrayList<>();
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);

        for (File file : files) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            totalFutures.add(threadPool.submit(() -> Utils.countFile(file)));
        }

        // 需要合并
        Map<String, Integer> finalResult = new HashMap<>();
        for (Future<Map<String, Integer>> future : totalFutures) {
            System.out.println(future.get());
            Utils.mergeMap(future.get(), finalResult);
        }

        return finalResult;
    }
}
