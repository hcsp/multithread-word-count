package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        for (File file : files) {
            Future<Map<String, Integer>> futureFileContent = readFileWithThreadPool(file, executorService);
            futures.add(futureFileContent);
        }
        return parseFutureFileContents(futures);
    }

    private static Map<String, Integer> parseFutureFileContents(List<Future<Map<String, Integer>>> futures) {
        List<Map<String, Integer>> mapList = new ArrayList<>();
        for (Future<Map<String, Integer>> future : futures) {
            try {
                Map<String, Integer> singleFileResult = future.get();
                mapList.add(singleFileResult);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return WordCountUtils.mergeMap(mapList);
    }

    public static Future<Map<String, Integer>> readFileWithThreadPool(File file, ExecutorService executorService) {
        return executorService.submit(new ReadFileTask(file));
    }

    static class ReadFileTask implements Callable<Map<String, Integer>> {
        File file;

        ReadFileTask(File file) {
            this.file = file;
        }

        @Override
        public Map<String, Integer> call() throws IOException {
            return WordCountUtils.statisticsFileWordCount(file);
        }
    }
}
