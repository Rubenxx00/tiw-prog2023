package it.polimi.tiw.controllers.pureHTML;
import it.polimi.tiw.beans.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = {"/Home", "/GetSessions"})
public class AuthFilter implements Filter {
    private FilterConfig filterConfig;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (isUserLoggedIn(httpRequest)) {
            // User is logged in, continue with the filter chain (execute the secured servlet)
            chain.doFilter(request, response);
        } else {
            // User is not logged in, redirect to the login page
            httpResponse.sendRedirect(filterConfig.getServletContext().getContextPath() + "/index.html");
        }
    }

    @Override
    public void destroy() {

    }

    private boolean isUserLoggedIn(HttpServletRequest request) {
        return request.getSession().getAttribute("currentUser") != null;
    }
}
