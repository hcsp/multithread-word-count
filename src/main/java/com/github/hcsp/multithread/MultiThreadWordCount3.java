package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MultiThreadWordCount3 {
    private static Map<String, Integer> result = new ConcurrentHashMap<>();

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws Exception {
        Task task = new Task(files);
        for (int i = 0; i < threadNum; i++) {
            Thread pro = new Thread(new Producer(task));
            Thread con = new Thread(new Consumer(task));
            pro.start();
            con.start();
            pro.join();
            con.join();
        }
        return result;
    }

    static class Producer implements Runnable {
        private Task task;

        public Producer(Task task) {
            this.task = task;
        }

        @Override
        public void run() {
            task.provider();
        }
    }

    static class Consumer implements Runnable {
        private Task task;

        public Consumer(Task task) {
            this.task = task;
        }

        @Override
        public void run() {
            task.consumer();
        }
    }

    static class Task {
        private List<File> files;

        private LinkedList<File> queue = new LinkedList<>();
        private Lock lock = new ReentrantLock();
        private Condition providerCondition = lock.newCondition();
        private Condition consumerCondition = lock.newCondition();


        public void provider() {
            try {
                lock.lock();
                while (queue.size() >= 1) {
                    providerCondition.await();
                }
                if (files.size() > 0) {
                    queue.add(files.get(0));
                    files.remove(0);
                    consumerCondition.signal();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

        public void consumer() {
            try {
                lock.lock();
                while (queue.size() == 0) {
                    consumerCondition.await();
                }
                while (queue.size() > 0) {
                    File file = queue.poll();
                    readFile(file);
                }
                providerCondition.signal();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
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

        public Task(List<File> files) {
            this.files = files;
        }
    }


}
