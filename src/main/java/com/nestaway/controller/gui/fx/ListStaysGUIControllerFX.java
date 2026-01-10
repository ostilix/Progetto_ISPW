package com.nestaway.controller.gui.fx;

import com.nestaway.bean.StayBean;
import com.nestaway.controller.app.BookStayController;
import com.nestaway.exception.NotFoundException;
import com.nestaway.exception.OperationFailedException;
import com.nestaway.utils.SessionManager;
import com.nestaway.utils.view.fx.FilesFXML;
import com.nestaway.utils.view.fx.PageManagerSingleton;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

import java.util.List;

public class ListStaysGUIControllerFX extends  AbstractGUIControllerFX {

    @FXML
    Pagination numberPage;

    @FXML
    VBox stayCard1;

    @FXML
    VBox stayCard2;

    @FXML
    VBox stayCard3;

    @FXML
    VBox stayCard4;

    @FXML
    VBox stayCard5;

    @FXML
    VBox stayCard6;

    @FXML
    ImageView stayImage1;

    @FXML
    ImageView stayImage2;

    @FXML
    ImageView stayImage3;

    @FXML
    ImageView stayImage4;

    @FXML
    ImageView stayImage5;

    @FXML
    ImageView stayImage6;

    ImageView[] stayImages;

    VBox[] stayCards;

    //lista degli alloggi recuperati
    List<StayBean> stays;

    @FXML
    public void selectStay(ActionEvent stay) {
        //recupero il bottone cliccato
        Button button = (Button) stay.getSource();
        //recupero il nome dell'alloggio dal bottone cliccato
        String nameStay = button.getText();
        //cerco l'alloggio
        StayBean stayBean = searchStay(nameStay);
        //imposto l'alloggio selezionato nella sessione corrente
        SessionManager.getSessionManager().getSessionFromId(currentSession).setStay(stayBean);
        //vado alla pagine dei dettagli
        goNext(FilesFXML.STAY.getPath());
    }

    @FXML
    public void goBack() {
        resetMsg(errorMsg);
        try {
            SessionManager.getSessionManager().getSessionFromId(currentSession).setStay(null);
            PageManagerSingleton.getInstance().goBack(currentSession);
        } catch (OperationFailedException | NotFoundException e) {
            setMsg(errorMsg, e.getMessage());
        }
    }

    public void initialize(Integer session) throws OperationFailedException, NotFoundException {
        this.currentSession = session;
        stayCards = new VBox[]{stayCard1, stayCard2, stayCard3, stayCard4, stayCard5, stayCard6};
        stayImages = new ImageView[]{stayImage1, stayImage2, stayImage3, stayImage4, stayImage5, stayImage6};
        int maxCards = 6;

        //chiamo il controller per cercare gli alloggi
        BookStayController bookStayController = new BookStayController();
        stays = bookStayController.findStays(SessionManager.getSessionManager().getSessionFromId(session).getCity(), SessionManager.getSessionManager().getSessionFromId(session).getCheckIn(), SessionManager.getSessionManager().getSessionFromId(session).getCheckOut(), SessionManager.getSessionManager().getSessionFromId(session).getNumGuests());

        int numPages = (stays.size() / maxCards);
        if(stays.size() % maxCards != 0){
            numPages++;
        }
        numberPage.setPageCount(numPages);
        numberPage.setMaxPageIndicatorCount(Math.min(numPages, 10));
        //tramite un listener quando cambio pagina chiamo createPage
        numberPage.currentPageIndexProperty().addListener(((obs, oldIndex, newIndex) -> createPage(newIndex.intValue(), maxCards)));
        //carico la prima pagina
        createPage(0, maxCards);
    }

    private void createPage(Integer pageIndex, Integer maxStays) {
        resetMsg(errorMsg);

        //nascondo le card prima di riempirle
        for (int i = 0; i < stayCards.length; i++) {
            stayCards[i].setVisible(false);
            stayImages[i].setImage(null);
            stayImages[i].setClip(null);
        }

        //calcolo quanti alloggi mostrare nella pagina
        int max = Math.min(maxStays, stays.size() - pageIndex * maxStays);

        for (int i = 0; i < max; i++) {
            //recupero il Bean corretto
            StayBean stay = stays.get(pageIndex * maxStays + i);
            //rendo visibile la card
            stayCards[i].setVisible(true);
            //carico i dettagli di quell'alloggio
            ObservableList<Node> elements = stayCards[i].getChildren();
            ((Button) elements.get(0)).setText(stay.getName());
            ((Label) elements.get(1)).setText("Price: " + stay.getPricePerNight() + "€/night");
            ((Label) elements.get(2)).setText("Host: " + stay.getHostUsername());

            int imageIndex = (pageIndex * maxStays + i) % 6 + 1;
            String imagePath = getClass().getResource("/images/stay" + imageIndex + ".png").toExternalForm();

            Image image = new Image(imagePath, 400, 0, true, true, true);
            stayImages[i].setImage(image);
            applyRoundedCorners(stayImages[i], 25, 25);
        }
    }


    private StayBean searchStay(String stayName){
        for(StayBean stay : stays){
            if(stay.getName().equals(stayName)){
                return stay;
            }
        }
        return null;
    }

    private void applyRoundedCorners(ImageView imageView, double arcWidth, double arcHeight) {
        Rectangle clip = new Rectangle();

        clip.widthProperty().bind(imageView.fitWidthProperty());
        clip.heightProperty().bind(imageView.fitHeightProperty());

        clip.setArcWidth(arcWidth);
        clip.setArcHeight(arcHeight);

        imageView.setClip(clip);
    }
}
