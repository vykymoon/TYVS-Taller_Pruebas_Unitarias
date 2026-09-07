package edu.unisabana.tyvs.domain.service;

import edu.unisabana.tyvs.domain.model.Gender;
import edu.unisabana.tyvs.domain.model.Person;
import edu.unisabana.tyvs.domain.model.RegisterResult;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.constraints.IntRange;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * PRUEBAS BASADAS EN PROPIEDADES (jqwik).
 *
 * Una prueba por ejemplo dice "edad 40 y no viva -> DEAD".
 * Una propiedad dice "para TODA edad y TODO genero, si no esta viva -> DEAD",
 * y jqwik genera cientos de combinaciones para intentar refutarla.
 *
 * Es la continuacion natural de las clases de equivalencia: usted eligio un
 * representante por clase a mano; aqui la maquina explora la clase entera.
 *
 * Cuando una propiedad falla, jqwik no reporta la entrada aleatoria que la
 * rompio, sino la MAS SIMPLE que la rompe (shrinking). Si "edad 73 con nombre
 * 'xkqz'" falla, le reportara "edad 0 con nombre ''", que es mucho mas facil
 * de diagnosticar.
 *
 * OJO: estas propiedades corresponden a las reglas YA implementadas
 * (iteraciones 1 y 2). Las de edad y duplicados son parte de su ejercicio; el
 * README explica como escribirlas.
 */
class RegistryPropertiesTest {

    /** Genera cualquier valor del enum Gender, incluido UNIDENTIFIED. */
    @Provide
    Arbitrary<Gender> generos() {
        return Arbitraries.of(Gender.values());
    }

    /** Nombres arbitrarios, incluida la cadena vacia. */
    @Provide
    Arbitrary<String> nombres() {
        return Arbitraries.strings().alpha().ofMaxLength(20);
    }

    /**
     * Regla R3: una persona no viva se rechaza SIEMPRE, sin importar su edad,
     * su documento ni su genero.
     *
     * Esta unica propiedad cubre mas terreno que cualquier tabla de ejemplos:
     * afirma algo sobre el espacio completo de entradas, no sobre cinco filas.
     */
    @Property
    void unaPersonaNoVivaSiempreEsRechazada(
            @ForAll("nombres") String nombre,
            @ForAll @IntRange(min = 1, max = 100_000) int id,
            @ForAll @IntRange(min = 0, max = 120) int edad,
            @ForAll("generos") Gender genero) {

        Person muerta = new Person(nombre, id, edad, genero, false);

        assertEquals(RegisterResult.DEAD, new Registry().registerVoter(muerta));
    }

    /**
     * Propiedad de DETERMINISMO: registrar la misma persona en dos Registry
     * recien creados produce el mismo resultado.
     *
     * Parece obvia, pero es justo la que se rompe cuando alguien introduce
     * estado compartido (por ejemplo, un Set estatico de ids en vez de uno de
     * instancia). Es un buen ejemplo de propiedad que atrapa errores de diseno
     * que ninguna prueba por ejemplo buscaria.
     */
    @Property
    void elResultadoNoDependeDeLaInstancia(
            @ForAll("nombres") String nombre,
            @ForAll @IntRange(min = 1, max = 100_000) int id,
            @ForAll @IntRange(min = 0, max = 120) int edad,
            @ForAll("generos") Gender genero,
            @ForAll boolean viva) {

        Person p = new Person(nombre, id, edad, genero, viva);

        RegisterResult primera = new Registry().registerVoter(p);
        RegisterResult segunda = new Registry().registerVoter(p);

        assertEquals(primera, segunda);
    }

    /**
     * Propiedad de TOTALIDAD: registerVoter nunca devuelve null ni lanza una
     * excepcion, sea cual sea la entrada.
     *
     * Un contrato debil, pero sorprendentemente util: detecta desbordamientos,
     * divisiones por cero y NullPointerException que aparecen solo en los
     * bordes del dominio.
     */
    @Property
    void nuncaDevuelveNullNiLanzaExcepcion(
            @ForAll("nombres") String nombre,
            @ForAll int id,
            @ForAll int edad,
            @ForAll("generos") Gender genero,
            @ForAll boolean viva) {

        Person p = new Person(nombre, id, edad, genero, viva);

        RegisterResult resultado = new Registry().registerVoter(p);

        org.junit.jupiter.api.Assertions.assertNotNull(resultado);
    }

        // R5: menor de edad -> UNDERAGE
    @Property
    void todoMenorDeEdadEsRechazado(
            @ForAll("nombres") String nombre,
            @ForAll @IntRange(min = 1, max = 100_000) int id,
            @ForAll @IntRange(min = 0, max = 17) int edad,
            @ForAll("generos") Gender genero) {

        Person menor = new Person(nombre, id, edad, genero, true);

        assertEquals(RegisterResult.UNDERAGE, new Registry().registerVoter(menor));
    }

    // R7: adulto valido no registrado antes -> VALID
    @Property
    void todoAdultoValidoSeRegistra(
            @ForAll("nombres") String nombre,
            @ForAll @IntRange(min = 1, max = 100_000) int id,
            @ForAll @IntRange(min = 18, max = 120) int edad,
            @ForAll("generos") Gender genero) {

        Person adulto = new Person(nombre, id, edad, genero, true);

        assertEquals(RegisterResult.VALID, new Registry().registerVoter(adulto));
    }

    // R2: id <= 0 -> INVALID
    @Property
    void unIdNoPositivoSiempreEsInvalido(
            @ForAll("nombres") String nombre,
            @ForAll @IntRange(min = -100_000, max = 0) int id,
            @ForAll @IntRange(min = 0, max = 120) int edad,
            @ForAll("generos") Gender genero,
            @ForAll boolean viva) {

        Person p = new Person(nombre, id, edad, genero, viva);

        assertEquals(RegisterResult.INVALID, new Registry().registerVoter(p));
    }

    // Estructural: una persona rechazada no queda registrada
    @Property
    void unaPersonaRechazadaNuncaQuedaRegistrada(
            @ForAll("nombres") String nombre,
            @ForAll @IntRange(min = 1, max = 100_000) int id,
            @ForAll @IntRange(min = 0, max = 17) int edad,
            @ForAll("generos") Gender genero) {

        Registry registry = new Registry();
        Person menor = new Person(nombre, id, edad, genero, true);

        RegisterResult primerIntento = registry.registerVoter(menor);
        RegisterResult segundoIntento = registry.registerVoter(menor);

        assertEquals(primerIntento, segundoIntento);
    }
}
