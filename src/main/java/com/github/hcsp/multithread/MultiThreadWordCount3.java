package com.github.hcsp.multithread;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MultiThreadWordCount3 {
    //多线程操作files， 单线程读取文件
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {
        return MultiThreadWordCount2.count(1, files);
    }
}
