package edu.unisabana.tyvs.domain.model;

public enum RegisterResult {
    VALID,        // cumple todas las reglas
    DUPLICATED,   // id ya registrado
    INVALID,      // persona nula o id <= 0
    DEAD,         // no está viva
    UNDERAGE,     // 0 <= edad < 18
    INVALID_AGE   // edad < 0 o edad > 120
}