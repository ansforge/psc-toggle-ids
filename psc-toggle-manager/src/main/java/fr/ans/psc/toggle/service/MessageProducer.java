package fr.ans.psc.toggle.service;

import com.google.gson.Gson;
import fr.ans.psc.model.Ps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import static fr.ans.psc.rabbitmq.conf.PscRabbitMqConfiguration.*;
import static fr.ans.psc.rabbitmq.conf.PscRabbitMqConfiguration.EXCHANGE_MESSAGES;

@Slf4j
@Component
public class MessageProducer {

    private final RabbitTemplate rabbitTemplate;

    public MessageProducer(final RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendPsMessage(Ps ps) {
        log.debug("Sending message for Ps {}", ps.getNationalId());

        Gson json = new Gson();
        try {
            rabbitTemplate.convertAndSend(EXCHANGE_MESSAGES, PS_UPDATE_MESSAGES_QUEUE_ROUTING_KEY, json.toJson(ps));
        } catch (AmqpException e) {
            log.error("Error occurred when sending Ps {} informations to queue manager", ps.getNationalId());
            e.printStackTrace();
        }
    }
}
