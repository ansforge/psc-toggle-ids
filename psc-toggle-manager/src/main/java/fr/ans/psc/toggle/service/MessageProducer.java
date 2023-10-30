/**
 * Copyright (C) ${project.inceptionYear} Agence du Numérique en Santé (ANS) (https://esante.gouv.fr)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

    public void sendPsMessage(Ps ps, String operation) {
        log.debug("Sending message for Ps {}", ps.getNationalId());
        String routingKey = operation.equalsIgnoreCase("UPDATE") ?
                PS_UPDATE_MESSAGES_QUEUE_ROUTING_KEY :
                PS_DELETE_MESSAGES_QUEUE_ROUTING_KEY ;

        Gson json = new Gson();
        try {
            rabbitTemplate.convertAndSend(EXCHANGE_MESSAGES, routingKey, json.toJson(ps));
        } catch (AmqpException e) {
            log.error("Error occurred when sending Ps {} informations to queue manager", ps.getNationalId());
            e.printStackTrace();
        }
    }
}
