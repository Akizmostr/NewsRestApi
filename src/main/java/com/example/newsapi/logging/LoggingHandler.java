package com.example.newsapi.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

@Aspect
@Component
public class LoggingHandler {
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Pointcut("within(com.example.newsapi.controller.impl.*)")
    public void controller() {}

    @Pointcut("within(com.example.newsapi.service.impl.*)")
    public void service() {}

    @Pointcut("within(@org.springframework.stereotype.Repository *)")
    public void repository() {}

    @Before("controller()")
    public void logRequest(JoinPoint joinPoint){
        //get request
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();

        String requestURL = request.getRequestURL().toString();
        String methodType = request.getMethod();

        //get arguments
        String arguments = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule()); //register module for using LocalDate with jackson
            //read arguments to string using jackson json, every argument must be mapped to String to avoid json exception(if the argument cannot be serialized)
            arguments = mapper.writeValueAsString(Arrays.stream(joinPoint.getArgs()).map(String::valueOf).collect(Collectors.toList()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        log.info ("--- request information start --------");
        log.info("requestURL : {}", requestURL);
        log.info("Method type: {}", methodType);
        log.info("Controller : {}", joinPoint.getTarget().getClass());
        log.info("Controller method :  {}", joinPoint.getSignature().getName());
        log.info("Arguments: {}", arguments);
        log.info ("--- request information end --------");
    }

    @AfterReturning(value = "controller()", returning = "result")
    public void logResponse(JoinPoint joinPoint, Object result){
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes() ;
        HttpServletResponse response = servletRequestAttributes.getResponse();

        log.info ("--- response information start --------");
        log.info("Response status code : {}", response.getStatus());
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            log.info("Response body: {}", mapper.writeValueAsString(result));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        log.info ("--- response information end --------");
    }

    @Before("service() || repository()")
    public void logBefore(JoinPoint joinPoint) {

        log.debug("Entering in Method :  {}", joinPoint.getSignature().getName());
        log.debug("Class Name :  {}", joinPoint.getSignature().getDeclaringTypeName());
        log.debug("Arguments :  {}", Arrays.toString(joinPoint.getArgs()));
        log.debug("Target class : {}", joinPoint.getTarget().getClass().getName());

    }

    @AfterReturning(pointcut = "service() || repository()", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        String returnValue = null;
        if(result != null)
            returnValue = result.toString();

        log.debug("Return value of {} : {}", joinPoint.getSignature(), returnValue);
    }

    @AfterThrowing(pointcut = "controller() || service() || repository()", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        log.error("An exception has been thrown in {}.{}()", className, methodName);
        log.error("Message : {}", ex.getMessage());
    }
}
