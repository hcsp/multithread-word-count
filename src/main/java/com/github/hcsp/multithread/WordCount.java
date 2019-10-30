package com.github.hcsp.multithread;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class WordCount {
    private final ExecutorService threadPool;

    public WordCount(int threadNum) {
        threadPool = Executors.newFixedThreadPool(threadNum);
    }


    /**
     * 每个线程计算一个文件
     *
     * @param fileList 文件列表
     * @return wordCounts
     * @throws ExecutionException   执行异常
     * @throws InterruptedException 中断异常
     */
    public Map<String, Integer> count(List<File> fileList) throws ExecutionException, InterruptedException {
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        for (File file : fileList) {
            futures.add(threadPool.submit(
                    () -> countOneFile(file)
            ));
        }

        //merge results
        Map<String, Integer> wordCounts = new HashMap<>();
        Map<String, Integer> temp;
        for (Future<Map<String, Integer>> f : futures) {
            temp = f.get();
            for (Map.Entry<String, Integer> entry : temp.entrySet()) {
                wordCounts.put(entry.getKey(),
                        wordCounts.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
        }
        return wordCounts;
    }

    /**
     * 针对单个文件的单线程操作
     *
     * @param file 操作文件
     * @return wordsCount 计算单词出现频率
     * @throws IOException I/O异常
     */
    public Map<String, Integer> countOneFile(File file) throws IOException {
        Map<String, Integer> wordsCount = new HashMap<>();

        FileReader fileReader = new FileReader(file);
        BufferedReader br = new BufferedReader(fileReader);
        String line = null;
        while ((line = br.readLine()) != null) {
            String[] words = line.split(" ");
            for (String word : words) {
                wordsCount.put(word, wordsCount.getOrDefault(word, 0) + 1);
            }
        }

        return wordsCount;
    }

}
