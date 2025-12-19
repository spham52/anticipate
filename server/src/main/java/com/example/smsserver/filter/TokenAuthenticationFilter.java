package com.example.smsserver.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
// custom security filter for verifying whether user is authenticated
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String USER_ID_CLAIM = "user_id";
    private static final String AUTHORISATION_HEADER = "Authorization";

    private final FirebaseAuth firebaseAuth;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // get authorisation header
        String authorisationHeader = request.getHeader(AUTHORISATION_HEADER);

        if (authorisationHeader != null && authorisationHeader.startsWith(BEARER_PREFIX)) {
            // grab actual token ID
            String token = authorisationHeader.replace(BEARER_PREFIX, "");
            Optional<String> userID = extractUserIdFromToken(token);

            if (userID.isPresent()) {
                var authentication = new UsernamePasswordAuthenticationToken(userID.get(),
                        null, null);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                setAuthErrorDetails(response);
            }
        }
        filterChain.doFilter(request, response);
    }

    // helper function to get userID from token
    private Optional<String> extractUserIdFromToken(String token) {
        try {
            FirebaseToken firebaseToken = firebaseAuth.verifyIdToken(token, true);
            String userID = String.valueOf(firebaseToken.getClaims().get(USER_ID_CLAIM));
            return Optional.of(userID);
        } catch (FirebaseAuthException exception) {
            return Optional.empty();
        }
    }

    // helper function, creating ProblemDetail error response, converting to JSON using ObjectMapper
    private void setAuthErrorDetails(HttpServletResponse response) throws IOException {
        HttpStatus unauthorised = HttpStatus.UNAUTHORIZED;
        response.setStatus(unauthorised.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail
                (unauthorised, "Failed to authenticate: missing, invalid " +
                "or expired token.");
        response.getWriter().write(objectMapper.writeValueAsString(problemDetail));
    }
}
