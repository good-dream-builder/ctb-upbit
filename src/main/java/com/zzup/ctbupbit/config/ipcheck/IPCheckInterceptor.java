package com.zzup.ctbupbit.config.ipcheck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Component
public class IPCheckInterceptor implements HandlerInterceptor, AsyncHandlerInterceptor {
    public Logger logger = LoggerFactory.getLogger(IPCheckInterceptor.class);

    @Autowired
    private IPCheckRepository ipCheckRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        final String userHeader = request.getHeader("X-Ctb-Client-Id");

        if(userHeader == null || userHeader.equals("psj") == false) {
            final String bannedIP = request.getRemoteAddr();

            IPCheck ipCheck = new IPCheck();
            ipCheck.setBannedIP(bannedIP);

            ipCheckRepository.save(ipCheck);

            response.setContentType("text/html; charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<a>https://www.police.go.kr/index.do</a>");
            out.flush();

            return false;
        } else {
            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
