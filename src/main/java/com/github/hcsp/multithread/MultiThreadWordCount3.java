package com.github.hcsp.multithread;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class MultiThreadWordCount3 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    //使用countDownLatch
    public static Map<String, Integer> count(int threadNum, List<File> files) throws Exception {
        //使用多个线程统计
        ArrayList<Map<String, Integer>> maps = new ArrayList<>();
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        CountDownLatch countDownLatch = new CountDownLatch(files.size());
        
        for (int i = 0; i<threadNum; i++){
            Thread thread = new Thread(() -> {
                 synchronized (files){
                 System.out.println(""+(countDownLatch.getCount()));
                 maps.add(MultiThreadWordCount1.merge(files.get((int) (countDownLatch.getCount()-1))));
                 countDownLatch.countDown();
                 }
            });
            thread.start();
        }
        //主线程等待执行
        countDownLatch.await();
        System.out.println("线程等待彻底结束");

        for (Map<String, Integer> stringIntegerMap : maps) {
            Set<String> strings = stringIntegerMap.keySet();
            for (String string : strings) {
                Integer num = map.putIfAbsent(string, stringIntegerMap.get(string));
                if (num==null){
                    //不存在  存入该值
                    map.put(string, stringIntegerMap.get(string));
                } else {
                    //存在 相加
                    map.put(string, stringIntegerMap.get(string)+map.get(string));
                }

            }
        }
        return map;
    }
    public static void main(String[] args) throws Exception {

        Map<String, Integer> count = count(2, Arrays.asList(new File("1.txt"), new File("2.txt")));
        System.out.println(count.toString());
    }
}
