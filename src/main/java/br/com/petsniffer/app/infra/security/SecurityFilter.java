package br.com.petsniffer.app.infra.security;

import br.com.petsniffer.app.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    @Autowired
    TokenService tokenService;
    @Autowired
    UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain)
                             throws ServletException, IOException {
        var token = this.recoverToken(request);
        if(token != null){
            var login = tokenService.validateToken(token);
            UserDetails user = userRepository.findByLogin(login);

            System.out.println("token: " + token);
            System.out.println("login: " + login);
            System.out.println("user: " + user.getUsername());


            var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request){
        var authHeader = request.getHeader("Authorization");
        var authHeaderContentType = request.getContentType();
        var authHeaderParameterNames = request.getParameterNames();
        var authHeaderParameterValues = request.getParameterValues("imagem");
        var authHeaderAttributeNames = request.getAttributeNames();


        System.out.println("authHeader:                 " + authHeader);
        System.out.println("authHeaderContentType:      " + authHeaderContentType);
        System.out.println("authHeaderParameterNames:   " + authHeaderParameterNames);
        System.out.println("authHeaderAttributeNames:   " + authHeaderAttributeNames);

        if(authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}

