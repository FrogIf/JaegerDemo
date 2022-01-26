package sch.frog.jaeger.server;

import io.jaegertracing.api_v2.Model;

public interface IMessageHandler {

    void handle(Model.Batch batch);
    
}
