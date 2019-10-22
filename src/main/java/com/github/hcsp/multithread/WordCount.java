package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WordCount {
    ExecutorService pool;
    int threadNum;

    public WordCount(int threadNum) {
        this.threadNum = threadNum;
        pool = Executors.newFixedThreadPool(threadNum);
    }


    // 统计文件中各单词的数量
    public Map<String, Integer> count(List<File> files) throws ExecutionException, InterruptedException, FileNotFoundException {
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        for (File file : files) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            futures.add(pool.submit(new Callable<Map<String, Integer>>() {
                @Override
                public Map<String, Integer> call() throws Exception {
                    Map<String, Integer> singleRes = new HashMap<>();
                    String lines;
                    while ((lines = reader.readLine()) != null) {
                        StringTokenizer st = new StringTokenizer(lines, " ");
                        while (st.hasMoreTokens()) {
                            String word = st.nextToken();
                            singleRes.put(word, singleRes.getOrDefault(word, 0) + 1);
                        }
                    }
                    return singleRes;
                }
            }));
        }
        return mergeToMap(futures);
    }

    private Map<String, Integer> mergeToMap(List<Future<Map<String, Integer>>> futures)
            throws ExecutionException, InterruptedException {
        Map<String, Integer> res = new HashMap<>();
        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> map = future.get();
            map.forEach((s, integer) -> res.put(s, res.getOrDefault(s, 0) + integer));
        }
        return res;
    }

}
