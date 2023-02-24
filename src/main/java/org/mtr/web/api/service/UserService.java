package org.mtr.web.api.service;

import org.mtr.web.api.controller.dto.UserDTO;
import org.mtr.web.api.repository.UserRepository;
import org.mtr.web.api.repository.dao.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    public UserDTO getUserByEmail(String email){
        return daoToDto( this.userRepository.getUserByEmail(email));
    }

    private UserDTO daoToDto(UserDAO userDao) {
        return new UserDTO( userDao.getId(),
                userDao.getNick(),
                userDao.getName(),
                userDao.getSurname(),
                userDao.getDob(),
                userDao.getPhonenr(),
                userDao.getEmail(),
                userDao.getBio(),
                userDao.getPassword());
    }
}
