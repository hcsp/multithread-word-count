package com.github.hcsp.multithread;

import com.google.common.collect.Lists;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.opentest4j.TestAbortedException;

public class MultiThreadWordCountTest {
    private static final int REPEAT = 100;

    @TestFactory
    public Iterable<DynamicTest> test() {
        return IntStream.range(1, 10)
                .mapToObj(this::loadClass)
                .filter(Objects::nonNull)
                .map(this::createOneTest)
                .collect(Collectors.toList());
    }

    private Class<?> loadClass(int i) {
        try {
            return Class.forName("com.github.hcsp.multithread.MultiThreadWordCount" + i);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private DynamicTest createOneTest(Class<?> testClass) {
        return DynamicTest.dynamicTest(testClass.getSimpleName(), () -> testOne(testClass));
    }

    private void testOne(Class<?> testClass) {
        try {
            Method countMethod = testClass.getMethod("count", int.class, List.class);
            Map<String, Integer> countResult =
                    (Map) countMethod.invoke(null, 10, createTestFiles());
            System.out.println(countResult);
            Assertions.assertTrue(countResult.values().stream().allMatch(n -> n == REPEAT));
        } catch (NoSuchMethodException e) {
            throw new TestAbortedException();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<File> createTestFiles() {
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
