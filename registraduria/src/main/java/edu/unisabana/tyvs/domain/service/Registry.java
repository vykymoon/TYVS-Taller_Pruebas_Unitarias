package edu.unisabana.tyvs.domain.service;

import edu.unisabana.tyvs.domain.model.Person;
import edu.unisabana.tyvs.domain.model.RegisterResult;

import java.util.HashSet;
import java.util.Set;

public class Registry {

    private static final int MIN_AGE = 0;
    private static final int MAX_AGE = 120;
    private static final int LEGAL_AGE = 18;

    private final Set<Integer> registrados = new HashSet<>();

    public RegisterResult registerVoter(Person p) {
        if (p == null) {
            return RegisterResult.INVALID;
        }
        if (p.getId() <= 0) {
            return RegisterResult.INVALID;
        }
        if (!p.isAlive()) {
            return RegisterResult.DEAD;
        }
        if (p.getAge() < MIN_AGE || p.getAge() > MAX_AGE) {
            return RegisterResult.INVALID_AGE;
        }
        if (p.getAge() < LEGAL_AGE) {
            return RegisterResult.UNDERAGE;
        }
        if (registrados.contains(p.getId())) {
            return RegisterResult.DUPLICATED;
        }
        registrados.add(p.getId());
        return RegisterResult.VALID;
    }
}