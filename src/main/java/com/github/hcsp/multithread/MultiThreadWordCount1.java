package com.github.hcsp.multithread;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MultiThreadWordCount1 extends Thread {
    public static final Map<String, Integer> resultMap = new ConcurrentHashMap<>();

    public static Map<String, Integer> count(int threadNum, List<File> files) {
        for (int i = 0; i < threadNum; ++i) {
            new Thread(new ProcessFileList(GetWillProcessFileList.getWillProcessFileList(threadNum, files, i), resultMap)).start();
        }

        return resultMap;
    }
}
