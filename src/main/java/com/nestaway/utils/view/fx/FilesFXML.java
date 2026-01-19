package com.nestaway.utils.view.fx;

public enum FilesFXML {
    //mappo i nomi logici delle viste ai percorsi fisici dei file FXML
    HOME("/HomeView.fxml"),

    LIST_STAYS("/ListStaysView.fxml"),

    STAY("/StayDetailsView.fxml"),

    BOOKING("/BookingView.fxml"),

    LOGIN("/LoginAndRegisterView.fxml"),

    NOTIFICATION("/NotificationsView.fxml"),

    HOST_HOME("/HostHomeView.fxml"),;

    private final String path; //memorizzo il percorso

    FilesFXML(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

}