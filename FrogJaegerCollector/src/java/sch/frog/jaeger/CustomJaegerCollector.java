package sch.frog.jaeger;

import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jaegertracing.api_v2.Model.Batch;
import sch.frog.jaeger.config.ConfigHolder;
import sch.frog.jaeger.server.IMessageHandler;
import sch.frog.jaeger.server.JaegerGrpcServer;

public class CustomJaegerCollector {

    private static final Logger logger = LoggerFactory.getLogger(CustomJaegerCollector.class);
    
    public static void main(String[] args) throws InterruptedException{
        String grpcPort = ConfigHolder.getProperty("grpc.port","14250");

        JaegerGrpcServer jaegerGRPCServer = new JaegerGrpcServer();
        try {
            jaegerGRPCServer.start(Integer.parseInt(grpcPort), Arrays.asList(new IMessageHandler() {

                @Override
                public void handle(Batch batch) {
                    logger.info("receive data : {}", batch);
                }
                
            }));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        logger.info("collector start success.");
        jaegerGRPCServer.blockUntilShutdown();
    }

}
