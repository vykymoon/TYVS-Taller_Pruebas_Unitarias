package edu.unisabana.tyvs.domain.service;

import edu.unisabana.tyvs.domain.model.Gender;
import edu.unisabana.tyvs.domain.model.Person;
import edu.unisabana.tyvs.domain.model.RegisterResult;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Pruebas por EJEMPLO del dominio: cada prueba fija una entrada concreta y su
 * resultado esperado. Estado al terminar la ITERACION 2 del README.
 *
 * Complemento: RegistryPropertiesTest expresa las mismas reglas como
 * PROPIEDADES sobre rangos completos de entradas, en vez de ejemplos sueltos.
 */
class RegistryTest {

    private Registry registry;

    /**
     * Un Registry NUEVO antes de cada prueba.
     *
     * Importante: cuando implemente DUPLICATED, el Registry guardara estado
     * (los ids ya registrados). Si compartiera la misma instancia entre
     * pruebas, una prueba podria "ensuciar" a la siguiente y los resultados
     * dependerian del orden de ejecucion. Cada prueba debe ser independiente.
     */
    @BeforeEach
    void setUp() {
        registry = new Registry();
    }

    @Test
    @DisplayName("Una persona viva y mayor de edad queda registrada")
    void shouldRegisterValidPerson() {
        // Arrange: preparar los datos
        Person person = new Person("Ana", 1, 30, Gender.FEMALE, true);

        // Act: ejecutar la accion que queremos probar
        RegisterResult result = registry.registerVoter(person);

        // Assert: verificar el resultado esperado
        assertEquals(RegisterResult.VALID, result);
    }

    @Test
    @DisplayName("Una persona no viva se rechaza con DEAD")
    void shouldRejectDeadPerson() {
        // Arrange: preparar los datos
        Person dead = new Person("Carlos", 2, 40, Gender.MALE, false);

        // Act: ejecutar la accion que queremos probar
        RegisterResult result = registry.registerVoter(dead);

        // Assert: verificar el resultado esperado
        assertEquals(RegisterResult.DEAD, result);
    }

    @Test
    @DisplayName("Una persona nula se rechaza con INVALID")
    void shouldReturnInvalidWhenPersonIsNull() {
        // Act
        RegisterResult result = registry.registerVoter(null);

        // Assert
        assertEquals(RegisterResult.INVALID, result);
    }
    @Test
    public void shouldRejectWhenIdIsZeroOrNegative() {
        Person invalido = new Person("Luis", 0, 30, Gender.MALE, true);
        assertEquals(RegisterResult.INVALID, registry.registerVoter(invalido));
    }

    @Test
    public void shouldRejectUnderageAt17() {
        Person menor = new Person("Sofia", 10, 17, Gender.FEMALE, true);
        assertEquals(RegisterResult.UNDERAGE, registry.registerVoter(menor));
    }

    @Test
    public void shouldAcceptAdultAt18() {
        Person adulto = new Person("Sofia", 11, 18, Gender.FEMALE, true);
        assertEquals(RegisterResult.VALID, registry.registerVoter(adulto));
    }

    @Test
    public void shouldAcceptMaxAge120() {
        Person anciano = new Person("Pedro", 12, 120, Gender.MALE, true);
        assertEquals(RegisterResult.VALID, registry.registerVoter(anciano));
    }

    @Test
    public void shouldRejectInvalidAgeOver120() {
        Person imposible = new Person("Ana", 13, 121, Gender.FEMALE, true);
        assertEquals(RegisterResult.INVALID_AGE, registry.registerVoter(imposible));
    }

    @Test
    public void shouldReturnUnderageWhenAgeIsZero() {
        Person recienNacido = new Person("Bebe", 1001, 0, Gender.MALE, true);
        assertEquals(RegisterResult.UNDERAGE, registry.registerVoter(recienNacido));
    }

    @Test
    public void shouldRejectDuplicatedId() {
        Person primero = new Person("Carlos", 14, 30, Gender.MALE, true);
        Person duplicado = new Person("Carla", 14, 25, Gender.FEMALE, true);
        registry.registerVoter(primero);
        assertEquals(RegisterResult.DUPLICATED, registry.registerVoter(duplicado));
    }
}