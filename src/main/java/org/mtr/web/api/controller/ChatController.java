package org.mtr.web.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.mtr.logger.MessageLogger;
import org.mtr.web.api.component.UserSession;
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
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    ChatService chatService;


    /**
     * ==================== Messages sent endpoints ====================
     * */

    @MessageMapping("/generalChat")
    @SendTo("/topic/generalChat")
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


    /**
     * ==================== Request endpoints ====================
     * */

    @RequestMapping(
            value = "/general-chat/{txtTo}",       // TODO: Secure endpoint (any logged user can see messages by any other user)
            method = RequestMethod.GET,
            produces = "application/json"
    )
    @ResponseBody
    public List<TextMessageDTO> getMessagesFrom(@PathVariable(name="txtTo") String generalChat){
        MessageLogger.log("ChatController - getMessagesFrom(...) - @RequestMapping(\"/general-chat/{txtTo}\")");
        ArrayList<TextMessageDTO> messages = null;

        messages = (ArrayList<TextMessageDTO>) chatService.getGeneralMessages(generalChat);

        return messages;
    }

    @RequestMapping(
            value = "/general-chat/{txtTo}/{timestamp}",       // TODO: Secure endpoint (any logged user can see messages by any other user)
            method = RequestMethod.GET,
            produces = "application/json"
    )
    @ResponseBody
    public List<TextMessageDTO> getMessagesFrom(@PathVariable(name="txtTo") String generalChat, @PathVariable(name="timestamp") String javascriptUTCTimestamp){
        MessageLogger.log("ChatController - getMessagesFrom(...) - @RequestMapping(\"/general-chat/{txtTo}/{timestamp}\")");
        ArrayList<TextMessageDTO> messages = null;

        messages = (ArrayList<TextMessageDTO>) chatService.getGeneralMessagesAfterTimestamp(generalChat, javascriptUTCTimestamp);

        return messages;
    }


    @RequestMapping(
            value = "/messages-with/{email}",
            method = RequestMethod.GET,
            produces = "application/json"
    )
    @ResponseBody
    public List<TextMessageDTO> getMessagesFromCurrentUserToUser(@PathVariable(name="email") String toUserEmail, Principal principal, HttpServletRequest request){
        MessageLogger.log("ChatController - getMessagesFrom(...) - @RequestMapping(\"/messages-with/{email}\")");
        ArrayList<TextMessageDTO> messages = null;

        messages = (ArrayList<TextMessageDTO>) chatService.getMessagesBetweenUsers( principal.getName(), toUserEmail, request);

        return messages;
    }

    @RequestMapping(
            value = "/messages-with/{email}/{timestamp}",
            method = RequestMethod.GET,
            produces = "application/json"
    )
    @ResponseBody
    public List<TextMessageDTO> getMessagesFromCurrentUserToUserAfterTimestamp(@PathVariable(name="email") String toUserEmail, @PathVariable(name="timestamp") String javascriptUTCTimestamp, Principal principal, HttpServletRequest request){
        MessageLogger.log("ChatController - getMessagesFrom(...) - @RequestMapping(\"/messages-with/{email}/{timestamp\")");
        ArrayList<TextMessageDTO> messages = null;

        messages = (ArrayList<TextMessageDTO>) chatService.getMessagesBetweenUsersAfterTimestamp( principal.getName(), toUserEmail, javascriptUTCTimestamp, request);

        return messages;
    }
}
