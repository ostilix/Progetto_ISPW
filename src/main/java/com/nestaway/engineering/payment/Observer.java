package com.nestaway.engineering.payment;

public abstract class Observer {
    //chiamato da Subject quando c'è un cambiamento di stato
    protected abstract void update();
}
