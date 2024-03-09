//package com.enigma.wmb_api.security;
//
//import com.enigma.enigma_shop.dto.response.JwtClaims;
//import com.enigma.enigma_shop.entity.UserAccount;
//import com.enigma.enigma_shop.service.JwtService;
//import com.enigma.enigma_shop.service.UserService;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.lang.NonNull;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.WebAuthenticationDetails;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class AuthenticationFilter extends OncePerRequestFilter {
//    private final JwtService jwtService;
//    private final UserService userService;
//    final String AUTH_HEADER = "Authorization";
//
//
//    @Override
//    protected void doFilterInternal(
//            @NonNull HttpServletRequest request,
//            @NonNull HttpServletResponse response,
//            @NonNull FilterChain filterChain
//    ) throws ServletException, IOException {
//        try {
//            String bearerToken = request.getHeader(AUTH_HEADER);
//
//            if (bearerToken != null && jwtService.verifyJwtToken(bearerToken)) {
//                JwtClaims jwtClaims = jwtService.getClaimsByToken(bearerToken);
//                UserAccount userAccount = userService.getByUserId(jwtClaims.getUserAccountId());
//                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                        userAccount.getUsername(),
//                        null,
//                        userAccount.getAuthorities()
//                );
//                authentication.setDetails(new WebAuthenticationDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            }
//        } catch (Exception e) {
//            log.error("Cannot set user authentication: {}", e.getMessage());
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}
