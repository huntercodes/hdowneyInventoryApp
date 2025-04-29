package com.hunterdowney.hdowneyinventoryapp.messaging;

import com.hunterdowney.hdowneyinventoryapp.domain.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class InventoryProducer {
    private static final Logger log = LoggerFactory.getLogger(InventoryProducer.class);

    private final KafkaTemplate<String, Item> kafkaTemplate;
    private final String topic;

    public InventoryProducer(KafkaTemplate<String, Item> kafkaTemplate,
                             @Value("${inventory.topic.low}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void sendLowInventory(Item item) {
        log.info("→ Producing low-inventory event for item {} (qty={})", item.getId(), item.getInventory());
        kafkaTemplate.send(topic, item.getId(), item);
    }
}
