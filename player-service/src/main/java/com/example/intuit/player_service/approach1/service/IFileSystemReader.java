package com.example.intuit.player_service.approach1.service;

import com.example.intuit.player_service.approach1.entities.Player;
import java.util.stream.Stream;

// common interface for all types of storages: NTFS, NFS, SFTP, S3 etc.
public interface IFileSystemReader {

    Stream<Player> getAllPlayers();
    Player getSinglePlayer(String playerID);
}
