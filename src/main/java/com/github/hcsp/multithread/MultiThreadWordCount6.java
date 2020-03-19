package com.github.hcsp.multithread;

/**
 * parallelStream
 */
public class MultiThreadWordCount6 {

    // 使用threadNum个线程，并发统计文件中各单词的数量
//    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException, ExecutionException {
//        Map<String, Integer> freqMap = new HashMap<>();
//        Optional<Map<String, Integer>> result = files.parallelStream().map(file -> FileUtil.count(file, freqMap)).reduce((map, map2) -> FileUtil.mergeCountMap(map, freqMap));
//        return null;
//    }
}
