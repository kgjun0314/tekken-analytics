package io.github.kgjun0314.tekken_analytics.benchmark;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ServiceMetricsAspect {

    private final ServiceMetrics metrics;

    @Around("""
        execution(* io.github.kgjun0314.tekken_analytics..service..*(..))
    """)
    public Object measureServiceCall(
            ProceedingJoinPoint joinPoint
    ) throws Throwable {

        MethodSignature signature =
                (MethodSignature) joinPoint.getSignature();

        String key =
                signature.getDeclaringType().getSimpleName()
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
