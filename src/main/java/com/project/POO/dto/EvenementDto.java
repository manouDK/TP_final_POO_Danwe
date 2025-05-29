package com.project.POO.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@ToString
public class EvenementDto {
    private String id;
    private String nom;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date;

    private String lieu;
    private int capaciteMax;
    private boolean annule;

    private String type;

    private String theme;
    private List<String> intervenants = new ArrayList<>();
    private String artiste;
    private String genreMusical;
    private int nombreParticipants;
}