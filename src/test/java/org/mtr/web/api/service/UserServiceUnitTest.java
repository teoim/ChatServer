package org.mtr.web.api.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mtr.utils.StringUtils;
import org.mtr.utils.TestDataFactory;
import org.mtr.web.api.controller.dto.UserDTO;
import org.mtr.web.api.repository.*;
import org.mtr.web.api.repository.dao.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class UserServiceUnitTest {

    @Mock private UserRepositoryJpa userRepositoryJpa;
    @Mock private UserRepository userRepository;
    @Mock private UsersIdSeqRepositoryJPA usersIdSeqRepository;
    @Mock private RoleRepositoryJPA roleRepositoryJPA;
    @Mock private RolesIdSeqRepositoryJPA lastRoleSeqRepoJPA;
    @Mock private RoleService roleService;
    @Mock private UserRelationshipRepositoryJpa userRelationshipJpa;

    @InjectMocks
    UserService userService;

//    private static UserDTO existingUserDto;
    UserDTO newUserDto;
    private static UserDAO existingUserDao;
    private static RoleDAO existingUserRoleDao;
    List<UserDAO> databaseExistingUsers;

    @BeforeAll
    public static void setUp() throws Exception {

        existingUserDao = new UserDAO(3L, "nick", "John", "Doe", Date.valueOf("1980-12-31"),
                "+393461964567", "john@test.com", "bio test", "pass", "photo.png");
        existingUserRoleDao = new RoleDAO();
        existingUserRoleDao.setId(100);
        existingUserRoleDao.setName("USER");
        existingUserDao.setUserRoles(List.of(existingUserRoleDao));

/*        existingUserDto = new UserDTO(null, existingUserDao.getNick(), existingUserDao.getName(),
                existingUserDao.getSurname(), existingUserDao.getDob(), existingUserDao.getPhonenr(), existingUserDao.getEmail(),
                existingUserDao.getBio(), existingUserDao.getPassword(), existingUserDao.getProfilePhotoLink());*/
    }

    @Test
    public void T01_loadUserByUsername() {
        final String existingUserEmail = "john@test.com";
        when(userRepositoryJpa.findByEmail(existingUserEmail)).thenReturn(Optional.ofNullable(existingUserDao));
        UserDetails expectedResult = new User(existingUserDao.getUsername(), existingUserDao.getPassword(), existingUserDao.getAuthorities());

        UserDetails actualResponse = userService.loadUserByUsername(existingUserEmail);

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

        UserDTO actualResponse = userService.getUserByEmail("john@test.com");

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

        UserDTO actualResponse = userService.registerUser(newUserDto);

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
    public void T04_searchUserByEmailLikeOrNickLike() {
        List<UserDTO> expectedResponse = new ArrayList<>();
        databaseExistingUsers = TestDataFactory.createUsers(40);
        Map<String, List<UserDAO>> scenarios = TestDataFactory.createUserSearchScenarios(databaseExistingUsers);
        when(userRepositoryJpa.getUsersByEmailLikeIgnoreCaseOrNickLikeIgnoreCase(anyString(), anyString()))
                .thenAnswer(invocation -> scenarios.get(
                        StringUtils.stripPercentMarkers(invocation.getArgument(0))));


        scenarios.forEach((searchTerm, expectedUsers) -> {
                    expectedUsers.stream()
                            .map(userDao -> expectedResponse.add(
                                    new UserDTO(String.valueOf(userDao.getId()), userDao.getNick(), userDao.getName(),
                                            userDao.getSurname(), userDao.getDob(), userDao.getPhonenr(),
                                            userDao.getEmail(), userDao.getBio(), userDao.getPassword(), userDao.getProfilePhotoLink())));
                });

        scenarios.forEach((searchTerm, expectedUsers) -> {
            List<UserDTO> actualResponse = userService.searchUserByEmailLikeOrNickLike(searchTerm);

            List<String> expectedEmails = expectedUsers.stream()
                    .map(UserDAO::getEmail)
                    .collect(Collectors.toList());

            List<String> actualEmails = actualResponse.stream()
                    .map(UserDTO::getEmail)
                    .collect(Collectors.toList());

            assertEquals(expectedEmails, actualEmails, "Search term: " + searchTerm);
        });
    }

    @Test
    public void T05_addUserToFriendsList() {
        when(userRelationshipJpa.findByUserIdAndFriendId(anyString(), anyString())).thenReturn(null);
        when(userRelationshipJpa.save(any(UserRelationshipDAO.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserRelationshipDAO newRelationshipExpected = new UserRelationshipDAO();
        newRelationshipExpected.setUserId("user-email@test.com");
        newRelationshipExpected.setFriendId("friend-email@test.com");
        newRelationshipExpected.setStatus("FRIEND");
        final Timestamp inRelationshipSince = new Timestamp(System.currentTimeMillis());
        newRelationshipExpected.setInRelationshipSince(inRelationshipSince);

        UserRelationshipDAO newRelationshipActual =
                userService.addUserToFriendsList("user-email@test.com", "friend-email@test.com");

        assertEquals(newRelationshipExpected, newRelationshipActual);
    }

    @Test
    public void T06_blockUser() {
        UserRelationshipDAO existingFriendRelationship = new UserRelationshipDAO();
        existingFriendRelationship.setUserId("user-email@test.com");
        existingFriendRelationship.setFriendId("friend-email@test.com");
        existingFriendRelationship.setStatus("FRIEND");
        existingFriendRelationship.setInRelationshipSince( new Timestamp(System.currentTimeMillis()));

        when(userRelationshipJpa.findByUserIdAndFriendId(anyString(), anyString())).thenReturn(existingFriendRelationship);
        when(userRelationshipJpa.save(any(UserRelationshipDAO.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));


        assertEquals( "FRIEND", existingFriendRelationship.getStatus(), "Before blocking, should be friends.");
        UserRelationshipDAO relationshipStatusFoe =
                userService.blockUser("user-email@test.com", "friend-email@test.com");

        assertEquals("FOE", existingFriendRelationship.getStatus(), "After blocking, should be foes.");
        assertEquals(existingFriendRelationship, relationshipStatusFoe);
    }
}