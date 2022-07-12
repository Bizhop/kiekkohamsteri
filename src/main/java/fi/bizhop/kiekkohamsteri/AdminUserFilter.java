package fi.bizhop.kiekkohamsteri;

import fi.bizhop.kiekkohamsteri.service.AuthService;
import lombok.RequiredArgsConstructor;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

@RequiredArgsConstructor
public class AdminUserFilter implements Filter {
    final AuthService authService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        var httpServletRequest = (HttpServletRequest) servletRequest;
        var httpServletResponse = (HttpServletResponse) servletResponse;

        var user = authService.getUser(httpServletRequest);
        httpServletRequest.setAttribute("user", user);

        if(user == null) {
            httpServletResponse.sendError(SC_UNAUTHORIZED);
            return;
        }
        else if(user.getLevel() != 2) {
            httpServletResponse.sendError(SC_FORBIDDEN);
            return;
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
