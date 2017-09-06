package com.fns.grivet.controller;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

@Aspect
@Component
class MetricsAspect {

	private final MetricRegistry metricRegistry;

	@Autowired
	public MetricsAspect(MetricRegistry metricRegistry) {
		this.metricRegistry = metricRegistry;
	}
	
	@Pointcut("execution(* com.fns.grivet.controller.*Controller.*(..))")
	private void controller() {}
	
	@Pointcut("@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
	private void deleteMapping() {}
	
	@Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    private void getMapping() {}
	
	@Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    private void postMapping() {}
	
	@Pointcut("@annotation(org.springframework.web.bind.annotation.PutMapping)")
    private void putMapping() {}

	@Around("controller() && deleteMapping()")
	public Object delete(ProceedingJoinPoint pjp) throws Throwable {
		return instrument(pjp);
	}
	
	@Around("controller() && getMapping()")
    public Object get(ProceedingJoinPoint pjp) throws Throwable {
        return instrument(pjp);
    }
	
	@Around("controller() && postMapping()")
    public Object post(ProceedingJoinPoint pjp) throws Throwable {
        return instrument(pjp);
    }
	
	@Around("controller() && putMapping()")
    public Object put(ProceedingJoinPoint pjp) throws Throwable {
        return instrument(pjp);
    }

    private Object instrument(ProceedingJoinPoint pjp) throws Throwable, Exception {
        Class<?> curClass = pjp.getTarget().getClass();
		MethodSignature ms = (MethodSignature) pjp.getSignature();
		Method m = ms.getMethod();

		Timer timer = metricRegistry.timer(MetricRegistry.name(curClass, m.getName()));

		try (Timer.Context context = timer.time()) {
			return pjp.proceed();
		} catch (Exception t) {
			throw t;
		}
    }

}

