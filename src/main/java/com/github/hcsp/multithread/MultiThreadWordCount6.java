package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 使用 parallelStream() 实现
 */
public class MultiThreadWordCount6 {

    public static Map<String, Integer> count(int threadNum, List<File> files) {
        return files.parallelStream()
                .map(file -> {
                    try {
                        return Util.countWordFromOneFile(file);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .reduce(new HashMap<>(), Util::mergeTwoMap);

    }
}
