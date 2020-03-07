package com.github.hcsp.multithread;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MultiThreadWordCount4 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    // 使用线程池处理每一个文件
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        ExecutorService es = Executors.newFixedThreadPool(threadNum);

        List<Future<HashMap<String, Integer>>> futures = new ArrayList<>();
        files.forEach(file -> {
            futures.add(es.submit(new Callable<HashMap<String, Integer>>() {
                @Override
                public HashMap<String, Integer> call() throws Exception {
                    return ReaderUtils.readFileAsMap(file);
                }
            }));
        });
        HashMap<String, Integer> finalMap = new HashMap<>();
        futures.forEach(future -> {
            try {
                ReaderUtils.mergeMapToMap(future.get(), finalMap);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        return finalMap;
    }
}
