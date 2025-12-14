package com.nestaway.controller.gui.fx;

import com.nestaway.bean.ReviewBean;
import com.nestaway.bean.StayBean;
import com.nestaway.controller.app.ReviewController;
import com.nestaway.exception.NotFoundException;
import com.nestaway.exception.OperationFailedException;
import com.nestaway.utils.SessionManager;
import com.nestaway.utils.view.fx.FilesFXML;
import com.nestaway.utils.view.fx.PageManagerSingleton;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;

import java.util.List;

public class StayDetailsGUIControllerFX extends AbstractGUIControllerFX {

    @FXML
    Pagination numberPage;

    @FXML
    Label title;

    @FXML
    Label city;

    @FXML
    Label address;

    @FXML
    Label guests;

    @FXML
    Label rooms;

    @FXML
    Label bathrooms;

    @FXML
    Label price;

    @FXML
    Label description;

    @FXML
    Label username;

    @FXML
    Button back;

    @FXML
    Button book;

    @FXML
    Button management;

    @FXML
    VBox reviewCard1;

    @FXML
    VBox reviewCard2;

    @FXML
    VBox reviewCard3;

    VBox[] reviewsCards;

    List<ReviewBean> reviews;

    StayBean stay;

    @FXML
    ImageView stayImage;


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

    @FXML
    public void bookStay() {
        goNext(FilesFXML.BOOKING.getPath());
    }

    @FXML
    public void bookManagement() {
        setMsg(errorMsg, "Not implemented yet.");
    }

    public void initialize(Integer session) throws OperationFailedException {
        this.currentSession = session;
        resetMsg(errorMsg);

        stay = SessionManager.getSessionManager().getSessionFromId(currentSession).getStay();

        title.setText(stay.getName());
        city.setText(stay.getCity());
        address.setText(stay.getAddress());
        guests.setText(String.valueOf(stay.getMaxGuests()));
        rooms.setText(String.valueOf(stay.getNumRooms()));
        bathrooms.setText(String.valueOf(stay.getNumBathrooms()));
        price.setText(String.valueOf(stay.getPricePerNight()));
        description.setText(stay.getDescription());
        username.setText(stay.getHostUsername());

        String imagePath = getClass().getResource("/images/stay6.png").toExternalForm();
        stayImage.setImage(new Image(imagePath, 800, 0, true, true, true));
        applyRoundedCorners(stayImage, 30, 30);

        reviewsCards = new VBox[]{reviewCard1, reviewCard2, reviewCard3};
        int maxCards = 3;

        ReviewController reviewController = new ReviewController();
        reviews = reviewController.getReviewsByStay(stay.getIdStay());

        int numPages = (reviews.size() / maxCards);
        if (reviews.size() % maxCards != 0) {
            numPages++;
        }
        numberPage.setPageCount(numPages);
        numberPage.setMaxPageIndicatorCount(Math.min(numPages / 2, 10));
        numberPage.currentPageIndexProperty().addListener(((obs, oldIndex, newIndex) -> createPage(newIndex.intValue(), maxCards)));
        createPage(0, maxCards);
    }

    private void createPage(Integer pageIndex, Integer maxReviews) {
        resetMsg(errorMsg);

        reviewCard1.setVisible(false);
        reviewCard2.setVisible(false);
        reviewCard3.setVisible(false);

        int max = Math.min(maxReviews, reviews.size() - pageIndex * maxReviews);

        for (int i = 0; i < max; i++) {
            reviewsCards[i].setVisible(true);
            ObservableList<Node> elements = reviewsCards[i].getChildren();
            ReviewBean review = reviews.get(pageIndex * maxReviews + i);

            Label ratingLabel;
            Node ratingNode = elements.get(0);
            if (ratingNode instanceof HBox outer) {
                Node inner = outer.getChildren().getFirst();
                if (inner instanceof HBox innerBox) {
                    ratingLabel = (Label) innerBox.getChildren().getFirst();
                } else {
                    ratingLabel = (Label) inner;
                }
            } else {
                ratingLabel = (Label) ratingNode;
            }

            Label commentLabel = (Label) ((HBox) elements.get(1)).getChildren().getFirst();
            Label dateLabel = (Label) ((HBox) elements.get(2)).getChildren().getFirst();

            ratingLabel.setText("Rating: " + review.getRating() + "/5");
            commentLabel.setText("Comment: " + review.getComment());
            dateLabel.setText("Date: " + review.getDate().toString());
        }
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
