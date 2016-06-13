package com.fns.grivet.controller;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
class MetricsAspect {

    private final MetricRegistry metricRegistry;
    
    @Autowired
    public MetricsAspect(MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }
    
    @Around("execution(* com.fns.grivet.controller.*Controller.*(..)) && @annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public Object run(ProceedingJoinPoint pjp) throws Throwable {

        Class<?> curClass = pjp.getTarget().getClass();
        MethodSignature ms = (MethodSignature) pjp.getSignature();
        Method m = ms.getMethod();

        Timer timer = metricRegistry.timer(MetricRegistry.name(curClass, m.getName()));
        Timer.Context context = timer.time();

        try {
            return pjp.proceed();
        } catch (Throwable t){
            throw t;
        } finally {
            context.close();
        }
    }

}

