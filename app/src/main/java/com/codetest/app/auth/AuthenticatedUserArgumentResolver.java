package com.codetest.app.auth;

import com.codetest.auth.AuthService;
import com.codetest.auth.UserDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

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

    private UserDetails resolveUserDetails(HttpServletRequest request) {
        /* TODO (Candidate)
         *  Extract the token using `request.getHeader("Authorization")`
         *  and use `authService.authenticate(String jwt)` to get the user if the token is valid,
         *  or return a 401 Status if not valid, or malformed etc..
         *  returning a `UserDetails` object from resolveArgument will automagically inject it into the Controller method call.
         */
        throw new UnsupportedOperationException("Implement Me");
    }
}
