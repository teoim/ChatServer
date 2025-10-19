package org.mtr.integration;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.mtr.web.api.controller.UserController;
import org.mtr.web.api.controller.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Based on src/test/resources/import.sql
 */
@Log4j2
public class UserIT extends BaseAbstractIT {

    private final UserController userController;


    @Autowired
    public UserIT(UserController userController) {
        this.userController = userController;
    }

    @Test
    public void T00_searchUserByEmailOrNick(){
        String userEmail = "hook@gogo.com";
        List<UserDTO> result = userController.searchUserByEmailOrNick(userEmail);

        assertNotNull(result);
        assertEquals(1, result.size(), "Expected one result for email '" + userEmail + "'");
        assertEquals(userEmail, result.get(0).getEmail(), "Email does not match.");
        assertEquals("Hook", result.get(0).getName(), "Name does not match.");
    }

    @Test
    public void T01_testSearchUserByEmailOrNick_singleResult(){
        String userEmail = "hook@gogo.com";
        String url = getBaseUrl() + "/searchUserByEmailOrNick/" + userEmail;
        ResponseEntity<List<UserDTO>> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<UserDTO>>(){});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Cooleanu", response.getBody().get(0).getNick());
        assertEquals("Hook", response.getBody().get(0).getName());
        assertEquals("hook@gogo.com", response.getBody().get(0).getEmail());
        assertEquals("I like chimcken", response.getBody().get(0).getBio());
        assertEquals("C.", response.getBody().get(0).getSurname());
        assertEquals("0", response.getBody().get(0).getId());
        assertEquals("", response.getBody().get(0).getPassword());
        assertEquals("", response.getBody().get(0).getPhonenr());
        assertNull(response.getBody().get(0).getDob());
    }

    @Test
    public void T02_testSearchUserByEmailOrNick_multipleResultsByEmail(){
        String searchText = "@gogo";
        String url = getBaseUrl() + "/searchUserByEmailOrNick/" + searchText;
        ResponseEntity<List<UserDTO>> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<UserDTO>>(){});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().size());
        assertEquals("hook@gogo.com", response.getBody().get(0).getEmail());
        assertEquals("jr@gogo.com", response.getBody().get(1).getEmail());
        assertEquals("teo@gogo.com", response.getBody().get(2).getEmail());
    }


    @Test
    public void T03_testSearchUserByEmailOrNick_multipleResultsByNick(){
        String searchText = "ol";
        String url = getBaseUrl() + "/searchUserByEmailOrNick/" + searchText;
        ResponseEntity<List<UserDTO>> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<UserDTO>>(){});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("Cooleanu", response.getBody().get(0).getNick());
        assertEquals("GoldenBoy", response.getBody().get(1).getNick());
    }

    @Test
    public void T04_testSearchUserByEmailOrNick_inexistent(){
        String searchText = "inexistent";
        String url = getBaseUrl() + "/searchUserByEmailOrNick/" + searchText;
        ResponseEntity<List<UserDTO>> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<UserDTO>>(){});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());
        assertEquals(Collections.emptyList(), response.getBody());
    }
}
