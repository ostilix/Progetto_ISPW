package com.nestaway.engineering.payment;

import java.security.SecureRandom;
import java.util.Random;

//è il Concrete Subject
public class PayPalAPI extends PaymentAPI {

    public void processPayment(PaymentRequest request) {

        Random random = new SecureRandom();

        //simulo successo o fallimento
        boolean paymentSuccessful = random.nextBoolean();
        //controllo sull'importo
        if (request.getAmount() <= 0.0) {
            paymentResponse = false;
        } else {
            paymentResponse = paymentSuccessful;
        }

        //notifico il controller (observer)
        notifyObservers();
    }

}
