package com.example.intuit.player_service.approach1.service;

import com.example.intuit.player_service.approach1.entities.Player;
import com.example.intuit.player_service.approach1.exceptions.PlayerNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileSystemReaderTests {

    @Mock
    private IFileProvider fileProvider;

    @InjectMocks
    private FileSystemReader fileSystemReader;

    private static final String VALID_CSV =
            "playerID,birthYear,birthMonth,birthDay,birthCountry,birthState,birthCity,deathYear,deathMonth,deathDay,deathCountry,deathState,deathCity,nameFirst,nameLast,nameGiven,weight,height,bats,throws,debut,finalGame,retroID,bbrefID\n" +
            "aardsda01,1981,12,27,USA,CO,Denver,,,,,,,David,Aardsma,David Allan,215,75,R,R,2004-04-06,2015-08-23,aardd001,aardsda01\n" +
            "aaronha01,1934,2,5,USA,AL,Mobile,,,,,,,Hank,Aaron,Henry Louis,180,72,R,R,1954-04-13,1976-10-03,aaroh101,aaronha01\n" +
            "aaronto01,1939,8,5,USA,AL,Mobile,1984,8,16,USA,GA,Atlanta,Tommie,Aaron,Tommie Lee,190,75,R,R,1962-04-10,1971-09-26,aarot101,aaronto01\n" +
            "aasedo01,1954,9,8,USA,CA,Orange,,,,,,,Don,Aase,Donald William,190,75,R,R,1977-07-26,1990-10-03,aased001,aasedo01";

    private static final String VALID_SINGLE_LINE_FROM_CSV =
            "aaronha01,1934,2,5,USA,AL,Mobile,,,,,,,Hank,Aaron,Henry Louis,180,72,R,R,1954-04-13,1976-10-03,aaroh101,aaronha01\n";

    @Test
    public void testGetAllPlayers_Success() {

        BufferedReader reader = new BufferedReader(new StringReader(VALID_CSV));
        when(fileProvider.getFileReader()).thenReturn(reader);

        Stream<Player> playerStream = fileSystemReader.getAllPlayers();
        List<Player> players = playerStream.toList();

        verify(fileProvider, times(1)).getFileReader();
        assertEquals(4, players.size());
        assertEquals("Aardsma", players.get(0).getNameLast());
        assertEquals("USA", players.get(1).getBirthCountry());
        assertEquals("aaronto01", players.get(2).getPlayerID());
        assertEquals("Donald William", players.get(3).getNameGiven());
    }

    @Test
    public void testGetAllPlayers_EmptyFile() throws IOException {

        String fileContent = "playerID,birthYear,birthMonth,birthDay,birthCountry,birthState,birthCity,deathYear,deathMonth,deathDay,deathCountry,deathState,deathCity,nameFirst,nameLast,nameGiven,weight,height,bats,throws,debut,finalGame,retroID,bbrefID\n";
        BufferedReader emptyReader = new BufferedReader(new StringReader(fileContent));
        when(fileProvider.getFileReader()).thenReturn(emptyReader);

        Stream<Player> playerStream = fileSystemReader.getAllPlayers();
        List<Player> players = playerStream.toList();

        verify(fileProvider, times(1)).getFileReader();
        assertEquals(0, players.size());
    }

    @Test
    public void testGetAllPlayers_ExceptionHandling() throws IOException {

        when(fileProvider.getFileReader()).thenThrow(new RuntimeException("File not found"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> fileSystemReader.getAllPlayers());
        assertEquals("error occurred while trying to read file", exception.getMessage());
        verify(fileProvider, times(1)).getFileReader();
    }

    @Test
    public void testGetSinglePlayer_Success() throws IOException {

        BufferedReader reader = new BufferedReader(new StringReader(VALID_CSV));
        when(fileProvider.getFileReader()).thenReturn(reader);

        Player player = fileSystemReader.getSinglePlayer("aaronha01");

        assertNotNull(player);
        assertEquals("aaronha01", player.getPlayerID());
        assertEquals(1934, player.getBirthYear());
    }

    @Test
    public void testGetSinglePlayer_PlayerNotFound() throws IOException {

        BufferedReader reader = new BufferedReader(new StringReader(VALID_CSV));
        when(fileProvider.getFileReader()).thenReturn(reader);

        // When & Then
        assertThrows(PlayerNotFoundException.class, () -> fileSystemReader.getSinglePlayer("zzzz"));
    }

    @Test
    public void testGetSinglePlayer_ExceptionHandling() throws IOException {

        when(fileProvider.getFileReader()).thenThrow(new RuntimeException("error occurred while trying to read file"));

        // When & Then
        assertThrows(RuntimeException.class, () -> fileSystemReader.getSinglePlayer("2"));
    }
}
