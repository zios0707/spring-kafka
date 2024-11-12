package com.springkafka.api.handlers;

import com.springkafka.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class VideoRouteHandler {

    private final VideoService videoService;

    public Mono<ServerResponse> getVideo(ServerRequest request) {
        String filename = request.pathVariable("name");
        Mono<ResourceRegion> regionMono = videoService.getPartialVideoByName(filename, request);

        return ServerResponse
                .status(HttpStatus.PARTIAL_CONTENT)
                .contentLength(0)
                .body(regionMono, ResourceRegion.class);
    }
}
