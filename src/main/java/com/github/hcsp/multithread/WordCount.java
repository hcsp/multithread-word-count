package com.github.hcsp.multithread;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class WordCount {
    private final int threadNum;
    private ExecutorService threadPool;
    List<Future<Map<String, Integer>>> futures = new ArrayList<>();

    public WordCount(int threadNum) {
        threadPool = Executors.newFixedThreadPool(threadNum);
        this.threadNum = threadNum;
    }

    // 统计文件中各单词的数量
    public Map<String, Integer> count(List<File> file) throws ExecutionException, InterruptedException {
        //设定每个文件分配一个线程
        for (int i = 0; i < file.size(); i++) {
            futures.add(threadPool.submit(new WorkJob(file, i)));
        }
        Map<String, Integer> finalResult = new HashMap<>();
        for (Future<Map<String, Integer>> future : futures) {
            mergeReworkInFinalResult(future.get(), finalResult);
        }
        return finalResult;
    }

    //将Future的合并结果
    private void mergeReworkInFinalResult(Map<String, Integer> resultFromWorker, Map<String, Integer> finalResulte) {
        for (Map.Entry<String, Integer> entry : resultFromWorker.entrySet()) {
            int mergerResult = finalResulte.getOrDefault(entry.getKey(), 0) + entry.getValue();
            finalResulte.put(entry.getKey(), mergerResult);
        }

    }

    class WorkJob implements Callable<Map<String, Integer>> {
        private List<File> file;
        private int i;

        WorkJob(List<File> file, int i) {
            this.i = i;
            this.file = file;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            List<String> lines = new ArrayList<>();
            Map<String, Integer> map = new HashMap<>();
            countWord(readFiles(file.get(i), lines), map);
            return map;
        }
    }

    // 读取文件的String, 通过BufferReader防止内存溢出
    private  List<String> readFiles(File file, List<String> list) {
        String str = "";
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            while ((str = bufferedReader.readLine()) != null) {
                list.addAll(Arrays.asList(str.split(" ")));
            }
        } catch (IOException e) {
            System.out.println("出现异常，请解决" + e);
        }
        return list;
    }

    //统计不同出现的单词出现的数量, 放入map表中
    public static Map<String, Integer> countWord(List<String> strings, Map<String, Integer> map) {
        for (String element : strings) {
            map.put(element, map.getOrDefault(element, 0) + 1);
        }
        return map;
    }
}
