package com.github.hcsp.multithread;

import com.google.common.collect.Lists;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Future与线程池 实现
 * @author kwer
 * @date 2020/5/5 22:23
 */
public class MultiThreadWordCount4 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {
        Map<String, Integer> finalResult = new HashMap<>();
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        List<Future<List<Map<String, Integer>>>> resultList = new ArrayList<>(files.size());
        List<List<File>> threadFiles = Lists.partition(files, files.size() / threadNum);
        for (List<File> fileList : threadFiles) {
            Future<List<Map<String, Integer>>> future = threadPool.submit(() -> {
                List<Map<String, Integer>> results = new ArrayList<>(fileList.size());
                for (File file : fileList) {
                    System.out.println(Thread.currentThread().getName() + " 执行文件解析……");
                    results.add(WordCountUtil.count(file));
                }
                return results;
            });
            resultList.add(future);
        }

        for (Future<List<Map<String, Integer>>>  future : resultList) {
            List<Map<String, Integer>> list = future.get();
            for (Map<String, Integer> map : list) {
                finalResult = WordCountUtil.merge(finalResult, map);
            }

        }
        return finalResult;
    }
}
