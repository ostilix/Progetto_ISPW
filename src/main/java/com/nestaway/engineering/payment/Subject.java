package com.nestaway.engineering.payment;

import java.util.ArrayList;
import java.util.List;
//gestisco la lista di osservatori e li notifico
public abstract class Subject {

    //lista protetta, contiene gli oggetti che voglione essere notificati
    protected final List<Observer> observers = new ArrayList<>();

    public void registerObserver(Observer observer){
        observers.add(observer);
    }

    //chiamata quando lo stato del Subject cambia
    public synchronized void notifyObservers(){
        for (Observer observer : observers) {
            observer.update();
        }
    }

}
