package com.github.hcsp.multithread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MultiThreadWordCount2 {
    //使用threadNum个线程，并发统计文件中各单词的数量
    //使用 juc 包中的 Lock/Condition
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        Lock lock = new ReentrantLock();
            ArrayList<Map<String, Integer>> maps = new ArrayList<>();
            ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
            AtomicInteger nums = new AtomicInteger(0);
            Condition condition = lock.newCondition();
            for (int i = 0; i < threadNum; i++) {
                Thread thread = new Thread(() -> {
                    try {
                        lock.lock();
                        File file = files.get(nums.get());
                        if (nums.get() != files.size()) {
                            maps.add(MultiThreadWordCount1.merge(file));
                            nums.getAndIncrement();
                        }
                        //提醒主线程已完成
                        System.out.println("通知主线程完成");
                        condition.signal();
                    } finally {
                        lock.unlock();
                    }
                });
                thread.start();
            }
            //主线程等待执行
            lock.lock();
            while (nums.get() != files.size()) {
                System.out.println("线程等待");
                condition.await();
            System.out.println("线程等待结束");
            }
            lock.unlock();
            System.out.println("线程等待彻底结束");
            for (Map<String, Integer> stringIntegerMap : maps) {
                Set<String> strings = stringIntegerMap.keySet();
                for (String string : strings) {
                    Integer num = map.putIfAbsent(string, stringIntegerMap.get(string));
                    if (num == null) {
                        //不存在  存入该值
                        map.put(string, stringIntegerMap.get(string));
                    } else {
                        //存在 相加
                        map.put(string, stringIntegerMap.get(string) + map.get(string));
                    }

                }
            }
            return map;
    }
    public static void main(String[] args) throws InterruptedException {

        Map<String, Integer> count = count(2, Arrays.asList(new File("1.txt"), new File("2.txt")));
        System.out.println(count.toString());
    }
}
