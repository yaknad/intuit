package com.example.intuit.player_service.approach1.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@Slf4j
public class FileProvider implements IFileProvider {

    // NOTE: I've located the player.csv file in the resources for convenience - since I don't know where will this service run and where will be the actual file.
    // In a realistic scenario, the file will be stored on a remote file server and be connected via SMB / SFTP etc. or maybe on S3 etc.
    //TODO: use ConfigurationProperties instead of @Value (I tried but it could not locate the file url when using ClassPathResource... try adding a prefix /)
    //private AppConfig appConfig;

    @Value("classpath:static/player.csv")
    Resource resourceFile;

    @Override
    public BufferedReader getFileReader() {
        try {
            return Files.newBufferedReader(Path.of(resourceFile.getURI()));
        } catch (IOException e) {
            log.error("error creating bufferedReader to file", e);
            throw new RuntimeException("error creating bufferedReader to file", e);
        }
    }
}