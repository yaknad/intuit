package com.example.intuit.player_service.approach1.controller;

import com.example.intuit.player_service.approach1.entities.Player;
import com.example.intuit.player_service.approach1.exceptions.PlayerNotFoundException;
import com.example.intuit.player_service.approach1.service.IFileSystemReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.stream.Stream;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.*;

@WebMvcTest(PlayerController.class)
@ExtendWith(MockitoExtension.class)
class PlayerControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IFileSystemReader fileReader;

    // using Spy because I want an actual model mapper so I'll not have to setup the mock for all the Player fields
    @SpyBean
    private ModelMapper modelMapper;

    @InjectMocks
    private PlayerController playerController;

    @Test
    void getAllPlayers_ShouldReturnAllPlayersDtos() throws Exception {
        Player player1 = new Player("abc1", 1910, 1, 12, "US", "NY", "NY", 2000, 1, 12, "CA", "ON", "QU", "John", "Doe", "Johna", 20, 180, 'R', 'R', "2020/8/3", "2005/1/6", "ac", "abc1");
        Player player2 = new Player("abd2", 1905, 2, 22, "CA", "ON", "QU", 2000, 1, 12, "US", "NY", "NY", "Jane", "Doe", "Jane", 22, 170, 'L', 'R',"2019/4/6", "2007/1/6", "ar", "abd2");

        when(fileReader.getAllPlayers()).thenReturn(Stream.of(player1, player2));

        mockMvc.perform(get("/api/approach1/players"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].playerID", is(player1.getPlayerID())))
                .andExpect(jsonPath("$[0].nameFirst", is(player1.getNameFirst())))
                .andExpect(jsonPath("$[0].nameLast", is(player1.getNameLast())))
                .andExpect(jsonPath("$[0].birthCountry", is(player1.getBirthCountry())))
                .andExpect(jsonPath("$[1].playerID", is(player2.getPlayerID())))
                .andExpect(jsonPath("$[1].nameFirst", is(player2.getNameFirst())))
                .andExpect(jsonPath("$[1].nameLast", is(player2.getNameLast())))
                .andExpect(jsonPath("$[1].birthCountry", is(player2.getBirthCountry())));

        verify(fileReader, times(1)).getAllPlayers();
    }

    @Test
    void getAllPlayers_fileReaderThrowsRuntimeException_ShouldReturn500() throws Exception {

        when(fileReader.getAllPlayers()).thenThrow(RuntimeException.class);

        mockMvc.perform(get("/api/approach1/players"))
                .andExpect(status().isInternalServerError());

        verify(fileReader, times(1)).getAllPlayers();
    }

    @Test
    void getPlayerById_ShouldReturnPlayerDto() throws Exception {

        Player player = new Player("abc1", 1910, 1, 12, "US", "NY", "NY", 2000, 1, 12, "CA", "ON", "QU", "John", "Doe", "Johna", 20, 180, 'R', 'R', "2020/8/3", "2005/1/6", "ac", "abc1");

        when(fileReader.getSinglePlayer("abc1")).thenReturn(player);

        mockMvc.perform(get("/api/approach1/players/abc1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerID", is(player.getPlayerID())))
                .andExpect(jsonPath("$.nameFirst", is(player.getNameFirst())))
                .andExpect(jsonPath("$.nameLast", is(player.getNameLast())))
                .andExpect(jsonPath("$.birthCountry", is(player.getBirthCountry())));

        verify(fileReader, times(1)).getSinglePlayer(player.getPlayerID());
    }

    @Test
    void getPlayerById_ShouldReturnNotFound_WhenPlayerDoesNotExist() throws Exception {
        when(fileReader.getSinglePlayer("999")).thenThrow(new PlayerNotFoundException("Player with ID 999 not found"));

        mockMvc.perform(get("/api/approach1/players/999"))
                .andExpect(status().isNotFound());

        verify(fileReader, times(1)).getSinglePlayer("999");
    }

    @Test
    void getPlayerById_ShouldReturnBadRequest_WhenPlayerIdIsBlank() throws Exception {

        mockMvc.perform(get("/api/approach1/players/ "))
               .andExpect(status().isBadRequest());

        verify(fileReader, never()).getSinglePlayer(anyString());
    }
}
