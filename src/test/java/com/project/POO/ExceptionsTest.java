package com.project.POO;

import com.project.POO.exception.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExceptionsTest {

    @Test
    @DisplayName("CapaciteMaxAtteinteException avec message")
    void capaciteMaxAtteinteException_WithMessage() {
        // Arrange
        String errorMessage = "La capacité maximale de l'événement est atteinte";

        // Act
        CapaciteMaxAtteinteException exception = new CapaciteMaxAtteinteException(errorMessage);

        // Assert
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("CapaciteMaxAtteinteException avec message et cause")
    void capaciteMaxAtteinteException_WithMessageAndCause() {
        // Arrange
        String errorMessage = "La capacité maximale de l'événement est atteinte";
        Throwable cause = new RuntimeException("Cause originale");

        // Act
        CapaciteMaxAtteinteException exception = new CapaciteMaxAtteinteException(errorMessage, cause);

        // Assert
        assertEquals(errorMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("EvenementDejaExistantException avec message")
    void evenementDejaExistantException_WithMessage() {
        // Arrange
        String errorMessage = "Un événement avec le même nom et date existe déjà";

        // Act
        EvenementDejaExistantException exception = new EvenementDejaExistantException(errorMessage);

        // Assert
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("EvenementDejaExistantException avec message et cause")
    void evenementDejaExistantException_WithMessageAndCause() {
        // Arrange
        String errorMessage = "Un événement avec le même nom et date existe déjà";
        Throwable cause = new RuntimeException("Cause originale");

        // Act
        EvenementDejaExistantException exception = new EvenementDejaExistantException(errorMessage, cause);

        // Assert
        assertEquals(errorMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("EvenementNotFoundException avec message")
    void evenementNotFoundException_WithMessage() {
        // Arrange
        String errorMessage = "Événement non trouvé avec l'id: 123";

        // Act
        EvenementNotFoundException exception = new EvenementNotFoundException(errorMessage);

        // Assert
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("EvenementNotFoundException avec message et cause")
    void evenementNotFoundException_WithMessageAndCause() {
        // Arrange
        String errorMessage = "Événement non trouvé avec l'id: 123";
        Throwable cause = new RuntimeException("Cause originale");

        // Act
        EvenementNotFoundException exception = new EvenementNotFoundException(errorMessage, cause);

        // Assert
        assertEquals(errorMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("ParticipantNotFoundException avec message")
    void participantNotFoundException_WithMessage() {
        // Arrange
        String errorMessage = "Participant non trouvé avec l'id: 456";

        // Act
        ParticipantNotFoundException exception = new ParticipantNotFoundException(errorMessage);

        // Assert
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("ParticipantNotFoundException avec message et cause")
    void participantNotFoundException_WithMessageAndCause() {
        // Arrange
        String errorMessage = "Participant non trouvé avec l'id: 456";
        Throwable cause = new RuntimeException("Cause originale");

        // Act
        ParticipantNotFoundException exception = new ParticipantNotFoundException(errorMessage, cause);

        // Assert
        assertEquals(errorMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}