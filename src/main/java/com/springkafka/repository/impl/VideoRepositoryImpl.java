package com.springkafka.repository.impl;

import com.springkafka.model.Video;
import com.springkafka.repository.VideoRepository;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class VideoRepositoryImpl implements VideoRepository {

    private final Map<String, Video> map = new ConcurrentHashMap<>();

    @Override
    public Mono<Video> getVideoByName(String name) {
        return Mono.create(sink -> {
            Video video = map.get(name);

            if (video != null) {
                sink.success(video);
            }else {
                sink.error(new RuntimeException("No video found with name " + name));
            }
        });
    }

    @Override
    public Flux<Video> getAllVideos() {
        synchronized (map) {
            return Flux.fromIterable(map.values());
        }
    }

    @Override
    public Mono<Video> saveVideo(Video video) {
        synchronized (map) {
            return Mono.fromCallable(() -> map.put(video.getName(), video));
        }
    }
}
