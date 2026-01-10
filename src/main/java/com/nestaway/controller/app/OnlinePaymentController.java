package com.nestaway.controller.app;

import com.nestaway.engineering.payment.Observer;
import com.nestaway.engineering.payment.PayPalAPI;
import com.nestaway.engineering.payment.PaymentAPI;
import com.nestaway.engineering.payment.PaymentRequest;
import com.nestaway.model.Host;

//osservo API di pagamento
public class OnlinePaymentController extends Observer {

    private PaymentAPI paymentAPI; //API astratta
    private Boolean response;

    //avvio il pagamento
    protected boolean payPayPal(Host host, Double amount, String reason) {
        //creo richiesta di pagamento
        PaymentRequest request = new PaymentRequest(host.getInfoPayPal(), amount, reason);
        //istanzio API concreta
        paymentAPI = new PayPalAPI();
        //registrazione come osservatore
        paymentAPI.registerObserver(this);
        //busy waiting
        long startTime = System.currentTimeMillis();
        long timeout = 60000L;

        paymentAPI.processPayment(request);
        //attendo la risposta
        while(response == null) {
            if(System.currentTimeMillis() - startTime > timeout) {
                return false;
            }
        }
        return response;
    }

    @Override
    protected void update(){
        if (paymentAPI != null){
            //leggo il risultato dall'API
            response = paymentAPI.getResponse();
        }
    }
}


