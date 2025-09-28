package org.mtr.web.api.service;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mtr.web.api.repository.UserRelationshipRepositoryJpa;
import org.mtr.web.api.repository.dao.UserRelationshipDAO;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
class UserRelationshipServiceUnitTest {

    @Mock
    private UserRelationshipRepositoryJpa userRelationshipJpa;

    @InjectMocks
    private UserRelationshipService userRelationshipService;

    @Test
    public void T01_addUserToFriendsList() {
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
                userRelationshipService.addUserToFriendsList("user-email@test.com", "friend-email@test.com");

        assertEquals(newRelationshipExpected, newRelationshipActual);
    }

    @Test
    public void T02_blockUser() {
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
                userRelationshipService.blockUser("user-email@test.com", "friend-email@test.com");

        assertEquals("FOE", existingFriendRelationship.getStatus(), "After blocking, should be foes.");
        assertEquals(existingFriendRelationship, relationshipStatusFoe);
    }
}