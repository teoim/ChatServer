package org.mtr.web.api.controller;

import org.mtr.logger.MessageLogger;
import org.mtr.web.api.component.UserSession;
import org.mtr.web.api.repository.dao.TextMessageDAO;
import org.mtr.web.api.controller.dto.TextMessageDTO;
import org.mtr.web.api.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;


import java.security.Principal;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    ChatService chatService;

    @MessageMapping("/generalChat")
    @SendTo("/topic/generalChat")
//    public String sendTextToGeneralChat(@Payload TextMessageDTO message, Principal principal){
    public TextMessageDTO sendTextToGeneralChat(@Payload TextMessageDTO message, Principal principal){
        MessageLogger.log("ChatController - sendTextToGeneralChat(String,Principal) - @MessageMapping(\"/sendText\")");

        message.setFrom(principal.getName());
        message.setTo("generalChat");

        chatService.processGeneralMessage(message);

        return message;
    }

    @MessageMapping("/sendPrivateText")
    public String sendPrivateTextToUser(@Payload TextMessageDTO message, Principal principal, @Header("simpSessionId") String sessionId){
        MessageLogger.log("ChatController - sendPrivateTextToUser(...) - @MessageMapping(\"/sendPrivateText\")");

        message.setFrom( principal.getName());

        chatService.processPrivateMessage(message);

        simpMessagingTemplate.convertAndSendToUser( message.getTo(), "/queue/sendPrivateText", message);

        return "";
    }
}
