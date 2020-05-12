package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MultiThreadWordCount4 {

    // 使用threadNum个线程，并发统计文件中各单词的数量 feature and thread pool

    public static void main(String[] args) throws Exception {
        List<File> files = Arrays.asList(new File("C:/Users/sunp/git/test-files/test1.txt"),
                new File("C:/Users/sunp/git/test-files/test2.txt"), new File("C:/Users/sunp/git/test-files/test3.txt"),
                new File("C:/Users/sunp/git/test-files/test4.txt"));
        System.out.println(count(4, files));
    }

    public static Map<String, Integer> count(int threadNum, List<File> files)
            throws IOException, InterruptedException, ExecutionException {
        Map<String, Integer> result = new HashMap<>();
        List<Future<Map<String, Integer>>> middleList = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        List<List<File>> fileChucks = MultiThreadWordUtility.fileSplit(threadNum, files);
        for (List<File> fileChuck : fileChucks) {
            Future<Map<String, Integer>> future = executorService.submit(new Callable<Map<String, Integer>>() {
                @Override
                public Map<String, Integer> call() throws Exception {
                    try {
                        return MultiThreadWordUtility.fileChuckCount(fileChuck);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            middleList.add(future);
        }

        for (Future<Map<String, Integer>> stringIntegerMap : middleList) {
            stringIntegerMap.get().forEach((key, value) -> {
                result.put(key, result.getOrDefault(key, 0) + value);
            });
        }

        return result;
    }
}
