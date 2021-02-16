package com.github.hcsp.multithread;

import com.sun.xml.internal.ws.addressing.WsaActionUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    //使用wait() 和 notify()
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        //使用多个线程统计
        ArrayList<Map<String, Integer>> maps = new ArrayList<>();
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        AtomicInteger nums = new AtomicInteger(0);
        for (int i = 0; i<threadNum; i++){
            Thread thread = new Thread(() -> {
               synchronized (nums){
                   File file = files.get(nums.get());
                   if (nums.get() !=files.size()){
                       maps.add(MultiThreadWordCount1.merge(file));
                       nums.getAndIncrement();
                   }
                   //提醒主线程已完成
                   System.out.println("通知主线程完成");
                   nums.notify();
               }
            });
            thread.start();
        }
        //主线程等待执行
        synchronized (nums){
            while (nums.get()!=files.size()) {
                System.out.println("线程等待");
                nums.wait();
                System.out.println("线程等待结束");
            }
        }
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




    public static void main(String[] args) throws InterruptedException {

        Map<String, Integer> count = count(2, Arrays.asList(new File("1.txt"), new File("2.txt")));
        System.out.println(count.toString());
    }

    //主线程生产  多线程消费




    /**
     *
     *
     * @description: 合并map
     * @return: map<String,Integer>
     * @author: luojw
     * @param file
     * @return Map<String, Integer>
     * @time:
     */
    public static Map<String, Integer> merge(File file){
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        if (file != null) {
            try {
                List<String> strings = Files.readAllLines(file.toPath());
                for (String string : strings) {
                    String[] s = string.split(" ");
                    for (String s1 : s) {
                        if (map.putIfAbsent(s1, 1) == null) {
                            map.put(s1, 1);
                        } else {
                            map.put(s1, map.get(s1) + 1);
                        }

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return  map;
    }
}
