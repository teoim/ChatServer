package org.mtr.web.api.service;

import org.mtr.web.api.controller.dto.LoginDTO;
import org.mtr.web.api.repository.AuthenticationRepositoryImpl;
import org.mtr.web.api.repository.AuthenticationRespository;
import org.mtr.web.api.repository.dao.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    AuthenticationRespository authRepository;

    @Autowired
    AuthenticationRepositoryImpl authRepositoryImpl;

    public UserDAO login(LoginDTO loginDto) {
        //return this.authRepository.findByEmailAndPassword( loginDto.getEmail(), loginDto.getPassword());
        return this.authRepositoryImpl.findUserByEmailAndPassword(loginDto.getEmail(), loginDto.getPassword());

        // TODO
        // dbUser = findByEmail
        // bcrypt.matches(loginDto.getPassword, dbUser.getPassword())   -> true / false
    }
}
