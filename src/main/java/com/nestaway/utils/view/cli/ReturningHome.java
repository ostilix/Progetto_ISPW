package com.nestaway.utils.view.cli;

public class ReturningHome {
    private Boolean returnHome;
    public ReturningHome() {
        this.returnHome = false; //se true i controller passano il controllo ai controller precedenti
    }
    public void setReturningHome(Boolean returningHome) {
        this.returnHome = returningHome;
    }
    public Boolean getReturningHome() {
        return returnHome;
    }
}
