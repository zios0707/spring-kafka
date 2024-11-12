package com.springkafka.api;

import com.springkafka.api.handlers.VideoRouteHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.function.server.RouterFunction;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class VideoHandler {

    @Bean
    public RouterFunction<ServerResponse> videosEndPoint(VideoRouteHandler fileHandler) {

        return route()
                .nest(
                        path("/videos"), builder -> builder
                            //todo .GET("", /** 영상 리스트 service **/)
                                .nest(path("/{name}"), videoBuilder -> videoBuilder
                                        .GET("/", fileHandler::getVideo)
                                )
                ).build();
    }
}
