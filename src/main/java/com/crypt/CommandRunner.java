package com.crypt;

import com.crypt.controller.CryptController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class CommandRunner {

    @Autowired
    private CryptController cryptController;

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {
        while (true) {
            cryptController.getActualPriceCrypt();
            try {
                TimeUnit.MINUTES.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}