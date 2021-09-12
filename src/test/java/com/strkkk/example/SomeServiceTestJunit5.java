package com.strkkk.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * FIRST
 * Fast
 * Independent
 * Repeatable
 * Self-validating
 * Thorough happy path + null, empty
 */
@ExtendWith(MockitoExtension.class)
public class SomeServiceTestJunit5 {
    // coverage
    // local -> idea coverage plugin or import jacoco results
    // maven -> jacoco

    @Mock
    FileService fileService;

    SomeService testObj;

    @TempDir
    static Path sharedTempDir;

    @BeforeEach
    void setUp() {
        // other way is to use @InjectMocks on testObj
        testObj = new SomeService(fileService);
    }

    @Test
    void shouldWriteToExternalFile() throws IOException {
        // given
        File file = new File(sharedTempDir + "\\some.txt");
        file.createNewFile();
        Mockito.when(fileService.getFilePath()).thenReturn(file.toString());

        // invocation
        testObj.writeString("some value");

        // then
        Mockito.verify(fileService, times(1)).closeFile();
        List<String> strings = Files.readAllLines(file.toPath());
        assertEquals(1, strings.size());
        assertEquals("some value", strings.get(0));
    }

    @Test
    void testWriteSomePropertyEnabled() throws IOException {
        Assumptions.assumeTrue("ci".equals(System.getProperty("env")));
        File file = new File(sharedTempDir + "/some.txt");
        file.createNewFile();
        // given
        Mockito.when(fileService.getFilePath()).thenReturn(file.toString());

        // invocation
        testObj.writeString("some value");

        // then
        Mockito.verify(fileService, times(1)).closeFile();
        List<String> strings = Files.readAllLines(file.toPath());
        assertEquals(1, strings.size());
        assertEquals("some value", strings.get(0));
    }

    @Test
    void testWriteMessage() throws IOException {

        // invocation
        testObj.writeMessage("some value");

        // then
        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        Mockito.verify(fileService, times(1)).write(captor.capture());
        Message result = captor.getValue();
        assertEquals("some value", result.getValue());
        Assertions.assertTrue(System.currentTimeMillis() - 1000 < result.getTimestamp());
    }

    private static Stream<Arguments> messages() {
        return Stream.of(
            Arguments.of(new Message("test", 1234L), "test, 1234"),
            Arguments.of(new Message("", 12345L), ", 12345")
        );
    }

    @ParameterizedTest
    @MethodSource("messages")
    void testWriteMessage(Message msg, String expectedResult) {

        // invocation
        String result = testObj.transformMessage(msg);

        // then
        assertEquals(expectedResult, result);
    }

    @ParameterizedTest
    @NullSource
    void shouldFailOnInvalidInputString(String value) {

        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class,
            () -> testObj.writeString(value));

        assertEquals(ex.getMessage(), "value");
    }

    @Test
    void testBrandGeneration() {
        String result = testObj.createIdentifier(Brands.Apple);
        assertEquals(result, "Apple_generated");
    }

    @ParameterizedTest
    @EnumSource(Brands.class)
    void testBrandGeneration2(Brands brand) {
        String result = testObj.createIdentifier(brand);
        assertEquals(result, brand.name() + "_generated");
    }


}
