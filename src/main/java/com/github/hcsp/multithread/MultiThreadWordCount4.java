package com.github.hcsp.multithread;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

public class MultiThreadWordCount4 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {

        Vector<InputStream> inputStreams = fileToVectorWithAutoClose(files);
        SequenceInputStream sequenceInputStream = new SequenceInputStream(inputStreams.elements());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(sequenceInputStream));
        Object lock = new Object();
        Map<String, Integer> result = new ConcurrentHashMap<>();
        IntStream list = IntStream.range(0, threadNum);
        list.parallel().forEach(i->{
            try {
                while (true){
                    String line = bufferedReader.readLine();
                    if (line==null){
                        break;
                    }
                    String[] words = line.split(" ");
                    for (String word : words) {
                        synchronized (lock){//避免下面这种非原子性的操作
                            result.put(word, result.getOrDefault(word, 0) + 1);
                        }
                    }
                }
            } catch (IOException exp) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                throw new RuntimeException(exp);
            }
        });
        try {
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
    private static Vector<InputStream> fileToVectorWithAutoClose(List<File> files){
        Map<String, Integer> result = new ConcurrentHashMap<>();

        Vector<InputStream> inputStreams = new Vector<>();
        try{

            for (File file : files){
                inputStreams.add(new FileInputStream(file));
            }

        }catch (Exception exp){
            for (InputStream is: inputStreams){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            throw new RuntimeException(exp);
        }
        return inputStreams;
    }
}
