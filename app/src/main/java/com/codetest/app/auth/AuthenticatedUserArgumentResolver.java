package com.codetest.app.auth;

import com.codetest.app.util.Printer;
import com.codetest.auth.AuthError;
import com.codetest.auth.AuthResult;
import com.codetest.auth.AuthService;
import com.codetest.auth.UserDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;

public class AuthenticatedUserArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthService authService;

    public AuthenticatedUserArgumentResolver(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthenticatedUser.class)
                && parameter.getParameterType().equals(UserDetails.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        return resolveUserDetails(request);
    }

    /**
     * Verifies a user and gives us their information absed on a JWT token. If the
     * call fails, the ResponseStatusException is thrown.
     * 
     * @param request The {@link HttpServletRequest} object containing the necessary
     *                Authorization header.
     * @return A {@link UserDetails} object, given that the request is a success.
     * @throws ResponseStatusException If for any reason a UserDetails object
     *                                 couldn't be found (expired token, missing
     *                                 Authorization header etc).
     */
    private UserDetails resolveUserDetails(HttpServletRequest request) throws ResponseStatusException {
        String foundToken = request.getHeader("Authorization");
        if (foundToken != null) {
            foundToken = foundToken.substring(BEARER_PREFIX.length());
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "There was no JWT token provided.");
        }

        AuthResult authResult = authService.authenticate(foundToken);
        if (authResult instanceof AuthResult.Authenticated(UserDetails user)) {
            return user;
        } else if (authResult instanceof AuthResult.Failed(AuthError error)) {
            // TODO: Add better logging of the reason that the user failed to authenticate.
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        // Technically unreachable code right now, but is still necessary for code to
        // compile.
        return null;

    }
}
