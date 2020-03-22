package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * 使用ForkJoinPool进行统计
 * 算法基于分治的思想，将大任务不断切分，最终归并得到答案
 */
public class MultiThreadWordCount2 {

    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();
        ForkJoinPool fjp = new ForkJoinPool();
        // 提交可分解的CountTask任务
        Map<String, Integer> r = fjp.submit(new CountTask(files)).get();
        long end = System.currentTimeMillis();
        System.out.println(end - start);
        return r;

    }

    //这里切分的最小单位是一个文件，统计单个文件的情况，结果以列表类型返回
    public static Map<String, Integer> count2(File file) {
        Map<String, Integer> wordCountResult = new HashMap<>();
        List<String> lines = null;
        try {
            lines = Files.readAllLines(file.toPath());
            for (String line : lines) {
                String[] words = line.split(" ");
                for (String word : words) {
                    int count = wordCountResult.getOrDefault(word, 0);
                    wordCountResult.put(word, count + 1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wordCountResult;
    }


    private static class CountTask extends RecursiveTask<Map<String, Integer>> {
        List<File> filesList;

         CountTask(List<File> files) {
            this.filesList = files;
        }

        @Override
        protected Map<String, Integer> compute() {

            //截至条件
            if (filesList.isEmpty()) {
                return Collections.emptyMap();
            }
            Map<String, Integer> wordCountMap = count2(filesList.get(0));
            Map<String, Integer> CountOfRestFileMap = new CountTask(filesList.subList(1, filesList.size())).compute();

            return merge2(wordCountMap, CountOfRestFileMap);
        }
    }

        public static Map<String, Integer> merge2(Map<String, Integer> map1, Map<String, Integer> map2) {
            for (String word : map2.keySet()) {
                map1.put(word, map1.getOrDefault(word, 0) + map2.getOrDefault(word, 0));

            }
            return map1;
        }
    }

