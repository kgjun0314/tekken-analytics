package io.github.kgjun0314.tekken_analytics.benchmark;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class RepositoryMetricsAspect {

    private final RepositoryMetrics metrics;

    @Around("""
    execution(* io.github.kgjun0314.tekken_analytics..repository..*RepositoryImpl.*(..))
    """)
    public Object measureRepositoryCall(
            ProceedingJoinPoint joinPoint
    ) throws Throwable {

        Class<?> repositoryClass = joinPoint.getTarget().getClass();

        String key =
                repositoryClass.getSimpleName()
                        + "."
                        + joinPoint.getSignature().getName();

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