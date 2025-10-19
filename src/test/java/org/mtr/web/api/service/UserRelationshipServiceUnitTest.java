package org.mtr.web.api.service;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mtr.web.api.repository.UserRelationshipRepositoryJpa;
import org.mtr.web.api.repository.UserRepositoryJpa;
import org.mtr.web.api.repository.dao.UserRelationshipDAO;

import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
class UserRelationshipServiceUnitTest {

    public static final String userEmail = "user-email@test.com";
    public static final String friendEmail = "friend-email@test.com";

    @Mock private UserRelationshipRepositoryJpa userRelationshipJpa;
    @Mock private UserRepositoryJpa userRepositoryJpa;

    @InjectMocks
    private UserRelationshipService userRelationshipService;

    @Test
    public void T01_addUserToFriendsList() {
        when(userRelationshipJpa.findByUserIdAndFriendId(anyString(), anyString())).thenReturn(Optional.empty());
        when(userRelationshipJpa.save(any(UserRelationshipDAO.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepositoryJpa.existsByEmail(anyString())).thenReturn(true);

        UserRelationshipDAO newRelationshipExpected = UserRelationshipDAO.builder()
                .userId(userEmail).friendId(friendEmail).build();
        newRelationshipExpected.setUserId(userEmail);
        newRelationshipExpected.setFriendId(friendEmail);
        newRelationshipExpected.setStatus("FRIEND");
        final Timestamp inRelationshipSince = new Timestamp(System.currentTimeMillis());
        newRelationshipExpected.setInRelationshipSince(inRelationshipSince);

        UserRelationshipDAO newRelationshipActual =
                userRelationshipService.addUserToFriendsList(userEmail, friendEmail);

        assertEquals(newRelationshipExpected, newRelationshipActual);
    }

    @Test
    public void T02_blockUser() {
        UserRelationshipDAO existingFriendRelationship = UserRelationshipDAO.builder()
                .userId(userEmail).friendId(friendEmail).build();
        existingFriendRelationship.setStatus("FRIEND");
        existingFriendRelationship.setInRelationshipSince( new Timestamp(System.currentTimeMillis()));

        when(userRelationshipJpa.findByUserIdAndFriendId(anyString(), anyString())).thenReturn(Optional.of(existingFriendRelationship));
        when(userRelationshipJpa.save(any(UserRelationshipDAO.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertEquals( "FRIEND", existingFriendRelationship.getStatus(), "Before blocking, should be friends.");
        UserRelationshipDAO relationshipStatusFoe =
                userRelationshipService.blockUser(userEmail, friendEmail);

        assertEquals("FOE", existingFriendRelationship.getStatus(), "After blocking, should be foes.");
        assertEquals(existingFriendRelationship, relationshipStatusFoe);
    }
}