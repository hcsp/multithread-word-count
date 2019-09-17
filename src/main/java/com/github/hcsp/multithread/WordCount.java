package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class WordCount {
    ScheduledThreadPoolExecutor executor;
    public WordCount(int threadNum) {
        this.executor = new ScheduledThreadPoolExecutor(threadNum);
    }

    // 统计文件中各单词的数量
    Map<String, Integer> count(List<File> files) {
        Map<String, Integer> map = new HashMap<>();
        for (File file: files) {
            Future<Map<String, Integer>> future = executor.submit(new SingleTask(file));
            try {
                for (Map.Entry<String, Integer> entry: future.get().entrySet()) {
                    String key = entry.getKey();
                    Integer val = entry.getValue();

                    if (map.containsKey(key)) {
                        map.put(key, map.get(key) + val);
                    }else {
                        map.put(key, val);
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    // 单个线程的任务
    static class SingleTask implements Callable<Map<String, Integer>> {
        File file;
        SingleTask(File file) {
            this.file = file;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            Map<String, Integer> map = new HashMap<>();
            BufferedReader br = new BufferedReader(new FileReader(file));
            String str;
            while ((str = br.readLine()) != null) {
                String[] words = str.split(" ");
                for (String word : words) {
                    if (map.containsKey(word)) {
                        map.put(word, map.get(word) + 1);
                    } else {
                        map.put(word, 1);
                    }
                }
            }
            return map;
        }

    }



}
