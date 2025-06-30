package com.springkafka.api.handlers;

import com.springkafka.model.Video;
import com.springkafka.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class VideoRouteHandler {

    private final VideoService videoService;

    @Autowired
    public VideoRouteHandler(VideoService videoService) {
        this.videoService = videoService;
    }

    public Mono<ServerResponse> listVideos(ServerRequest request) {

        Flux<Video> videos = videoService.getAllVideos();

        Flux<VideoDetail> videoResourceMono = videos
                .map(video ->
                    new VideoDetail(
                            video.getName(),
                            request.uri() + "/" + video.getName()
                    )
                )
                .doOnError(t -> {
                    throw Exceptions.propagate(t);
                });

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .cacheControl(CacheControl.noCache())
                .location(request.uri())
                .body(videoResourceMono, VideoDetail.class);
    }

    public Mono<ServerResponse> getVideoPartial(ServerRequest request) {
        System.out.println("단편화 가져갈게요~");
        String filename = request.pathVariable("name");
        Mono<ResourceRegion> resourceRegionMono = videoService.getPartialVideoByName(filename, request);

        return resourceRegionMono.flatMap(resourceRegion ->
                ServerResponse
                        .status(HttpStatus.PARTIAL_CONTENT)
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                        .contentType(MediaType.valueOf("video/mp4"))
                        .contentLength(resourceRegion.getCount())
                        .headers(hs -> hs.setCacheControl(CacheControl.noCache()))
                        .body(resourceRegionMono, ResourceRegion.class)
        );
    }

    public Mono<ServerResponse> getFullContent(ServerRequest request) {

        String filename = request.pathVariable("name");

        Mono<UrlResource> videoUrlResource = videoService.getResourceByName(filename);

        return videoUrlResource
                .flatMap(urlResource -> {
                    long contentLength = videoService.lengthOf(urlResource);

                    return ServerResponse
                            .ok()
                            .contentLength(contentLength)
                            .headers(hs -> hs.setCacheControl(CacheControl.noCache()))
                            .bodyValue(urlResource);
                });
    }

    private record VideoDetail(String name, String link) { }
}
