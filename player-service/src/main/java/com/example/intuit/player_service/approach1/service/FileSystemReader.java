package com.example.intuit.player_service.approach1.service;

import com.example.intuit.player_service.approach1.entities.Player;
import com.example.intuit.player_service.approach1.exceptions.PlayerNotFoundException;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.stream.Stream;

@Service
@Slf4j
@AllArgsConstructor
public class FileSystemReader implements IFileSystemReader {

    private IFileProvider fileProvider;

    // I assumed the file contents should not be cached because the memory consumption may get expensive and in a microservice we should keep
    // the memory consumption as low as possible. See also in the README file.
    public Stream<Player> getAllPlayers() {

        try{
            BufferedReader reader = fileProvider.getFileReader();
            CsvToBean<Player> cb = new CsvToBeanBuilder<Player>(reader)
                    .withType(Player.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withSkipLines(1) // skip headers.
           // TODO: .withMappingStrategy(mappingStrategy) - use custom mapping strategy assign default values where the csv parsing fails
                    .build();
            Stream<Player> stream = cb.stream();
            stream.onClose(() -> {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.warn("file reader was not closed properly", e);
                }
            });

            return stream;
        }
        catch (Exception e) {
            String message = "error occurred while trying to read file";
            log.error(message, e);
            throw new RuntimeException(message);
        }
    }

    public Player getSinglePlayer(String playerID) {

        try (BufferedReader br = fileProvider.getFileReader();) {
            //skipping the header line:
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields[0].equals(playerID)) {
                    return parseSingleLineToPlayer(line);  // Parse only the relevant line
                }
                // since the file is sorted by playerId, if the current id is larger than playerID, then playerID doesn't exist:
                if (fields[0].compareToIgnoreCase(playerID) > 0) {
                    break;
                }
            }
            // TODO:  another possible performance improvement level - see in the README_IMPORTANT.md file
        } catch (IOException e) {
            String message = "error occurred while trying to read file";
            log.error(message, e);
            throw new RuntimeException(message);
        }

        throw new PlayerNotFoundException(String.format("requested player not exist. playerID: %s was not found", playerID));
    }

    private Player parseSingleLineToPlayer(String line) {
        try (StringReader stringReader = new StringReader(line)) {
            CsvToBean<Player> csvToBean = new CsvToBeanBuilder<Player>(stringReader)
                    .withType(Player.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            return csvToBean.parse().getFirst();  // Parse the single line
        }
    }
}
