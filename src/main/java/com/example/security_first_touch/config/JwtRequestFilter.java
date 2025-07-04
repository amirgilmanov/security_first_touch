package com.example.security_first_touch.config;

import com.example.security_first_touch.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Этот фильтр включится, когда клиент обратиться в защищенную область
 * Если область не защищена, то тогда этот фильтр не будет действовать
 */
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;


    /**
     *
     * Метод, чтобы встроится в цепочку фильтров Spring
     * Если запрос в защищенную область если в нем есть токен(JWT), этот фильтр будет
     * перекладывать данные из токена в Security контекст
     */

    //TODO: проверять заблокирован пользователь или нет
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;

        String path = request.getRequestURI();

        log.info("Request URI: " + path);

        if (path.equals("/registration")){
            filterChain.doFilter(request, response);
            return;
        }

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7);

            // Получаем информацию о пользователе.
            // Проверяем пользователя.
            //Библиотека jjwt проверяет подпись, дату истечения

            try {
                username = jwtUtil.getUsername(jwtToken);
            } catch (ExpiredJwtException e) {
                log.debug("Время жизни токена вышло");
            } catch (SignatureException e) {
                log.debug("Неверная подпись");
            }
        }

        //Проверка на то, что контекст не занят кем-то другим
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    jwtUtil.getUserRoles(jwtToken).stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList()
            );
            SecurityContextHolder.getContext().setAuthentication(token);
        }
        //Обращаемся к следующему фильтру в цепочке
        filterChain.doFilter(request, response);
    }
}
