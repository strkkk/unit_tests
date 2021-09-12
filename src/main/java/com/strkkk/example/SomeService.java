package com.strkkk.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SomeService {
    private FileService fileService;

    public SomeService(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * Write value to a file
     *
     * @param value value to write
     * @throws IllegalArgumentException if value is null or blank
     */
    public void writeString(String value) throws IOException {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("value");
        }
        Path filePath = Paths.get(fileService.getFilePath());
        Files.writeString(filePath, value);
        fileService.closeFile();
    }

    /**
     * Write message to a file
     *
     * @param value value to write
     * @throws IllegalArgumentException if value is null or blank
     */
    public void writeMessage(String value) throws IOException {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("value");
        }
        fileService.write(new Message(value, System.currentTimeMillis()));
    }

    /**
     * Creates a string identifier based on brand name
     *
     * @throws IllegalArgumentException if value is null
     */
    public String createIdentifier(Brands brand) {
        if (brand == null) {
            throw new IllegalArgumentException("brand");
        }
        return brand.name() + "_generated";
    }

    /**
     * Creates a string from message
     *
     */
    public String transformMessage(Message message) {
        return message.getValue() + ", " + message.getTimestamp();
    }

}

