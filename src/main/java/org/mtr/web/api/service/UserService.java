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

    public int registerUser(UserDTO newUserDto) {
        // TODO: Criptare password
        UserDAO newUserDao = dtoToDao(newUserDto);
        return this.userRepository.registerUser(newUserDao);
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

    private UserDAO dtoToDao(UserDTO userDto) {
        return new UserDAO( userDto.getId(),
                userDto.getNick(),
                userDto.getName(),
                userDto.getSurname(),
                userDto.getDob(),
                userDto.getPhonenr(),
                userDto.getEmail(),
                userDto.getBio(),
                userDto.getPassword());
    }

}
