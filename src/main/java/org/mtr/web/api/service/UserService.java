package org.mtr.web.api.service;

import org.mtr.logger.MessageLogger;
import org.mtr.web.api.controller.dto.UserDTO;
import org.mtr.web.api.repository.UserRepository;
import org.mtr.web.api.repository.UserRepositoryJpa;
import org.mtr.web.api.repository.dao.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRepositoryJpa userRepositoryJpa;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //return this.userRepository.getUserByEmail(username);
        UserDAO customUser;
        User springUser = null;

//        customUser = this.userRepository.getUserByEmail(username);
        customUser = this.userRepositoryJpa.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Auth failed."));

        if (customUser == null) return null;

        springUser = new User( customUser.getUsername(), customUser.getPassword(), customUser.getAuthorities());
        return springUser;
    }


    public UserDTO getUserByEmail(String email){
        MessageLogger.log( "UserService - getUserByEmail(String)");
        return daoToDto( this.userRepository.getUserByEmail(email));
    }

    public int registerUser(UserDTO newUserDto) {
        MessageLogger.log( "UserService - registerUser(UserDTO)");

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
                userDao.getPassword(),
                userDao.getProfilePhotoLink());
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
                userDto.getPassword(),
                userDto.getProfilePhotoLink());
    }
}
