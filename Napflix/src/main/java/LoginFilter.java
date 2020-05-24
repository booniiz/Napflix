import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashSet;

@WebFilter(urlPatterns = "/*")
public class LoginFilter implements Filter {
    HashSet<String> allowedURI = new HashSet<>();
    @Override
    public void init(FilterConfig filterConfig) {
        // Add here for allow URI before logon
        allowedURI.add("/Napflix/login.html".toLowerCase());
        allowedURI.add("/Napflix/login.js".toLowerCase());
        allowedURI.add("/Napflix/ajax/login".toLowerCase());
        allowedURI.add("/Napflix/ajax/Androidlogin".toLowerCase());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession();
        if (allowedURI.contains(req.getRequestURI().toLowerCase())){
            chain.doFilter(request, response);
            return;
        }
        if (session.getAttribute("id") == null){
            resp.sendRedirect("/Napflix/login.html");
        }else{
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }

}
