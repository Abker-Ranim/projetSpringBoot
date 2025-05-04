package tn.ucar.enicar.info.projetspring.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@org.aspectj.lang.annotation.Aspect
@Component
public class Aspect {
    private static final Logger logger = LoggerFactory.getLogger(Aspect.class);

    // Pointcut pour toutes les méthodes publiques dans le package sevices
    @Pointcut("execution(public * tn.ucar.enicar.info.projetspring.sevices.*.*(..))")
    public void serviceMethods() {
    }

    // Advice Before : Journaliser avant l'exécution de la méthode
    @Before("serviceMethods()")
    public void logBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();
        logger.info("Entering method: {}.{} with arguments: {}", className, methodName, Arrays.toString(args));
    }

    // Advice AfterReturning : Journaliser après une exécution réussie
    @AfterReturning(pointcut = "serviceMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        logger.info("Exiting method: {}.{} with result: {}", className, methodName, result);
    }

    // Advice AfterThrowing : Journaliser en cas d'exception
    @AfterThrowing(pointcut = "serviceMethods()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        logger.error("Exception in method: {}.{}: {}", className, methodName, exception.getMessage());
    }

    // Advice Around : Mesurer le temps d'exécution
    @Around("serviceMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        try {
            Object result = joinPoint.proceed(); // Exécuter la méthode
            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("Method: {}.{} executed in {} ms", className, methodName, executionTime);
            return result;
        } catch (Throwable t) {
            logger.error("Exception in method: {}.{}: {}", className, methodName, t.getMessage());
            throw t;
        }
    }
}