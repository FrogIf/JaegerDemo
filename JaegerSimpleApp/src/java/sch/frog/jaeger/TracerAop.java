package sch.frog.jaeger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;

@Component
@Aspect
public class TracerAop {
    
    @Autowired
    private Tracer tracer;

    @Around("within(sch.frog.jaeger.controller..*)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable{
        Span parentSpan = tracer.scopeManager().activeSpan();
        Signature signature = pjp.getSignature();
        Span currentSpan = this.tracer.buildSpan(signature.getDeclaringTypeName() + "." + signature.getName())
                .asChildOf(parentSpan)
                .start();
        currentSpan.setTag("class", signature.getDeclaringTypeName());
        currentSpan.setTag("method", signature.getName());

        Scope scope = tracer.scopeManager().activate(currentSpan);
        try{
            return pjp.proceed();
        }finally{
            try{
                currentSpan.finish();
            }finally{
                scope.close();
            }
        }
    }

}
