package com.example.weyland.audit;

import jakarta.annotation.PostConstruct;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class AuditAspect {

    private final AuditDispatcher dispatcher;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuditAspect(AuditDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Around("@annotation(com.example.weyland.audit.WeylandWatchingYou)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Map<String, Object> auditData = new HashMap<>();
        auditData.put("method", signature.getName());
        auditData.put("args", joinPoint.getArgs());

        Object result;
        try {
            result = joinPoint.proceed();
            auditData.put("result", result);
        } catch (Throwable ex) {
            auditData.put("exception", ex.getClass().getSimpleName() + ": " + ex.getMessage());
            throw ex;
        }

        String auditMessage = objectMapper.writeValueAsString(auditData);
        dispatcher.dispatch(auditMessage);

        return result;
    }
}