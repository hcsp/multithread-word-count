package com.github.hcsp.multithread;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class MultiThreadWordCount1 {
    //开启n个线程，对合并的文件流进行逐行读取，线程争抢合并流的资源
    public static Map<String, Integer> count(int threadNum, List<File> files) throws IOException, ExecutionException, InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        //将多个输入流合并成一个
        Vector<InputStream> inputStreams = new Vector<>();
        for (File file: files){
            InputStream inputStream = new FileInputStream(file);
            inputStreams.add(inputStream);
        }
        SequenceInputStream sequenceInputStream = new SequenceInputStream(inputStreams.elements());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(sequenceInputStream));
        List<Future<Map<String, Integer>>> futureList = new ArrayList<>();
        Object lock = new Object();
        //读取输入流并计算结果
        for (int i=0; i<threadNum; i++){
            futureList.add(threadPool.submit(new Callable<Map<String, Integer>>() {
                @Override
                public Map<String, Integer> call() throws Exception {
                    Map<String, Integer> wordMap = new HashMap<>();
                    while (true){
                        String line = bufferedReader.readLine();
                        if (line == null){
                            break;
                        }
                        String[] words = line.split(" ");
                        for (String word : words){
                            synchronized (lock){
                                wordMap.put(word, wordMap.getOrDefault(word, 0)+1);
                            }
                        }
                    }

                    return wordMap;
                }
            }));
        }

        Map<String, Integer> result = new HashMap<String, Integer>();
        for (Future<Map<String, Integer>> future: futureList){
            Map<String, Integer> wordMap = future.get();
            for (Map.Entry<String, Integer> entry : wordMap.entrySet()){
                String word = entry.getKey();
                Integer value = entry.getValue();
                result.put(word, result.getOrDefault(word, 0) +value);
            }
        }

        bufferedReader.close();

        return result;
    }
}
