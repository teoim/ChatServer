package org.mtr.web.api.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mtr.web.api.controller.dto.TextMessageDTO;
import org.mtr.web.api.repository.ChatRepositoryJpa;
import org.mtr.web.api.repository.dao.TextMessageDAO;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class ChatServiceUnitTest {

    private static final List<TextMessageDAO> generalMessagesDAO = new ArrayList<>();
    private static final List<TextMessageDAO> privateMessagesDAO = new ArrayList<>();

    public static final Timestamp TIMESTAMP_MSG1 = Timestamp.valueOf("2024-11-17 06:08:35.345");
    public static final Timestamp TIMESTAMP_MSG2 = Timestamp.valueOf("2024-11-17 07:08:35.567");
    public static final Timestamp TIMESTAMP_MSG3 = Timestamp.valueOf("2024-11-17 08:09:35.678");
    public static final Timestamp TIMESTAMP_MSG4 = Timestamp.valueOf("2024-11-17 09:08:35.789");
    public static final Timestamp TIMESTAMP_MSG5 = Timestamp.valueOf("2024-11-17 10:08:35.890");
    public static final Timestamp TIMESTAMP_MSG6 = Timestamp.valueOf("2024-11-17 11:08:35.999");

    // UnitTests run in EET timezone, so I need to pass this format to service class
    public static final String TIMESTAMP_MSG1_JAVASCRIPT_ISO = "2024-11-17T06:08:35.345+02:00";
    public static final String TIMESTAMP_MSG4_JAVASCRIPT_ISO = "2024-11-17T09:08:35.789+02:00";

    public static final String MSG1_CONTENT = "Hi Laura!";
    public static final String MSG2_CONTENT = "Hi Jeff!";
    public static final String MSG3_CONTENT = "How are you?";
    public static final String MSG4_CONTENT = "Hello everyone from Jeff!";
    public static final String MSG5_CONTENT = "Hello everyone from Laura!";
    public static final String MSG6_CONTENT = "Is everyone ok?";

    public static final String GENERAL_CHAT_DESTINATION = "generalChat";
    public static final String EMAIL_JEFF = "jeff@gmail.com";
    public static final String EMAIL_LAURA = "laura@hotmail.com";

    @Mock
    private ChatRepositoryJpa repositoryJpa;

    @InjectMocks
    private ChatService service;


    @BeforeAll
    public static void setUp() {

        // Database messages:
        // 1 - Private messages:
        privateMessagesDAO.add(new TextMessageDAO(TIMESTAMP_MSG1, EMAIL_JEFF, EMAIL_LAURA, MSG1_CONTENT));
        privateMessagesDAO.add(new TextMessageDAO(TIMESTAMP_MSG2, EMAIL_LAURA, EMAIL_JEFF, MSG2_CONTENT));
        privateMessagesDAO.add(new TextMessageDAO(TIMESTAMP_MSG3, EMAIL_JEFF, EMAIL_LAURA, MSG3_CONTENT));
        // 2 - General Messages
        generalMessagesDAO.add(new TextMessageDAO(TIMESTAMP_MSG4, EMAIL_JEFF, GENERAL_CHAT_DESTINATION, MSG4_CONTENT));
        generalMessagesDAO.add(new TextMessageDAO(TIMESTAMP_MSG5, EMAIL_LAURA, GENERAL_CHAT_DESTINATION, MSG5_CONTENT));
        generalMessagesDAO.add(new TextMessageDAO(TIMESTAMP_MSG6, EMAIL_LAURA, GENERAL_CHAT_DESTINATION, MSG6_CONTENT));
    }


    @Test
    @Disabled("To be implemented.")
    public void T01_processPrivateMessage() {
    }

    @Test
    @Disabled("To be implemented.")
    public void T02_processGeneralMessage() {
    }

    @Test
    public void T03_getGeneralMessages() {
        when(repositoryJpa.getMessagesByTxtTo(GENERAL_CHAT_DESTINATION)).thenReturn(generalMessagesDAO);
        List<TextMessageDTO> expectedResult = new ArrayList<>();
        for(TextMessageDAO m : generalMessagesDAO){
            expectedResult.add(new TextMessageDTO(
                    m.getTimestamp(),
                    m.getTxtFrom(),
                    m.getTxtTo(),
                    m.getContent()));
        }

        List<TextMessageDTO> result = service.getGeneralMessages(GENERAL_CHAT_DESTINATION);

        assertEquals(expectedResult, result);
    }

    @Test
    public void T04_getGeneralMessagesAfterTimestamp() {
        when(repositoryJpa.getMessagesByTxtToAndTimestampGreaterThan(anyString(), any(Timestamp.class)))
                .thenReturn( generalMessagesDAO.stream()
                        .filter(m -> m.getTimestamp().compareTo(TIMESTAMP_MSG4) > 0)
                        .collect(Collectors.toList()));

        List<TextMessageDTO> expectedResult = new ArrayList<>();
        for(TextMessageDAO m : generalMessagesDAO){
            if(m.getTimestamp().compareTo(TIMESTAMP_MSG4) > 0){
                expectedResult.add(new TextMessageDTO(
                        m.getTimestamp(),
                        m.getTxtFrom(),
                        m.getTxtTo(),
                        m.getContent()));
            }
        }

        List<TextMessageDTO> result =
                service.getGeneralMessagesAfterTimestamp(GENERAL_CHAT_DESTINATION, TIMESTAMP_MSG4_JAVASCRIPT_ISO);

        assertEquals(expectedResult.size(), result.size());
        assertEquals( expectedResult, result);
    }

    @Test
    public void T05_getMessagesBetweenUsers() {
        when(repositoryJpa.getMessagesByTxtFromInAndTxtToIn( anyCollection(), anyCollection() ))
                .thenReturn(privateMessagesDAO.stream()
                        .filter(m -> List.of(EMAIL_JEFF, EMAIL_LAURA).contains(m.getTxtFrom()) ||
                                List.of(EMAIL_JEFF, EMAIL_LAURA).contains(m.getTxtTo()))
                        .collect(Collectors.toList()));

        List<TextMessageDTO> expectedResult = new ArrayList<>();
        for(TextMessageDAO m : privateMessagesDAO){
            if( List.of(EMAIL_JEFF, EMAIL_LAURA).contains(m.getTxtFrom())
                    || List.of(EMAIL_LAURA, EMAIL_JEFF).contains(m.getTxtTo())){
                expectedResult.add( new TextMessageDTO(
                        m.getTimestamp(),
                        m.getTxtFrom(),
                        m.getTxtTo(),
                        m.getContent()));
            }
        }

        List<TextMessageDTO> actualResult = service.getMessagesBetweenUsers(EMAIL_JEFF, EMAIL_LAURA, null);

        assertEquals(3, actualResult.size());
    }

    @Test
    public void T06_getMessagesBetweenUsersAfterTimestamp() {
        List<TextMessageDTO> expectedResult = privateMessagesDAO.stream()
                .filter(m -> m.getTimestamp().after(TIMESTAMP_MSG1))
                .map(m -> new TextMessageDTO(
                        m.getTimestamp(),
                        m.getTxtFrom(),
                        m.getTxtTo(),
                        m.getContent()))
                .toList();

        when(repositoryJpa.getMessagesByTxtFromInAndTxtToInAndTimestampGreaterThan(anyCollection(), anyCollection(), any(Timestamp.class)))
                .thenReturn(privateMessagesDAO.stream()
                        .filter(m -> m.getTimestamp().after(TIMESTAMP_MSG1))
                        .collect(Collectors.toList()));

        List<TextMessageDTO> actualResult = service.getMessagesBetweenUsersAfterTimestamp(EMAIL_JEFF, EMAIL_LAURA, TIMESTAMP_MSG1_JAVASCRIPT_ISO, null);

        assertEquals( expectedResult.size(), actualResult.size());
    }
}