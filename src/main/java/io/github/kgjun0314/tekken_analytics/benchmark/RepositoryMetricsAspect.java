package io.github.kgjun0314.tekken_analytics.benchmark;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class RepositoryMetricsAspect {

    private final RepositoryMetrics metrics;

    @Before("""
            execution(* io.github.kgjun0314.tekken_analytics..repository.*.*(..))
            """)
    public void countRepositoryCall(
            JoinPoint joinPoint
    ) {

        String repository =
                joinPoint.getSignature()
                        .getDeclaringType()
                        .getSimpleName();

        String method =
                joinPoint.getSignature()
                        .getName();

        metrics.increment(
                repository + "." + method
        );
    }
}