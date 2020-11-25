package com.github.hcsp.multithread;


public class MultiThreadWordCount2 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
//    public static Map<String, Integer> count(int threadNum, List<File> files) throws FileNotFoundException {
//        Map<String, Integer> finalResult = new HashMap<>();
//        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
//        List<Map<String, Integer>> units = new ArrayList<>();
//        for (int i = 0; i < threadNum; i++) {
//            new Thread(() -> {
//                String line;
//                Map<String, Integer> result = new HashMap<>();
//                for (File file : files
//                ) {
//                    try {
//                        lock.readLock().lock();
//                        BufferedReader reader = new BufferedReader(new FileReader(file));
//                        if (!((line = reader.readLine()) != null)) {
//                            break;
//                        }
//                        String[] words = line.split(" ");
//                        for (String word : words) {
//                            result.put(word, result.getOrDefault(word, 0) + 1);
//                        }
//                        units.add(result);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }  finally {
//                        lock.readLock().unlock();
//                    }
//                }
//            }).start();
//        }
//
//        for (Map<String, Integer> unit : units
//        ) {
//            for (Map.Entry<String, Integer> entry : unit.entrySet()
//            ) {
//                String word = entry.getKey();
//                int finalCount = finalResult.getOrDefault(word, 0) + entry.getValue();
//                finalResult.put(entry.getKey(), finalCount);
//            }
//        }
//        return finalResult;
//    }
}
