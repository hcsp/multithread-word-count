package com.github.hcsp.multithread;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreadWordCount2 {
    private static final LinkedBlockingQueue<File> queue = new LinkedBlockingQueue<>();
//     使用threadNum个线程，并发统计文件中各单词的数量
        public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
            Map<String, Integer> finalResult = new HashMap<>();
            Object lock = new Object();
            for (File file : files) {
                queue.put(file);
            }

            List<Map<String, Integer>> resultList = new ArrayList<>(files.size());
            AtomicInteger fileSize = new AtomicInteger(files.size());
            for (int i = 0; i < threadNum; i++){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        File file = queue.poll();
                        while(file != null ){
                            resultList.add(FileUtil.count(file));
                            synchronized (lock){
                                fileSize.decrementAndGet();
                                lock.notify();
                            }
                            file = queue.poll();
                        }
                    }
                }).start();
            }

            synchronized (lock){
                while(fileSize.get() > 0){
                    lock.wait();
                }
            }
            for (Map<String, Integer> map : resultList) {
                finalResult = FileUtil.merge(finalResult, map);

            }
            return finalResult;
        }
}
