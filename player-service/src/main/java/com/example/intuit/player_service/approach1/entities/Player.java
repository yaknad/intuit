package com.example.intuit.player_service.approach1.entities;

import com.opencsv.bean.CsvBindByPosition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    // I chose to bind by position and not by name to support mapping in the getPlayerById scenario, where I parse a single line (no headers)
    // I use Integer instead of integer in order to enable null values for missing csv fields
    @CsvBindByPosition(position = 0)
    private String playerID;
    @CsvBindByPosition(position = 1)
    private Integer birthYear;
    @CsvBindByPosition(position = 2)
    private Integer birthMonth;
    @CsvBindByPosition(position = 3)
    private Integer birthDay;
    @CsvBindByPosition(position = 4)
    private String birthCountry;
    @CsvBindByPosition(position = 5)
    private String birthState;
    @CsvBindByPosition(position = 6)
    private String birthCity;
    @CsvBindByPosition(position = 7)
    private Integer deathYear;
    @CsvBindByPosition(position = 8)
    private Integer deathMonth;
    @CsvBindByPosition(position = 9)
    private Integer deathDay;
    @CsvBindByPosition(position = 10)
    private String deathCountry;
    @CsvBindByPosition(position = 11)
    private String deathState;
    @CsvBindByPosition(position = 12)
    private String deathCity;
    @CsvBindByPosition(position = 13)
    private String nameFirst;
    @CsvBindByPosition(position = 14)
    private String nameLast;
    @CsvBindByPosition(position = 15)
    private String nameGiven;
    @CsvBindByPosition(position = 16)
    private Integer weight;
    @CsvBindByPosition(position = 17)
    private Integer height;
    @CsvBindByPosition(position = 18)
    private Character bats;
    @CsvBindByPosition(position = 19)
    private Character throwsHand;
    // I've chosen String type for the next 2 positions since they don't have a constant format
    @CsvBindByPosition(position = 20)
    private String debut;
    @CsvBindByPosition(position = 21)
    private String finalGame;
    @CsvBindByPosition(position = 22)
    private String retroID;
    @CsvBindByPosition(position = 23)
    private String bbrefID;
}
