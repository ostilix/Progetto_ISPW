package com.nestaway.utils.view.fx;

import com.nestaway.controller.gui.fx.AbstractGUIControllerFX;
import com.nestaway.exception.NotFoundException;
import com.nestaway.exception.OperationFailedException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Level;
import java.util.logging.Logger;

//un solo page manager, gestsisce l'unica finestra aperta dell'applicazione
public class PageManagerSingleton {

    private final Stage primaryStage;
    //stack per la cronologia di navigazione(Last In First Out)
    private static final Deque<String> viewStack = new ArrayDeque<>();
    private static PageManagerSingleton instance = null;

    private PageManagerSingleton(Stage stage) {
        primaryStage = stage;
    }

    public static synchronized PageManagerSingleton getInstance(Stage ... stage){
        if(instance == null){
            instance = new PageManagerSingleton(stage[0]);
        }
        return instance;
    }
    //navigo verso la nuova vista e la aggiungo in cima allo stack
    public void goNext(String fxmlPath, Integer session)  throws OperationFailedException, NotFoundException {
        //aggiungo alla cronologia
        viewStack.push(fxmlPath);
        changeView(fxmlPath, session);
    }

    public void goBack(Integer session)  throws OperationFailedException, NotFoundException{
        if (!viewStack.isEmpty()) {
            //rimuovo la vista attuale
            viewStack.pop();
            //vedo qual è la vista precedente
            String previousView = viewStack.peek();
            //carico la vista precedente
            changeView(previousView, session);
        }
    }

    public void goHome(Integer session)  throws OperationFailedException, NotFoundException{
        if(!viewStack.isEmpty()){
            //prendo l'ultima vista dello stack (Home)
            String home = viewStack.reversed().pop();
            setHome(home, session);
        }
    }

    public void setHome(String fxmlPath, Integer session)  throws OperationFailedException, NotFoundException{
        //salvo stack vecchio per eventuale rollback
        Deque<String> oldStack = new ArrayDeque<>(viewStack);
        try {
            //pulisco lo stack
            viewStack.clear();
            //inserisco la nuova home
            viewStack.push(fxmlPath);
            //carico la vista
            changeView(fxmlPath, session);
        } catch (OperationFailedException | NotFoundException e){
            //rollback in caso di fallimento
            viewStack.clear();
            viewStack.addAll(oldStack);
            throw e;
        }
    }

    public void changeView(String fxmlPath, Integer session) throws OperationFailedException, NotFoundException {
        try {
            //preparo il loader
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            //recupero il controller JavaFX associato al file FXML
            AbstractGUIControllerFX controller = loader.getController();
            //inizializzo il controller passandogli la sessione corrente
            controller.initialize(session);

            //creo la nuova scena
            Scene scene = new Scene(root);
            //imposto la scena sullo stage principale
            primaryStage.setScene(scene);
            primaryStage.setTitle("NestAway");

            //imposto dimensioni massime disponibili
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            primaryStage.setX(screenBounds.getMinX());
            primaryStage.setY(screenBounds.getMinY());
            primaryStage.setWidth(screenBounds.getWidth());
            primaryStage.setHeight(screenBounds.getHeight());
            //mostro la finestra
            primaryStage.show();
        } catch (IOException | IllegalStateException | NullPointerException e) {
            viewStack.pop();
            String errorMsg = "Impossible to load the view: " + fxmlPath;
            Logger.getGlobal().log(Level.SEVERE, errorMsg, e);
            throw new OperationFailedException();
        } catch (OperationFailedException | NotFoundException e) {
            viewStack.pop();
            throw e;
        }
    }


}
