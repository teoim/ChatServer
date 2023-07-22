package org.mtr.web.api.service;

import org.mtr.logger.ErrorLogger;
import org.mtr.web.api.controller.dto.TextMessageDTO;
import org.mtr.web.api.repository.ChatRepositoryJpa;
import org.mtr.web.api.repository.dao.TextMessageDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class ChatService {

    @Autowired
    ChatRepositoryJpa chatRepositoryJpa;


    /**
     * ==================== Messages sent processing methods ====================
     * */

    public void processPrivateMessage(TextMessageDTO messageDTO) {
        try {
            TextMessageDAO messageDAO = dtoToDao(messageDTO);
            TextMessageDAO savedMessage = chatRepositoryJpa.save(messageDAO);
        }
        catch(IllegalArgumentException e ){
            ErrorLogger.log(e, this.getClass().getSimpleName(), "processPrivateMessage(TextMessageDAO)");
        }
        catch(OptimisticLockingFailureException e ){
            ErrorLogger.log(e, this.getClass().getSimpleName(), "processPrivateMessage(TextMessageDAO)");
        }
    }

    public void processGeneralMessage(TextMessageDTO messageDTO) {
        try {
            TextMessageDAO messageDAO = dtoToDao(messageDTO);
            TextMessageDAO savedMessage = chatRepositoryJpa.save(messageDAO);
        }
        catch(IllegalArgumentException e ){
            ErrorLogger.log(e, this.getClass().getSimpleName(), "processPrivateMessage(TextMessageDAO)");
        }
        catch(OptimisticLockingFailureException e ){
            ErrorLogger.log(e, this.getClass().getSimpleName(), "processPrivateMessage(TextMessageDAO)");
        }
    }


    /**
     * ==================== GET methods ====================
     * */

    public List<TextMessageDTO> getGeneralMessages(String toTxt) {
        List<TextMessageDAO> messagesDAO;
        List<TextMessageDTO> messagesDTO = new ArrayList<TextMessageDTO>();

//        messagesDAO = chatRepositoryJpa.getMessagesByTxtFrom(username);   // Test get messages for "generalChat"
        messagesDAO = chatRepositoryJpa.getMessagesByTxtTo(toTxt);

        for(TextMessageDAO dao : messagesDAO){
            messagesDTO.add( new TextMessageDTO(
                    dao.getTimestamp(),
                    dao.getTxtFrom(),
                    dao.getTxtTo(),
                    dao.getContent()));
        }

        return messagesDTO;
    }

    public List<TextMessageDTO> getGeneralMessagesAfterTimestamp(String toTxt, String javascriptUTCTimestamp) {
        List<TextMessageDAO> messagesDAO;
        List<TextMessageDTO> messagesDTO = new ArrayList<TextMessageDTO>();

        // Convert javascriptUTCTimestamp from UTC to local format
        Timestamp afterTimestamp = null;
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        try{
            Date localDate = isoFormat.parse(javascriptUTCTimestamp);
            afterTimestamp = new Timestamp(localDate.getTime());
        } catch(ParseException e) {
            ErrorLogger.log(e, this.getClass().getSimpleName(), "getMessagesFromUserToUserAfterTimestamp(String,String,String)");
            afterTimestamp = new Timestamp( System.currentTimeMillis() - (24 * 60 * 60 * 1000) );  // get messages from the last 24 hours TODO: maybe think about it
        }

//        messagesDAO = chatRepositoryJpa.getMessagesByTxtTo(toTxt);
        messagesDAO = chatRepositoryJpa.getMessagesByTxtToAndTimestampGreaterThan(toTxt, afterTimestamp);

        for(TextMessageDAO dao : messagesDAO){
            messagesDTO.add( new TextMessageDTO(
                    dao.getTimestamp(),
                    dao.getTxtFrom(),
                    dao.getTxtTo(),
                    dao.getContent()));
        }

        return messagesDTO;
    }

    public List<TextMessageDTO> getMessagesBetweenUsers(String fromUserEmail, String toUserEmail) {
        List<TextMessageDAO> messagesDAO;
        List<TextMessageDTO> messagesDTO = new ArrayList<TextMessageDTO>();

        Collection<String> messagesBetweenUsers = new ArrayList<>();
        messagesBetweenUsers.add(fromUserEmail);
        messagesBetweenUsers.add(toUserEmail);

        messagesDAO = chatRepositoryJpa.getMessagesByTxtFromInAndTxtToIn(messagesBetweenUsers, messagesBetweenUsers);

        for(TextMessageDAO dao : messagesDAO){
            messagesDTO.add( new TextMessageDTO(
                    dao.getTimestamp(),
                    dao.getTxtFrom(),
                    dao.getTxtTo(),
                    dao.getContent()));
        }

        return messagesDTO;
    }

    public List<TextMessageDTO> getMessagesBetweenUsersAfterTimestamp(String fromUserEmail, String toUserEmail, String javascriptUTCTimestamp) {
        List<TextMessageDAO> messagesDAO;
        List<TextMessageDTO> messagesDTO = new ArrayList<TextMessageDTO>();

        Collection<String> messagesBetweenUsers = new ArrayList<>();
        messagesBetweenUsers.add(fromUserEmail);
        messagesBetweenUsers.add(toUserEmail);

        // Convert javascriptUTCTimestamp from UTC to local format
        Timestamp afterTimestamp = null;
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        try{
            Date localDate = isoFormat.parse(javascriptUTCTimestamp);
            afterTimestamp = new Timestamp(localDate.getTime());
        } catch(ParseException e) {
            ErrorLogger.log(e, this.getClass().getSimpleName(), "getMessagesFromUserToUserAfterTimestamp(String,String,String)");
            afterTimestamp = new Timestamp( System.currentTimeMillis() - (24 * 60 * 60 * 1000) );  // get messages from the last 24 hours TODO: maybe think about it
        }

        messagesDAO = chatRepositoryJpa.getMessagesByTxtFromInAndTxtToInAndTimestampGreaterThan(messagesBetweenUsers, messagesBetweenUsers, afterTimestamp);

        for(TextMessageDAO dao : messagesDAO){
            messagesDTO.add( new TextMessageDTO(
                    dao.getTimestamp(),
                    dao.getTxtFrom(),
                    dao.getTxtTo(),
                    dao.getContent()));
        }

        return messagesDTO;
    }


    /**
     * ==================== Utility methods ====================
     * */

    private TextMessageDAO dtoToDao(TextMessageDTO messageDTO){
        return new TextMessageDAO(
                messageDTO.getTimestamp(),
                messageDTO.getFrom(),
                messageDTO.getTo(),
                messageDTO.getContent());
    }

    private TextMessageDTO daoToDto(TextMessageDAO messageDAO){
        return new TextMessageDTO(
                messageDAO.getTimestamp(),
                messageDAO.getTxtFrom(),
                messageDAO.getTxtTo(),
                messageDAO.getContent());
    }
}
