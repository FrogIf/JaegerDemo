package sch.frog.jaeger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import io.jaegertracing.Configuration;
import io.jaegertracing.internal.samplers.ConstSampler;
import io.opentracing.Tracer;

@org.springframework.context.annotation.Configuration
public class TracerConfigure {

    @Value("${spring.application.name:undefined}")
    private String appName;

    @Value("${jaeger.agent-host}")
    private String agentHost;

    @Value("${jaeger.agent-port:6831}")
    private int agentPort;
    
    @Bean
    public Tracer tracer(){
        return Configuration.fromEnv(this.appName)
        .withSampler(Configuration.SamplerConfiguration.fromEnv()
                .withType(ConstSampler.TYPE)
                .withParam(1)
        )
        .withReporter(Configuration.ReporterConfiguration.fromEnv()
                .withFlushInterval(1000)
                .withLogSpans(true)
                .withMaxQueueSize(1000)
                .withSender(Configuration.SenderConfiguration.fromEnv()
                        .withAgentHost(this.agentHost)
                        .withAgentPort(this.agentPort)
                )
        )
        .getTracer();
    }
}
