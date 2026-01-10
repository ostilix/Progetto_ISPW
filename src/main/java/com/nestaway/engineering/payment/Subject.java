package com.nestaway.engineering.payment;

import java.util.ArrayList;
import java.util.List;

public abstract class Subject {

    //lista protetta, contiene gli oggetti che voglione essere notificati
    protected final List<Observer> observers = new ArrayList<>();

    public void registerObserver(Observer observer){
        observers.add(observer);
    }

    public synchronized void notifyObservers(){
        for (Observer observer : observers) {
            observer.update();
        }
    }

}
