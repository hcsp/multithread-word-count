package com.github.hcsp.multithread;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WordCount {

    private final int threadNum;
    private ExecutorService threadPool;

    public WordCount(int threadNum) {
        threadPool = Executors.newFixedThreadPool(threadNum);
        this.threadNum = threadNum;
    }

    /**
     * 统计文件中各单词的数量
     *
     * @param files 文件列表
     * @return 单词统计结果
     * @throws IOException          文件读写出错时抛出
     * @throws ExecutionException   Future 取回结果出错时抛出
     * @throws InterruptedException 线程被中断时抛出
     */
    public Map<String, Integer> count(List<File> files) throws IOException, ExecutionException, InterruptedException {
        BufferedReader reader = new BufferedReader(new FileReader(mergeFilesIntoSingleFile(files)));
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        // 开辟若干个线程，每个线程读取文件的一行内容，并将单词统计结果返回
        // 最后主线程将工作线程返回的结果汇总在一起
        for (int i = 0; i < threadNum; i++) {
            futures.add(threadPool.submit(new WorkerJob(reader)));
        }
        // 最终结果集
        Map<String, Integer> finalResult = new HashMap<>();
        // 将futures中的每个子结果集合并到终集中
        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> resultFromWorker = future.get();
            mergeWorkerResultIntoFinalResult(resultFromWorker, finalResult);
        }
        threadPool.shutdown();
        return finalResult;
    }

    /**
     * 将文件列表中的文件合并为一个文件
     *
     * @param files 文件列表
     * @return 结果文件
     * @throws IOException 文件读写出错时抛出
     */
    private File mergeFilesIntoSingleFile(List<File> files) throws IOException {
        File result = File.createTempFile("tmp", "");
        for (File file : files) {
            String encoding = "UTF-8";
            FileUtils.write(result, FileUtils.readFileToString(file, encoding), encoding, true);
        }
        return result;
    }

    /**
     * 将子集合并到终集中
     *
     * @param resultFromWorker 终集
     * @param finalResult      子集
     */
    private void mergeWorkerResultIntoFinalResult(Map<String, Integer> resultFromWorker,
                                                  Map<String, Integer> finalResult) {
        for (Map.Entry<String, Integer> entry : resultFromWorker.entrySet()) {
            String word = entry.getKey();
            int mergedResult = finalResult.getOrDefault(word, 0) + entry.getValue();
            finalResult.put(word, mergedResult);
        }
    }

    static class WorkerJob implements Callable<Map<String, Integer>> {
        private BufferedReader reader;

        private WorkerJob(BufferedReader reader) {
            this.reader = reader;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            String line;
            Map<String, Integer> result = new HashMap<>();
            while ((line = reader.readLine()) != null) {
                String[] words = line.split(" ");
//                System.out.println(Thread.currentThread().getName());
//                System.out.println(line);
//                System.out.println();
                for (String word : words) {
                    result.put(word, result.getOrDefault(word, 0) + 1);
                }
            }
            return result;
        }
    }
}
