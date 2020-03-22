package com.github.hcsp.multithread;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 使用并行流
 * parallelStream提供了流的并行处理，它是Stream的另一重要特性，其底层使用Fork/Join框架实现。
 * <p>
 * 不得不说java8引进的新方法确实可以，大大大大减少了代码量
 * files.parallelStream()   ：将文件列表转换成文件并行流
 * <p>
 * map(MultiThreadWordCount2::count2)：    map()方法用于遍历处理流中的每一个文件，括号中是处理方法
 * 对每一个文件执行MultiThreadWordCount2类中的count2方法，也就是并行统计每一个文件中的单词个数
 * 值得注意的是，这里的遍历时并行遍历的，他调用使用了ForkJoinPool进行统计，所以文件流不是被顺序遍历的
 * <p>
 * reduce()  :reduce方法用于对stream中元素进行聚合求值，最常见的用法就是将stream中一连串的值合成为单个值，
 * 比如为一个包含一系列数值的数组求和。
 * 这里第一个参数指定了返回的集合类型
 * 第二个参数是一个lambada表达式，调用了map的merge()方法对每个线程得到的结果进行合并
 */
public class MultiThreadWordCount5 {
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        return files.parallelStream()
                .map(MultiThreadWordCount2::count2)
                .reduce(new ConcurrentHashMap<>(), (m1, m2) -> {
                    m1.forEach((k, v) -> m2.merge(k, v, Integer::sum));
                    return m2;
                });
    }
}
