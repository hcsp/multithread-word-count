package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class MultiThreadWordCount5 {

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws FileNotFoundException {
        MultiFilesReader multiFilesReader = new MultiFilesReader(files);
        Map<String, Integer> wordCount = new HashMap<>();

        List<MultiFilesReaderStream> streams = new ArrayList<>();

        for (int i = 0; i < threadNum; i++) {
            streams.add(new MultiFilesReaderStream(multiFilesReader));
        }

        streams.parallelStream().forEach(multiFilesReaderStream -> {
            mergeTaskResultIntoWordCountResult(wordCount, multiFilesReaderStream.readFile());
        });

        return wordCount;
    }

    public static class MultiFilesReaderStream {
        MultiFilesReader multiFilesReader;

        public MultiFilesReaderStream(MultiFilesReader multiFilesReader) {
            this.multiFilesReader = multiFilesReader;
        }

        public Map<String, Integer> readFile() {
            String line = null;
            Map<String, Integer> wordCount = new HashMap<>();
            while (true) {
                try {
                    if ((line = multiFilesReader.readLine()) == null) {
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                assert line != null;
                String[] words = line.split(" ");

                for (String word : words) {
                    wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
                }
            }

            return wordCount;
        }
    }

    public static synchronized void mergeTaskResultIntoWordCountResult(
            Map<String, Integer> wordCountResult,
            Map<String, Integer> workerResult) {
        for (Map.Entry<String, Integer> entry : workerResult.entrySet()) {
            String word = entry.getKey();
            wordCountResult.put(word, wordCountResult.getOrDefault(word, 0) + entry.getValue());
        }
    }

    public static class MultiFilesReader {
        Queue<File> files = new LinkedList<>();
        private BufferedReader reader;

        public MultiFilesReader(List<File> files) throws FileNotFoundException {
            this.files.addAll(files);
            pollReader();
        }

        private synchronized void pollReader() throws FileNotFoundException {
            File file = files.poll();
            if (file == null) {
                reader = null;
            } else {
                reader = new BufferedReader(new FileReader(file));
            }
        }

        public synchronized String readLine() throws IOException {
            if (reader == null) {
                return null;
            }
            String line = reader.readLine();
            if (line == null) {
                pollReader();
                return readLine();
            }
            return line;
        }
    }
}
