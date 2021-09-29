package com.example.newsapi.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Aspect
@Component
public class LoggingHandler {
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Pointcut("within(com.example.newsapi.controller.*)")
    public void controller() {}

    @Pointcut("within(com.example.newsapi.service.*)")
    public void service() {}

    @Pointcut("within(@org.springframework.stereotype.Repository *)")
    public void repository() {}

    @Before("controller()")
    public void logRequest(JoinPoint joinPoint){
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();

        String requestURL = request.getRequestURL().toString();
        String method = request.getMethod();
        String arguments = Arrays.toString(joinPoint.getArgs());

        log.info ("--- request information start --------");
        log.info("requestURL : {}", requestURL);
        log.info("Controller : {}", joinPoint.getTarget().getClass());
        log.info("Method type: {}", method);
        log.info("Arguments: {}", arguments);
        log.info ("--- request information end --------");
    }

    @Before("controller() || service() || repository()")
    public void logBefore(JoinPoint joinPoint) {

        log.debug("Entering in Method :  {}", joinPoint.getSignature().getName());
        log.debug("Class Name :  {}", joinPoint.getSignature().getDeclaringTypeName());
        log.debug("Arguments :  {}", Arrays.toString(joinPoint.getArgs()));
        log.debug("Target class : {}", joinPoint.getTarget().getClass().getName());

    }

    @AfterReturning(pointcut = "controller() || service() || repository()", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        String returnValue = null;
        if(result != null)
            returnValue = result.toString();

        log.debug(joinPoint.getSignature() + " returned value : " + returnValue);
    }

    @AfterThrowing(pointcut = "controller() || service() || repository()", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        log.error("An exception has been thrown in {}.{}()", className, methodName);
        log.error("Message : " + ex.getMessage());
    }
}
