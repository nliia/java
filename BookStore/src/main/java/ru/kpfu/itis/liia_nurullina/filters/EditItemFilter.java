package ru.kpfu.itis.liia_nurullina.filters;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class EditItemFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        if (!request.getSession().getAttribute("session_uname").equals("admin")) {
            response.sendRedirect("/profile");
        } else {
            chain.doFilter(req, resp);
        }

    }

    @Override
    public void destroy() {

    }
}
