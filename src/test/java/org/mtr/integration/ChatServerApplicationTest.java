package org.mtr.integration;

import org.junit.jupiter.api.Test;
import org.mtr.web.api.controller.*;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class ChatServerApplicationTest extends BaseAbstractIntegrationTest {

    @Autowired
    private AuthenticationController authenticationController;
    @Autowired
    private ChatController chatController;
    @Autowired
    private DashboardController dashboardController;
    @Autowired
    private UserController userController;
//    @Autowired
//    private WebRTCController webRTCController;


    @Test
    public void T01_contextLoadsAuthenticationController(){
        assertNotNull(authenticationController);
    }

    @Test
    public void T02_contextLoadsChatController(){
        assertNotNull(chatController);
    }

    @Test
    public void T03_contextLoadsDashboardController(){
        assertNotNull(dashboardController);
    }

    @Test
    public void T04_contextLoadsUserController(){
        assertNotNull(userController);
    }

//    @Test
//    public void T05_contextLoadsWebRTCController(){
//        assertNotNull(webRTCController);
//    }
}