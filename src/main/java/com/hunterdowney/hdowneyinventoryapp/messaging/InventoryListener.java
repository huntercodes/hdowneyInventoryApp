package com.hunterdowney.hdowneyinventoryapp.messaging;

import com.hunterdowney.hdowneyinventoryapp.domain.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryListener {
    private static final Logger log = LoggerFactory.getLogger(InventoryListener.class);

    private final LowInventoryEmailService emailService;

    public InventoryListener(LowInventoryEmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "${inventory.topic.low}", groupId = "inventory-group")
    public void onLowInventory(Item item) {
        log.warn("← Received LOW-INV alert for item {} (qty={})", item.getId(), item.getInventory());
        emailService.sendLowInventoryAlert(item);
        log.info("✉️  Sent low-inventory email for item {}", item.getId());
    }
}
