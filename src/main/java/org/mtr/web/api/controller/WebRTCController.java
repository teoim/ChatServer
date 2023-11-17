package org.mtr.web.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.mtr.logger.ErrorLogger;
import org.mtr.logger.MessageLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/webrtc")
public class WebRTCController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/test")
    @ResponseBody
    public String testWebrtc(HttpServletRequest request){
        MessageLogger.log("WebRTCController - testWebrtc(...) - @GetMapping(\"/api/webrtc/test\")");
        return "ok";
    }

    @PostMapping(path="/ice-server/message")
    @ResponseBody
    public String handleClientMessage(HttpServletRequest request){
        MessageLogger.log("WebRTCController - handleClientMessage(...) - @PostMapping(\"/api/webrtc/message\")");

        String messageBody = null;
        String targetUser = null;

        try {
            // !!! Once we do this, we cannot read the request body again as getReader has already been called
            messageBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            MessageLogger.log(messageBody);
        } catch (IOException e) {
            ErrorLogger.log(e, this.getClass().getSimpleName(), "handleClientMessage(...) - @PostMapping(\\\"/api/webrtc/message\\\")\"");
        }

        try {
            targetUser = String.valueOf((new ObjectMapper()).reader().readTree(messageBody).get("target"));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // Remove heading and trailing " characters
        if(( targetUser.charAt(0) == '"' ) && (targetUser.charAt( targetUser.length()-1) == '"')) {
            targetUser = targetUser.substring((targetUser.indexOf("\"")) + 1, targetUser.lastIndexOf("\""));
        }

        if(targetUser.equalsIgnoreCase("null") || targetUser==null) return messageBody;

        MessageLogger.log("Sending WebRTC request to " + targetUser);

        assert messageBody != null;
        assert !targetUser.isEmpty();
        assert !targetUser.equals("null");
        simpMessagingTemplate.convertAndSendToUser( targetUser, "/queue/sendICEMessage", messageBody);

        return messageBody;
    }
}
