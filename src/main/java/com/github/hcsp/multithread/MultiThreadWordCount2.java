package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class MultiThreadWordCount2 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws IOException, ExecutionException, InterruptedException {
        MultiThreadWordCount2 count2 = new MultiThreadWordCount2();
        ArrayList<Map<String, Integer>> maps = new ArrayList<>();
        for (File file : files) {
            maps.add(count2.fileCount(threadNum, file));
        }
        return CountTools.MapListReduce(maps);
    }

    private Map<String, Integer> fileCount(int threadNum, File file) throws IOException, ExecutionException, InterruptedException {
        ArrayList<Map<String, Integer>> maps = new ArrayList<>();
        BufferedReader reader = null;
        reader = new BufferedReader(new FileReader(file));
        ArrayList<Future<Map<String, Integer>>> futures = new ArrayList<>();
        ExecutorService threadPool = newFixedThreadPool(threadNum);

        String line;
        while ((line = reader.readLine()) != null) {
            String finalLine = line;
            Callable<Map<String, Integer>> callable = () -> CountTools.lineToMap(finalLine);
            futures.add(threadPool.submit(callable));
        }

        for (Future<Map<String, Integer>> future : futures) {
            maps.add(future.get());
        }
        System.out.println(file.getName());
        return CountTools.MapListReduce(maps);
    }

//    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
//        File directory = new File("D:\\temp");
//        List<File> files = Arrays.asList(directory.listFiles());
//        System.out.println(count(10, files));
//    }
}
