package org.mtr.web.api.service;

import org.junit.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mtr.web.api.controller.dto.UserDTO;
import org.mtr.web.api.repository.*;
import org.mtr.web.api.repository.dao.RoleDAO;
import org.mtr.web.api.repository.dao.RolesIdSeqDAO;
import org.mtr.web.api.repository.dao.UserDAO;
import org.mtr.web.api.repository.dao.UsersIdSeqDAO;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserServiceTest {

    @Mock private UserRepositoryJpa userRepositoryJpa;
    @Mock private UserRepository userRepository;
    @Mock private UsersIdSeqRepositoryJPA usersIdSeqRepository;
    @Mock private RoleRepositoryJPA roleRepositoryJPA;
    @Mock private RolesIdSeqRepositoryJPA lastRoleSeqRepoJPA;
    @Mock private RoleService roleService;

    @InjectMocks
    UserService service;

    UserDTO existingUserDto;
    UserDTO newUserDto;
    UserDAO existingUserDao;
    RoleDAO existingUserRoleDao;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        existingUserDao = new UserDAO(3L, "nick", "John", "Doe", Date.valueOf("1980-12-31"),
                "+393461964567", "john@test.com", "bio test", "pass", "photo.png");
        existingUserRoleDao = new RoleDAO();
        existingUserRoleDao.setId(100);
        existingUserRoleDao.setName("USER");
        existingUserDao.setUserRoles(List.of(existingUserRoleDao));

        existingUserDto = new UserDTO(null, existingUserDao.getNick(), existingUserDao.getName(),
                existingUserDao.getSurname(), existingUserDao.getDob(), existingUserDao.getPhonenr(), existingUserDao.getEmail(),
                existingUserDao.getBio(), existingUserDao.getPassword(), existingUserDao.getProfilePhotoLink());
    }

    @Test
    public void T01_loadUserByUsername() {
        final String existingUserEmail = "john@test.com";
        when(userRepositoryJpa.findByEmail(existingUserEmail)).thenReturn(Optional.ofNullable(existingUserDao));
        UserDetails expectedResult = new User(existingUserDao.getUsername(), existingUserDao.getPassword(), existingUserDao.getAuthorities());

        UserDetails actualResponse = service.loadUserByUsername(existingUserEmail);

        assertNotNull(actualResponse);
        assertEquals(expectedResult, actualResponse);
        assertEquals(expectedResult.getUsername(), actualResponse.getUsername());
        assertEquals(expectedResult.getPassword(), actualResponse.getPassword());
        assertEquals(expectedResult.getAuthorities(), actualResponse.getAuthorities());
    }

    @Test
    public void T02_getUserByEmail() {
        when(userRepository.getUserByEmail("john@test.com")).thenReturn(existingUserDao);
        UserDTO expectedResponse = new UserDTO(String.valueOf(3L), "nick", "John",
                "Doe", Date.valueOf("1980-12-31"), "+393461964567",
                "john@test.com", "bio test", "pass", "photo.png");

        UserDTO actualResponse = service.getUserByEmail("john@test.com");

        assertEquals(expectedResponse, actualResponse);
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getNick(), actualResponse.getNick());
        assertEquals(expectedResponse.getName(), actualResponse.getName());
        assertEquals(expectedResponse.getSurname(), actualResponse.getSurname());
        assertEquals(expectedResponse.getDob(), actualResponse.getDob());
        assertEquals(expectedResponse.getPhonenr(), actualResponse.getPhonenr());
        assertEquals(expectedResponse.getEmail(), actualResponse.getEmail());
        assertEquals(expectedResponse.getBio(), actualResponse.getBio());
        assertEquals(expectedResponse.getPassword(), actualResponse.getPassword());
        assertEquals(expectedResponse.getProfilePhotoLink(), actualResponse.getProfilePhotoLink());
    }

    @Test
    public void T03_registerUser() {
        newUserDto = new UserDTO(null, "new-nick", "John", "Doe",
                Date.valueOf("1980-12-31"), "+393461964567", "john@test.com",
                "bio test", "pass", "new-photo.png");
        UsersIdSeqDAO lastUserId = new UsersIdSeqDAO();
        RolesIdSeqDAO lastRoleId = new RolesIdSeqDAO();
        lastUserId.setLast_value(49);
        lastRoleId.setLast_value(99);

        when(usersIdSeqRepository.findAll()).thenReturn(Collections.singletonList(lastUserId));
        when(roleRepositoryJPA.findByName(anyString())).thenReturn(new ArrayList<>());
        when(lastRoleSeqRepoJPA.findAll()).thenReturn(Collections.singletonList(lastRoleId));
        when(roleService.createRole(any(RoleDAO.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepositoryJpa.save(any(UserDAO.class))).thenAnswer(invocation -> invocation.getArgument(0)); // to be verified

        UserDTO expectedResponse = new UserDTO(String.valueOf(lastUserId.getLast_value()+1), newUserDto.getNick(),
                newUserDto.getName(), newUserDto.getSurname(), newUserDto.getDob(), newUserDto.getPhonenr(),
                newUserDto.getEmail(), newUserDto.getBio(), newUserDto.getPassword(), newUserDto.getProfilePhotoLink());

        UserDTO actualResponse = service.registerUser(newUserDto);

        assertEquals(expectedResponse, actualResponse);
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getNick(), actualResponse.getNick());
        assertEquals(expectedResponse.getName(), actualResponse.getName());
        assertEquals(expectedResponse.getSurname(), actualResponse.getSurname());
        assertEquals(expectedResponse.getDob(), actualResponse.getDob());
        assertEquals(expectedResponse.getPhonenr(), actualResponse.getPhonenr());
        assertEquals(expectedResponse.getEmail(), actualResponse.getEmail());
        assertEquals(expectedResponse.getBio(), actualResponse.getBio());
        assertEquals(expectedResponse.getPassword(), actualResponse.getPassword());
        assertEquals(expectedResponse.getProfilePhotoLink(), actualResponse.getProfilePhotoLink());
        // TODO: ? test for new user dto role (functionality to be implemented on dto side)
    }

    @Test
    @Ignore("To be implemented.")
    public void T04_searchUserByEmailLikeOrNickLike() {
    }

    @Test
    @Ignore("To be implemented.")
    public void T05_addUserToFriendsList() {
    }

    @Test
    @Ignore("To be implemented.")
    public void T06_blockUser() {
    }
}