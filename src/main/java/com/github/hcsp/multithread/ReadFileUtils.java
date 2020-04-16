package com.github.hcsp.multithread;

import com.google.common.collect.Lists;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ReadFileUtils {
    public static void readWordsToMap(Map<String, Integer> map, BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] words = line.split(" ");
            for (String word : words) {
                map.put(word, map.getOrDefault(word, 0) + 1);
            }
        }
    }

    public static Map<String, Integer> mergeMap(Map<String, Integer> result, Map<String, Integer> source) {
        Set<String> strings = source.keySet();
        for (String key : strings) {
            result.put(key, result.getOrDefault(key, 0) + source.get(key));
        }
        return result;
    }

    private static final int REPEAT = 100;
    public List<File> createTestFiles() {
        // 2600 lines of random aligned strings
        List<String> randomStrings =
                IntStream.range(0, 26)
                        .mapToObj(i -> String.valueOf((char) ('a' + i)))
                        .flatMap(letter -> Collections.nCopies(REPEAT, letter).stream())
                        .collect(Collectors.toList());

        Collections.shuffle(randomStrings);

        // Write to 10 files
        return Lists.partition(randomStrings, 26 * REPEAT / 10).stream()
                .map(this::writeToTempFile)
                .collect(Collectors.toList());
    }

    private File writeToTempFile(List<String> oneCharacterLines) {
        try {
            File file = File.createTempFile("tmp", "");

            List<String> lines =
                    Lists.partition(oneCharacterLines, 10).stream()
                            .map(list -> String.join(" ", list))
                            .collect(Collectors.toList());
            Files.write(file.toPath(), lines);
            return file;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
