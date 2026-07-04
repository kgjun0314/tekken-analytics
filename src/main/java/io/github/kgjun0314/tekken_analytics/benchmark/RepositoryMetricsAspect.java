package io.github.kgjun0314.tekken_analytics.benchmark;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
public class RepositoryMetricsAspect {

    private final RepositoryMetrics metrics;

    @Around("execution(* io.github.kgjun0314.tekken_analytics..repository..*(..))")
    public Object measureRepositoryCall(
            ProceedingJoinPoint joinPoint
    ) throws Throwable {

        MethodSignature signature =
                (MethodSignature) joinPoint.getSignature();

        Class<?> repositoryClass =
                signature.getMethod().getDeclaringClass();

        if (repositoryClass.equals(CrudRepository.class)
                || repositoryClass.equals(JpaRepository.class)) {

            repositoryClass = Arrays.stream(
                            joinPoint.getTarget()
                                    .getClass()
                                    .getInterfaces()
                    )
                    .filter(it -> it.getSimpleName().endsWith("Repository"))
                    .findFirst()
                    .orElse(repositoryClass);
        }

        String key =
                repositoryClass.getSimpleName()
                        + "."
                        + signature.getMethod().getName();

        long start = System.nanoTime();

        try {
            return joinPoint.proceed();
        } finally {
            metrics.record(
                    key,
                    System.nanoTime() - start
            );
        }
    }
}