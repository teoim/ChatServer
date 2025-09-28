package org.mtr.integration;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.mtr.web.api.controller.dto.UserRelationshipDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


import static org.junit.jupiter.api.Assertions.*;

/**
 * Based on src/test/resources/import.sql
 */
@Log4j2
public class UserRelationshipIntegrationTest extends BaseAbstractIntegrationTest {

    @Test
    public void T01_testBlockUser(){
        String userEmailPrincipal = "teo@gogo.com"; // based on test config
        String userEmailToBlock = "jr@gogo.com";
        String urlGetCurrent = getBaseUrl() + "/relationships/" + userEmailToBlock;
        String urlBlock = getBaseUrl() + "/blockUser";

        // Before: Test existing relationship, before blocking user
        ResponseEntity<UserRelationshipDTO> responseGet =
                restTemplate.exchange(
                        urlGetCurrent,
                        HttpMethod.GET,
                        null,
                        UserRelationshipDTO.class
                );

        assertNotNull(responseGet);
        assertEquals(HttpStatus.OK, responseGet.getStatusCode());
        assertNotNull(responseGet.getBody());
        assertEquals(userEmailPrincipal, responseGet.getBody().getThisUser());
        assertEquals(userEmailToBlock, responseGet.getBody().getThatUser());
        assertEquals("FRIEND", responseGet.getBody().getRelationship());

        // After: Test blocking user and new relationship status
        HttpEntity<String> request = new HttpEntity<>(userEmailToBlock);
        ResponseEntity<String> response =
                restTemplate.exchange(
                        urlBlock,
                        HttpMethod.PUT,
                        request,
                        String.class
                );

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Blocked relationship:  jr@gogo.com is FOE", response.getBody());
    }


    @Test
    public void T02_testBlockUser(){
        String userEmailToBlock = "hook@gogo.com";
        String url = getBaseUrl() + "/blockUser";
        HttpEntity<String> request = new HttpEntity<>(userEmailToBlock);
        ResponseEntity<String> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.PUT,
                        request,
                        String.class
                );

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Blocked relationship:  hook@gogo.com is FOE", response.getBody());
    }
}
