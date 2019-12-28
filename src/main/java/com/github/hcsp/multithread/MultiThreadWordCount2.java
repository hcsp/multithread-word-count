package com.github.hcsp.multithread;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class MultiThreadWordCount2 {
    //使用forkJoin框架，对合并流进行一定粒度的切分，每个子任务特定数量行的统计
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {
        //提取文件队列
        //分发n条线程
        //读取第一个文件，读取完成则读取下一个文件
        //所有线程读取完成时，退出聚合最终结果
        ForkJoinPool forkJoinPool = new ForkJoinPool(threadNum);
        //forkjoin是把大任务按一定尺度进行分解
        ForkJoinTask<Map<String, Integer>> reslut = forkJoinPool.submit(new FileCount(files));

        return reslut.get();
    }

    public static class FileCount extends RecursiveTask<Map<String, Integer>> {
        private final int SIZE = 3; //3个文件为一组s
        List<File> files;

        public FileCount(List<File> files){
            this.files = files;
        }

        @Override
        protected Map<String, Integer> compute() {
            if (this.files.size()<=SIZE){
                Map<String, Integer> result = new ConcurrentHashMap<>();
                for (File file : files){

                    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))){
                        while (true){
                            String line = bufferedReader.readLine();
                            if (line == null){
                                break;
                            }
                            countWords(result, line);
                        }

                    } catch (IOException exp) {
                        throw new RuntimeException(exp);
                    }

                }

                return result;

            } else {
                RecursiveTask<Map<String, Integer>> aTask = new FileCount(files.subList(0, files.size()/2));
                RecursiveTask<Map<String, Integer>> bTask = new FileCount(files.subList(files.size()/2,  files.size()));
                invokeAll(aTask, bTask);
                return assign(aTask.join(), bTask.join());

            }

        }
    }

    private static synchronized Map<String, Integer> assign(Map<String, Integer> originWordMap, Map<String, Integer> targetWordMap){
        for (Map.Entry<String, Integer> entry: targetWordMap.entrySet()){
            String word = entry.getKey();
            Integer value = entry.getValue();
            originWordMap.put(word, originWordMap.getOrDefault(word, 0)+value);
        }
        return originWordMap;
    }

    private static Object lock = new Object();

    private static void countWords(Map<String, Integer> wordMap, String content){
        if (content == null){
            return;
        }

        String[] words = content.split(" ");

        for (String word: words){
            synchronized (lock){
                wordMap.put(word, wordMap.getOrDefault(word, 0)+1);
            }
        }
    }
}
