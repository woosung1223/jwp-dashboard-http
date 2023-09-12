package org.apache.catalina.container;

import nextstep.jwp.servlets.*;
import org.apache.catalina.filter.LoggingFilter;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.RequestURI;
import org.apache.coyote.http11.message.response.HttpResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Context {

    private static final Map<String, Servlet> mappings;

    private static final StaticResourceServlet staticResourceServlet;

    static {
        mappings = new HashMap<>();
        mappings.put("/", new RootServlet());
        mappings.put("/index", new IndexServlet());
        mappings.put("/login", new LoginServlet());
        mappings.put("/register", new RegisterServlet());

        staticResourceServlet = new StaticResourceServlet();
    }

    private Context() {
    }

    public static HttpResponse service(HttpRequest httpRequest) throws IOException {
        RequestURI requestURI = httpRequest.getRequestURI();
        String absolutePath = requestURI.absolutePath();
        HttpResponse httpResponse = HttpResponse.init();

        LoggingFilter.doFilter(httpRequest, httpResponse);

        Servlet servlet = findServlet(absolutePath);
        servlet.service(httpRequest, httpResponse);

        return httpResponse;
    }

    private static Servlet findServlet(String absolutePath) {
        return mappings.getOrDefault(absolutePath, staticResourceServlet);
    }
}
