package org.mtr.web.api.service;

import org.mtr.logger.ErrorLogger;
import org.mtr.web.api.controller.dto.TextMessageDTO;
import org.mtr.web.api.repository.ChatRepositoryJpa;
import org.mtr.web.api.repository.dao.TextMessageDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;


@Service
public class ChatService {

    @Autowired
    ChatRepositoryJpa chatRepositoryJpa;

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

    private TextMessageDAO dtoToDao(TextMessageDTO messageDTO){
        return new TextMessageDAO(
                messageDTO.getTimestamp(),
                messageDTO.getFrom(),
                messageDTO.getTo(),
                messageDTO.getContent());
    }
}
