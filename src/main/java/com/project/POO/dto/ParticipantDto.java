package com.project.POO.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ParticipantDto {
    private String id;
    private String nom;
    private String email;
    private List<String> evenementsInscrits;
    private boolean organisateur;
    private List<String> evenementsOrganises;
}