package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class WordCount {

    private final int threadNum;
    private ExecutorService threadPool;

    public WordCount(int threadNum) {
        threadPool = Executors.newFixedThreadPool(threadNum);
        this.threadNum = threadNum;
    }

    // 统计文件中各单词的数量
    public Map<String, Integer> count(List<File> file) throws FileNotFoundException, ExecutionException, InterruptedException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();

        // 开辟若干个线程，每个线程读取文件的一行内容，并将单词统计结果返回
        // 最后主线程将工作线程返回的结果汇总在一起
        // 似乎即使开了10个线程，但每个线程其实做了一样的事情，都是从头读到尾...
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
                System.out.println(Thread.currentThread().getName());
                System.out.println(line);
                System.out.println();
                for (String word : words) {
                    result.put(word, result.getOrDefault(word, 0) + 1);
                }
            }
            return result;
        }
    }

    public static void main(String[] args) throws FileNotFoundException, ExecutionException, InterruptedException {
        File file = new File("C:\\Users\\admin\\Projects\\tmp\\multithread-word-count\\src\\main\\java\\com\\github\\hcsp\\multithread\\testWords2.txt");
        WordCount wordCount = new WordCount(10);
        long t0 = System.currentTimeMillis();
        Map<String, Integer> countResult = wordCount.count(file);
        long t1 = System.currentTimeMillis();
        System.out.println(countResult);
        System.out.println(t1 - t0);
    }

//    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        ExecutorService threadPool = Executors.newFixedThreadPool(10);
//
//        // submit 方法会立刻返回（参考 JS 中的 promise）
//        // 提交的任务会异步执行，不会阻塞当前线程
//        Future<Integer> future1 = threadPool.submit(new Callable<Integer>() {
//            @Override
//            public Integer call() throws Exception {
//                Thread.sleep(3000);
//                return 0;
//            }
//        });
//
//        Future<String> future2 = threadPool.submit(new Callable<String>() {
//            @Override
//            public String call() throws Exception {
//                Thread.sleep(1000);
//                return "ojbk";
//            }
//        });
//
//        Future<Object> future3 = threadPool.submit(new Callable<Object>() {
//            @Override
//            public Object call() throws Exception {
//                throw new RuntimeException();
//            }
//        });
//
//        System.out.println(future1.get());
//        System.out.println(future2.get());
//        System.out.println(future3.get());
//    }

}
