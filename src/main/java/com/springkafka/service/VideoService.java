package com.springkafka.service;

import org.springframework.core.io.support.ResourceRegion;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

@Service
public class VideoService {

    public Mono<ResourceRegion> getPartialVideoByName(String name, ServerRequest request) {
        // TODO 로직 생성

        return Mono.empty();
    }
}
