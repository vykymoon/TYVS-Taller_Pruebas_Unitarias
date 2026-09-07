# Registro de Defectos

## Defecto 01

- **Caso de prueba**: Registro de una persona con edad exactamente en el límite inferior válido (0 años)
- **Entrada**: `Person("Bebe", 1001, 0, Gender.MALE, true)`
- **Resultado esperado**: `UNDERAGE` (según R5: `0 ≤ edad < 18`)
- **Resultado obtenido antes de la corrección**: sin prueba que lo verificara — el borde entre `INVALID_AGE` y `UNDERAGE` (edad = 0) no estaba cubierto
- **Causa probable**: `RegistryTest` no incluía ningún caso con `edad = 0`, el valor límite exacto entre la clase de equivalencia "inválida" (`edad < 0`) y la clase "menor" (`0 ≤ edad < 18`). Detectado mediante mutation testing con PIT: el mutante `ConditionalsBoundaryMutator`, que cambió `p.getAge() < MIN_AGE` por `p.getAge() <= MIN_AGE` en `Registry.java`, sobrevivió porque ninguna prueba distinguía ambos comportamientos.
- **Estado**: Resuelto — se agregó la prueba `shouldReturnUnderageWhenAgeIsZero()` en `RegistryTest.java`, que verifica que `edad = 0` devuelve `UNDERAGE`. Tras agregarla, el mutante pasó de `SURVIVED` a `KILLED` (mutation score del mutador subió de 75% a 100%; score global de 88% a 92%).