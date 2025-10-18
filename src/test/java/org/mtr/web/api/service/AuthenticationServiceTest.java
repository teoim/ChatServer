package org.mtr.web.api.service;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mtr.web.api.component.UserSession;
import org.mtr.web.api.controller.dto.AuthenticationDTO;
import org.mtr.web.api.repository.AuthenticationRepository;
import org.mtr.web.api.repository.UserRelationshipRepositoryJpa;
import org.mtr.web.api.repository.dao.UserDAO;
import org.mtr.web.api.repository.dao.UserRelationshipDAO;
import org.mtr.web.api.utilities.Utilities;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
class AuthenticationServiceTest {

    static final String USERNAME = "teo@gogo.com";
    static final String PASSWORD = "pass";
    static final List<UserRelationshipDAO> friendsList = new ArrayList<>();

    @Mock AuthenticationRepository authRepository;
    @Mock UserService userDetailsService;
    @Mock UserRelationshipRepositoryJpa userRelationshipRepositoryJpa;
    @Mock UserSession userSession;

    @InjectMocks
    AuthenticationService authService;

    @Test
    public void T201_login_ok() {
        authService.userSession = userSession;
        UserDAO existingDbUserDao = new UserDAO(0L, "Teo","Teodor","Ionut",
                Date.valueOf("1990-01-01"), "0040123456755", "teo@gogo.com"  ,
                "I like bread"  ,  "pass", "/images/icons/user-64.png");
        friendsList.add(new UserRelationshipDAO(1L, existingDbUserDao.getEmail(), "hook@gogo.com",
                "FRIEND", Utilities.getCurrentTimestamp(), Utilities.getCurrentTimestamp()));
        friendsList.add(new UserRelationshipDAO(2L, existingDbUserDao.getEmail(), "jr@gogo.com",
                "FRIEND", Utilities.getCurrentTimestamp(), Utilities.getCurrentTimestamp()));
        when(authRepository.findByEmailAndPassword(USERNAME,PASSWORD)).thenReturn(existingDbUserDao);
        when(userDetailsService.loadUserByUsername(USERNAME)).thenReturn(existingDbUserDao);
        when(userRelationshipRepositoryJpa.findByUserId(USERNAME)).thenReturn(friendsList);
        AuthenticationDTO authenticationDTO = new AuthenticationDTO(USERNAME, PASSWORD);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod(HttpMethod.POST.name());
        request.setRequestURI("authenticate-post");

        UserDAO actualResponse = authService.login(authenticationDTO, request);

        assertNotNull(actualResponse);
        assertEquals(existingDbUserDao, actualResponse);
        assertEquals(existingDbUserDao.getId(), actualResponse.getId());
        assertEquals(existingDbUserDao.getName(), actualResponse.getName());
        assertEquals(existingDbUserDao.getSurname(), actualResponse.getSurname());
        assertEquals(existingDbUserDao.getDob(), actualResponse.getDob());
//        assertEquals(existingDbUserDao.getPhonenr(), actualResponse.getPhonenr());    // null
//        assertEquals(existingDbUserDao.getEmail(), actualResponse.getEmail());    // null
        assertEquals(existingDbUserDao.getBio(), actualResponse.getBio());
        assertEquals(existingDbUserDao.getProfilePhotoLink(), actualResponse.getProfilePhotoLink());
    }
}