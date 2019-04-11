package com.eresearch.dblp.consumer.service;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Log4j

@Component
public class CaptureDblpResponseService {

    @Value("${capture-service.path-to-store-files}")
    private String pathToStoreFiles;

    public void log(String filename, String contents, String fileType) {
        try {
            Path path = Paths.get(pathToStoreFiles, filename + "." + fileType);

            Files.deleteIfExists(path);
            Files.createFile(path);

            try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path)) {
                bufferedWriter.write(contents);
                bufferedWriter.newLine();
            }
        } catch (Exception error) {
            log.error("error occurred: " + error.getMessage(), error);
        }
    }

}
