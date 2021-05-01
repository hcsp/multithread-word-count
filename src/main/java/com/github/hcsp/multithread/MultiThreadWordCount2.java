package com.github.hcsp.multithread;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreadWordCount2 {
    private static Map<String, Integer> result = new HashMap<>();

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        Container container = new Container();
        Producer producer = new Producer(threadNum, files, container);
        Consumer consumer = new Consumer(threadNum, container);

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();
        return result;
    }

    static class Container extends Thread {
        File value;
    }

    static class Producer extends Thread {
        Container container;
        int threadNum;
        List<File> files;

        public Producer(int threadNum, List<File> files, Container container) {
            this.container = container;
            this.threadNum = threadNum;
            this.files = files;
        }

        @Override
        public void run() {
            for (int i = 0; i < threadNum; i++) {
                synchronized (container) {
                    // 若条件不满足,就等待
                    while (null != container.value) {
                        try {
                            container.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if(files.size() > 0){
                        container.value = files.get(0);
                        files.remove(0);
                        container.notify();
                    }
                }
            }
        }
    }


    static class Consumer extends Thread {
        Container container;
        int threadNum;

        public Consumer(int threadNum, Container container) {
            this.threadNum = threadNum;
            this.container = container;
        }

        @Override
        public void run() {
            for (int i = 0; i < threadNum; i++) {
                synchronized (container) {
                    // 若条件不满足,就等待
                    while (null == container.value) {
                        try {
                            container.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                    try {
                        readFile(container.value);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    container.value = null;
                    container.notifyAll();
                }
            }
        }
    }

    private static void readFile(File file) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String strLine = null;
        while (null != (strLine = bufferedReader.readLine())) {
            String[] words = strLine.split("\\s");
            for (String word : words) {
                Integer num = result.getOrDefault(word, 0);
                num++;
                result.put(word, num);
            }
        }
        bufferedReader.close();
    }

}
