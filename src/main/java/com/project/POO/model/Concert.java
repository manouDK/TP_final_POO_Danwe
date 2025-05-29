package com.project.POO.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Concert extends Evenement {

    private String artiste;
    private String genreMusical;

    public Concert(String nom, LocalDateTime date, String lieu, int capaciteMax, String artiste, String genreMusical) {
        super(nom, date, lieu, capaciteMax);
        this.artiste = artiste;
        this.genreMusical = genreMusical;
    }


    @Override
    public String afficherDetails() {
        StringBuilder details = new StringBuilder();
        details.append("Concert: ").append(getNom())
                .append("\nDate: ").append(getDate())
                .append("\nLieu: ").append(getLieu())
                .append("\nArtiste: ").append(artiste)
                .append("\nGenre musical: ").append(genreMusical)
                .append("\nCapacité maximale: ").append(getCapaciteMax())
                .append("\nParticipants inscrits: ").append(getParticipants().size());

        return details.toString();
    }
}
