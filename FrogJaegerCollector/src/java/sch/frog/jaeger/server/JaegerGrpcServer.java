package sch.frog.jaeger.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.ClientAuth;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslProvider;
import io.grpc.stub.StreamObserver;
import io.jaegertracing.api_v2.Collector;
import io.jaegertracing.api_v2.CollectorServiceGrpc;
import io.jaegertracing.api_v2.Model;
import sch.frog.jaeger.config.ConfigHolder;

public class JaegerGrpcServer {

    private static final Logger logger = LoggerFactory.getLogger(JaegerGrpcServer.class);

    private Server server;

    public void start(int port, List<IMessageHandler> handlers) throws IOException{
        boolean isTlsEnable = "true".equals(ConfigHolder.getProperty("collector.grpc.tls.enabled", "false"));
        if(isTlsEnable){
            String tlsKeyPath = ConfigHolder.getProperty("collector.grpc.tls.key");
            String certPath = ConfigHolder.getProperty("collector.grpc.tls.cert");
            String trustCertPath = ConfigHolder.getProperty("collector.grpc.tls.client-ca");
            server = NettyServerBuilder.forPort(port)
                    .addService(new CustomCollectorService(handlers))
                    .sslContext(getSslContextBuilder(certPath, tlsKeyPath, trustCertPath).build())
                    .build().start();
        }else{
            server = ServerBuilder.forPort(port)
                    .addService(new CustomCollectorService(handlers))
                    .build()
                    .start();
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                logger.info("shutting down gRPC server since JVM is shutting down.");
                JaegerGrpcServer.this.stop();
                logger.info("server shut down.");
            }
        });
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    private SslContextBuilder getSslContextBuilder(String certChainFilePath, String privateKeyFilePath, String trustCertCollectionFilePath) {
        SslContextBuilder sslClientContextBuilder = SslContextBuilder.forServer(new File(certChainFilePath),
                new File(privateKeyFilePath));
        if (trustCertCollectionFilePath != null) {
            sslClientContextBuilder.trustManager(new File(trustCertCollectionFilePath));
            sslClientContextBuilder.clientAuth(ClientAuth.REQUIRE);
        }
        return GrpcSslContexts.configure(sslClientContextBuilder,
                SslProvider.OPENSSL);
    }

    private static class CustomCollectorService extends CollectorServiceGrpc.CollectorServiceImplBase {
        private final ArrayList<IMessageHandler> handlers;

        public CustomCollectorService(List<IMessageHandler> handlers) {
            this.handlers = new ArrayList<>(handlers);
        }

        @Override
        public void postSpans(Collector.PostSpansRequest request, StreamObserver<Collector.PostSpansResponse> responseObserver) {
            logger.debug("receive data.");
            Model.Batch batch = request.getBatch();
            if(batch != null){
                for(IMessageHandler handler : handlers){
                    handler.handle(batch);
                }
            }else{
                logger.warn("batch is null.");
            }
            responseObserver.onNext(Collector.PostSpansResponse.newBuilder().build());
            responseObserver.onCompleted();
        }
    }
    
}
