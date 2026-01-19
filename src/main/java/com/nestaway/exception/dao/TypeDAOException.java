package com.nestaway.exception.dao;

public enum TypeDAOException {
    GENERIC(1),
    DUPLICATE(2),
    NOT_AVAILABLE(3);

    private final int id;

    TypeDAOException(int id) {
        this.id = id;
    }

    public int getId(){
        return id;
    }
}