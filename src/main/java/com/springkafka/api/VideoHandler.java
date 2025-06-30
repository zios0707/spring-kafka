package com.springkafka.api;

import com.springkafka.api.handlers.VideoRouteHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RequestPredicates;
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
                                .GET("", fileHandler::listVideos)
                                .nest(path("/{name}"), videoBuilder -> videoBuilder
                                        .GET("", param("partial"), fileHandler::getVideoPartial)
                                        .GET("", fileHandler::getFullContent)

                                )
                ).build();
    }

    private static RequestPredicate param(String param) {
        return RequestPredicates.all().and(request -> request.queryParam(param).isPresent());
    }
}
