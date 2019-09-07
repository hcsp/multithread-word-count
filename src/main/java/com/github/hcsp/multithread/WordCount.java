package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WordCount {
    private final int threadNum;
    //调用ExecutorService 建线程池
    ExecutorService threadPool;

    public WordCount(int threadNum) {
        threadPool = Executors.newFixedThreadPool(threadNum);
        this.threadNum = threadNum;
    }

    // 统计文件中各单词的数量
    public Map<String, Integer> count(File file) throws FileNotFoundException, ExecutionException, InterruptedException {
        //创建一个Future 的List 用来存放每个进程所统计的数据
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        //先创建BufferedReader读取文件内容
        BufferedReader reader = new BufferedReader(new FileReader(file));
        for (int i = 0; i < threadNum; i++) {
            //调用workerJob，给每个线程指定要做的任务
            futures.add(workerJob(reader));
        }
        //每个线程完成上面工作后,就需要将每个线程所得的值进行一个整合
        Map<String, Integer> finalResult = new HashMap<>();
        //遍历每个进程对象，将所有进程对象放在一个Map中
        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> resultFromWorker = future.get();
            //调用函数进行具体的整合操作
            mergeWorkerResultIntoFinalResult(resultFromWorker, finalResult);
        }
        return finalResult;
    }

    private void mergeWorkerResultIntoFinalResult(Map<String, Integer> resultFromWorker,
                                                  Map<String, Integer> finalResult) {
        //Map.Entry 用于遍历Map中的所有键值对，resultFromWorker.entrySet将键值变成Set
        for (Map.Entry<String, Integer> entry : resultFromWorker.entrySet()) {
            //拿到键
            String word = entry.getKey();
            //将所有进程所得到的键值进行相加
            int mergeResultNumber = finalResult.getOrDefault(word, 0) + entry.getValue();
            //将对应的键值对放入最终结果集里
            finalResult.put(word, mergeResultNumber);

        }

    }

    private Future<Map<String, Integer>> workerJob(BufferedReader reader) {
        //worker主要工作是向线程池提交即将要做的事
        return threadPool.submit(() -> {
            String lines;
            Map<String, Integer> result = new HashMap<>();
            //如果result表中含有该键值，就将键值进行加1操作；如果没有包含，就将该键值设为1
            while ((lines = reader.readLine()) != null) {
                String[] words = lines.split(" ");
                for (String word : words) {
                    result.put(word, result.getOrDefault(word, 0) + 1);
                }
            }
            return result;
        });
    }
}
