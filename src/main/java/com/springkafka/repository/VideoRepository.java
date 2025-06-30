package com.springkafka.repository;

import com.springkafka.model.Video;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface VideoRepository {
    Mono<Video> getVideoByName(String name);
    Flux<Video> getAllVideos();
    Mono<Video> saveVideo(Video video);
}
