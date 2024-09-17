package com.example.intuit.player_service.approach1.controller;

import com.example.intuit.player_service.approach1.dto.PlayerDto;
import com.example.intuit.player_service.approach1.entities.Player;
import com.example.intuit.player_service.approach1.service.IFileSystemReader;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/approach1/players")
@Slf4j
@AllArgsConstructor
public class PlayerController {

    private IFileSystemReader fileReader;
    private ModelMapper modelMapper;

    @GetMapping
    public Stream<PlayerDto> getAllPlayers() {
        log.info("requested all players");
        return fileReader.getAllPlayers().map(this::mapToPlayerDto);
    }

    @GetMapping("/{playerID}")
    public PlayerDto getPlayerById(@PathVariable @NotNull @NotBlank String playerID) {
        log.info("requested player with ID {}", playerID);
        // preferred not using Optional, and if not exist, throw an exception that will be globally handled by the ControllerAdvice
        var player = fileReader.getSinglePlayer(playerID);
        return mapToPlayerDto(player);
    }

    private PlayerDto mapToPlayerDto(Player player) {
        return modelMapper.map(player, PlayerDto.class);
    }
}
