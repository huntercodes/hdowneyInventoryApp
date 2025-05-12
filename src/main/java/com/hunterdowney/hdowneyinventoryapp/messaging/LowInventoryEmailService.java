package com.hunterdowney.hdowneyinventoryapp.messaging;

import com.hunterdowney.hdowneyinventoryapp.domain.Item;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class LowInventoryEmailService {

    private final JavaMailSender mailSender;
    private final String managerAddress;

    public LowInventoryEmailService(JavaMailSender mailSender,
                                    @Value("${spring.mail.properties.from}") String from) {
        this.mailSender     = mailSender;
        this.managerAddress = "managers@myinventoryapp.com";
    }

    public void sendLowInventoryAlert(Item item) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("no-reply@myinventoryapp.com");
        msg.setTo(managerAddress);
        msg.setSubject("🔔 Low Inventory Alert: “" + item.getName() + "”");
        msg.setText(String.format(
                "Item ID: %s%nName: %s%nCurrent Inventory: %d%nThreshold: %d%n%n" +
                        "Please reorder this product as soon as possible.",
                item.getId(),
                item.getName(),
                item.getInventory(),
                10
        ));
        mailSender.send(msg);
    }
}

