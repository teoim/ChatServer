package org.mtr.web.api.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.mtr.logger.ErrorLogger;
import org.mtr.logger.MessageLogger;
import org.mtr.web.api.component.UserSession;
import org.mtr.web.api.controller.dto.AuthenticationDTO;
import org.mtr.web.api.repository.AuthenticationRepositoryImpl;
import org.mtr.web.api.repository.AuthenticationRepository;
import org.mtr.web.api.repository.UserRelationshipRepositoryJpa;
import org.mtr.web.api.repository.dao.UserDAO;
import org.mtr.web.api.repository.dao.UserRelationshipDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Service
public class AuthenticationService {

    @Autowired
    UserService userDetailsService;

    @Autowired
    AuthenticationRepository authRepository;

    @Autowired
    UserRelationshipRepositoryJpa userRelationshipRepositoryJpa;

    @Autowired
    AuthenticationRepositoryImpl authRepositoryImpl;

    @Autowired
    UserSession userSession;


    public UserDAO login(AuthenticationDTO authenticationDto, HttpServletRequest request) {
        MessageLogger.log( "AuthenticationService - login(AuthenticationDTO)");
        UserDAO user =  this.authRepository.findByEmailAndPassword( authenticationDto.getUsername(), authenticationDto.getPassword());
        //return this.authRepositoryImpl.findUserByEmailAndPassword(authenticationDto.getUsername(), authenticationDto.getPassword());

        List<UserRelationshipDAO> userRelationshipDAOList;

        if ( user != null) {
            DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
            authProvider.setUserDetailsService(this.userDetailsService);
            authProvider.setPasswordEncoder( NoOpPasswordEncoder.getInstance());    //TODO: upgrade to BCrypt
            AuthenticationManager authManager = new ProviderManager( authProvider);
            Authentication auth = authManager.authenticate( new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities()));
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            /*Authentication authentication =
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword(), user.getAuthorities());
            context.setAuthentication(authentication);*/
            context.setAuthentication(auth);

            HttpSession session = request.getSession(true);
            session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, context);
            // get jsession id:
            // request.getSession().getId()


            userRelationshipDAOList = this.userRelationshipRepositoryJpa.findByUserId(user.getEmail());

            List<String> friendsList = new ArrayList<>();
            for( UserRelationshipDAO friend : userRelationshipDAOList){
                if(friendsList.contains(friend.getFriendId())){
                    ErrorLogger.log( new Exception("Friend email duplicate detected: " + friend.getFriendId()
                            + "\nIn list: " + friendsList.toString())
                                    , this.getClass().getSimpleName()
                                    , "login(AuthenticationDTO, HttpServletRequest)");
                } else {
                    friendsList.add(friend.getFriendId());
                }
            }
            session.setAttribute("friendsList", friendsList);

            SecurityContextHolder.setContext(context);

            userSession.setEmail(user.getEmail() + " - " + user.getNick());      // TODO: inlocuirea email-ului in sesiune cu un token jwt
            userSession.setUsername(user.getNick());
            userSession.setBio(user.getBio());
        }

        // TODO
        // dbUser = findByEmail
        // bcrypt.matches(authenticationDto.getPassword, dbUser.getPassword())   -> true / false

        return user;
    }
}
