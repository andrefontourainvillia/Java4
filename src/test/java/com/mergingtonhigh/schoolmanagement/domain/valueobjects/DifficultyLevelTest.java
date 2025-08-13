package com.mergingtonhigh.schoolmanagement.domain.valueobjects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class DifficultyLevelTest {

    @Test
    void shouldHaveCorrectDisplayNames() {
        assertEquals("Iniciante", DifficultyLevel.INICIANTE.getDisplayName());
        assertEquals("Intermediário", DifficultyLevel.INTERMEDIARIO.getDisplayName());
        assertEquals("Avançado", DifficultyLevel.AVANCADO.getDisplayName());
    }

    @Test
    void shouldConvertFromStringByName() {
        assertEquals(DifficultyLevel.INICIANTE, DifficultyLevel.fromString("INICIANTE"));
        assertEquals(DifficultyLevel.INTERMEDIARIO, DifficultyLevel.fromString("INTERMEDIARIO"));
        assertEquals(DifficultyLevel.AVANCADO, DifficultyLevel.fromString("AVANCADO"));
    }

    @Test
    void shouldConvertFromStringByDisplayName() {
        assertEquals(DifficultyLevel.INICIANTE, DifficultyLevel.fromString("Iniciante"));
        assertEquals(DifficultyLevel.INTERMEDIARIO, DifficultyLevel.fromString("Intermediário"));
        assertEquals(DifficultyLevel.AVANCADO, DifficultyLevel.fromString("Avançado"));
    }

    @Test
    void shouldBeIgnoreCase() {
        assertEquals(DifficultyLevel.INICIANTE, DifficultyLevel.fromString("iniciante"));
        assertEquals(DifficultyLevel.INTERMEDIARIO, DifficultyLevel.fromString("intermediário"));
        assertEquals(DifficultyLevel.AVANCADO, DifficultyLevel.fromString("avançado"));
    }

    @Test
    void shouldHandleTrimmableStrings() {
        assertEquals(DifficultyLevel.INICIANTE, DifficultyLevel.fromString("  Iniciante  "));
        assertEquals(DifficultyLevel.INTERMEDIARIO, DifficultyLevel.fromString(" Intermediário "));
    }

    @Test
    void shouldReturnNullForInvalidValues() {
        assertNull(DifficultyLevel.fromString("Invalid"));
        assertNull(DifficultyLevel.fromString(""));
        assertNull(DifficultyLevel.fromString("   "));
        assertNull(DifficultyLevel.fromString(null));
    }

    @Test
    void shouldUseDisplayNameInToString() {
        assertEquals("Iniciante", DifficultyLevel.INICIANTE.toString());
        assertEquals("Intermediário", DifficultyLevel.INTERMEDIARIO.toString());
        assertEquals("Avançado", DifficultyLevel.AVANCADO.toString());
    }
}