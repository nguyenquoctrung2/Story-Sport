package org.example.baitapbig.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.baitapbig.model.Account;
import org.example.baitapbig.repository.UserRepository;
import org.example.baitapbig.service.UserService;
import org.example.baitapbig.until.AppConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        String email = request.getParameter("username");
        System.out.println(email);
        Account userDtls = userRepository.findByEmail(email);
        System.out.println(userDtls);
         if(userDtls.getIsEnable()) {

            if (userDtls.getAccountNonLocked()) {

                if (userDtls.getFailedAttempt() < AppConstant.ATTEMPT_TIME) {
                    userService.increaseFailedAttempt(userDtls);
                } else {
                    userService.userAccountLock(userDtls);
                    exception = new LockedException("Your account is locked !! failed attempt 3");
                }
            } else {

                if (userService.unlockAccountTimeExpired(userDtls)) {
                    exception = new LockedException("Your account is unlocked !! Please try to login");
                } else {
                    exception = new LockedException("your account is Locked !! Please try after sometimes");
                }
            }

        } else {
            exception = new LockedException("your account is inactive");
        }
        super.setDefaultFailureUrl("/signin?error");
        super.onAuthenticationFailure(request, response, exception);
    }

}
