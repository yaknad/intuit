package com.example.intuit.player_service.approach1.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;

// NOTE: separated from IFileSystemReader for:
// 1. Decoupling: of IFileSystemReader from the actual file system
// 2. Testability: enable FileSystemReader unit testing since now FileSystemReader doesn't operate directly on the file system
public interface IFileProvider {
    BufferedReader getFileReader();
}

