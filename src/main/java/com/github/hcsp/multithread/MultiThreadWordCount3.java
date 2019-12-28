package com.github.hcsp.multithread;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class MultiThreadWordCount3 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        Map<String, Integer> result = new ConcurrentHashMap<>();

        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch endSignal = new CountDownLatch(threadNum);

        Vector<InputStream> inputStreams = fileToVectorWithAutoClose(files);
        SequenceInputStream sequenceInputStream = new SequenceInputStream(inputStreams.elements());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(sequenceInputStream));
        Object lock = new Object();
        for (int i=0; i<threadNum; i++){
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        startSignal.await();
                        while (true) {
                            String line = bufferedReader.readLine();
                            if (line == null) {
                                break;
                            }
                            String[] words = line.split(" ");

                            for (String word : words) {
                                synchronized (lock){//避免下面这种非原子性的操作
                                    result.put(word, result.getOrDefault(word, 0) + 1);
                                }
                            }
                        }

                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }

                    endSignal.countDown(); // 通过-1操作通知外界，这里的数据好了
                }
            }).start();
        }
        startSignal.countDown();
        endSignal.await(); //这里会一直阻塞，直到内部的num为0
        try {
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    //避免一个文件报错，导致一堆已经打开的那批文件没有被关闭
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
