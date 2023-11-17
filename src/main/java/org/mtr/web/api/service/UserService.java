package org.mtr.web.api.service;

import org.mtr.logger.ErrorLogger;
import org.mtr.logger.MessageLogger;
import org.mtr.web.api.controller.dto.UserDTO;
import org.mtr.web.api.repository.*;
import org.mtr.web.api.repository.dao.RoleDAO;
import org.mtr.web.api.repository.dao.RolesIdSeqDAO;
import org.mtr.web.api.repository.dao.UserDAO;
import org.mtr.web.api.repository.dao.UsersIdSeqDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRepositoryJpa userRepositoryJpa;

    @Autowired
    private UsersIdSeqRepositoryJPA lastUserSeqRepoJPA;

    @Autowired
    private RoleRepositoryJPA roleRepositoryJpa;

    @Autowired
    private RolesIdSeqRepositoryJPA lastRoleSeqRepoJPA;

    @Autowired
    RoleService roleService;


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

//    @Transactional
    public UserDAO registerUser(UserDTO newUserDto) {
        MessageLogger.log( "UserService - registerUser(UserDTO)");

        // TODO: Criptare password

        //Default profile photo:
        if( newUserDto.getProfilePhotoLink()==null || newUserDto.getProfilePhotoLink().isEmpty() ) newUserDto.setProfilePhotoLink("/images/icons/user-64.png");
        UserDAO newUserDao = dtoToDao(newUserDto);

        // Get the next user sequence from DB and assign it to the new user - TODO: do better maybe
        newUserDao.setId( lastUserSeqRepoJPA.findAll().get(0).getLast_value() + 1);

        // Default user role is "CHUBARKA"
        String defaultUserRole = "CHUBARKA";
        List<RoleDAO> newUserRole = roleRepositoryJpa.findByName(defaultUserRole);
        if (newUserRole.isEmpty()){
            RoleDAO newRole = new RoleDAO();
            newRole.setName(defaultUserRole);

            // Get roles next id when using JPA repository class for new user insert
            List<RolesIdSeqDAO> nextRoleSeq = lastRoleSeqRepoJPA.findAll();
            if (!nextRoleSeq.isEmpty()) {
                newRole.setId( (int)((nextRoleSeq.get(0)).getLast_value()) + 1 );
            }

            if( !roleService.createRole(newRole).getName().isEmpty()){
                newUserRole.add(newRole);
            } else{
                ErrorLogger.log(new RoleNotFoundException(), this.getClass().getSimpleName(), "registerUser(UserDTO)");
            }
        }

        newUserDao.setUserRoles( newUserRole);

//        return this.userRepository.registerUser(newUserDao);
        return this.userRepositoryJpa.save(newUserDao);
        // TODO: Create user role
    }

    public ArrayList<UserDTO> searchUserByEmailLikeOrNickLike(String textInput) {
        MessageLogger.log( "UserService - searchUserByEmailLikeOrNickLike(String)");

        ArrayList<UserDAO> dbResult = this.userRepositoryJpa.getUsersByEmailLikeIgnoreCaseOrNickLikeIgnoreCase(textInput, textInput);

        ArrayList<UserDTO> clientResult = new ArrayList<>();
        for(UserDAO userDao : dbResult){
            userDao.setPassword("");
            userDao.setId(0L);
            userDao.setPhonenr("");
            userDao.setDob(null);
            userDao.setUserRoles(null);
            userDao.setSurname(userDao.getSurname().substring(0,1).toUpperCase() + ".");
            clientResult.add(daoToDto(userDao));
        }

        return clientResult;
    }

    public UserDAO addUserToFriendsList( String myEmail, String myNewFriendEmail) {
        UserDAO myself = this.userRepositoryJpa.getUserByEmail(myEmail);
        UserDAO myNewFriend = this.userRepositoryJpa.getUserByEmail(myNewFriendEmail);

        if(myself.getFriends().contains(myNewFriend)){
            ErrorLogger.log(new Exception(" User " +myNewFriendEmail + " is already a friend."), Thread.currentThread().getStackTrace()[1]);
        } else {
            myself.getFriends().add(myNewFriend);
            try {
                myself = this.userRepositoryJpa.save(myself);
            } catch (Exception e) {
//            ErrorLogger.log(e, this.getClass().getSimpleName(), "addUserToFriendsList(String, String)");
                ErrorLogger.log(e, Thread.currentThread().getStackTrace()[1]);
            }
        }

        return myself;
    }

    private UserDTO daoToDto(UserDAO userDao) {
        return new UserDTO(
                String.valueOf(userDao.getId()),
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
        return new UserDAO(
                //userDto.getId() == null ? 0 : Long.parseLong(userDto.getId()),
                0L,
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
