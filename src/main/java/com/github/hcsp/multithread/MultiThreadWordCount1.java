package com.github.hcsp.multithread;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreadWordCount1 {
    //使用synchronized,共同处理一个map
    static final Map<String, Integer> result = new HashMap<>();

    static final AtomicInteger finishedTheardNum = new AtomicInteger(0);

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws FileNotFoundException {
        //需要手动划分文本,分配给各个线程,交代完任务后,需要等到所有线程都执行完操作,再激活main线程,返回答案
        int chunkNumb = files.size() / threadNum; //计算平均每个线程分得的file数量
        synchronized (result) {//先由main Thread拿到monitor,并陷入等待
            for (int i = 0; i < threadNum; i++) {
                int begin = i * chunkNumb;
                int end = i == threadNum - 1 ? files.size() : (i + 1) * chunkNumb;
                List<File> inputFiles = files.subList(begin, end);
                new Thread(new ReadAndCount(inputFiles, threadNum)).start();
            }
            try {
                result.wait(); //等待最后一个线程完成工作后唤醒
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return result;
    }

    private static class ReadAndCount implements Runnable {
        private final List<File> inputFiles;
        private final int threadNum;

        ReadAndCount(List<File> inputFiles, int threadNum) {
            this.inputFiles = inputFiles;
            this.threadNum = threadNum;
        }

        public void run() {
            //将分配到的文件进行处理
            for (File inputFile : inputFiles) {
                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile))) {
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] words = line.split(" ");
                        //获得result的monitor,防止重写
                        mergeWordsIntoResult(words);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            //修改完成后,应该开始累积完成的线程数量,当所有线程都完成时,就唤醒main线程.
            //多线程的情况下,如果以"最后"一个线程作为完成标志会有问题,除非让每个线程都完成自己的工作所有后再释放锁
            finishedTheardNum.addAndGet(1);
            synchronized (result) {
                if (finishedTheardNum.get() == threadNum) {
                    result.notify();
                }
            }
        }

        private void mergeWordsIntoResult(String[] words) {
            synchronized (result) {
                for (String word :
                        words) {
                    result.put(word, result.getOrDefault(word, 0) + 1);
                }
            }

        }
    }
}
