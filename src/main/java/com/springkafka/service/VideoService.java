package com.springkafka.service;

import com.springkafka.model.Video;
import com.springkafka.repository.VideoRepository;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class VideoService {
    private final long BYTE_LENGTH = 1024;
    private final long CHUNK_SIZE_VERY_LOW = BYTE_LENGTH * 256;
    private final long CHUNK_SIZE_LOW = BYTE_LENGTH * 512;
    private final long CHUNK_SIZE_MID = BYTE_LENGTH * 1024;
    private final long CHUNK_SIZE_HIGH = BYTE_LENGTH * 2048;
    private final long CHUNK_SIZE_VERY_HIGH = CHUNK_SIZE_HIGH * 2;

    private final VideoRepository videoRepository;

    public Mono<ResourceRegion> getPartialVideoByName(String name, ServerRequest request) {
        HttpHeaders headers = request.headers().asHttpHeaders();
        HttpRange range = (!headers.getRange().isEmpty()) ? headers.getRange().get(0) : null;

        System.out.println(range);

        AtomicInteger size = new AtomicInteger();

        request.queryParam("partial").ifPresent(val ->
                size.set(Integer.parseInt(val)));

        long chunk = getChunkSize(size.get());

        Mono<UrlResource> resource = getResourceByName(name);

        return resource.map(urlResource -> {
            long contentLength = lengthOf(urlResource);

            if (range != null) {
                long start = range.getRangeStart(contentLength);
                long end = range.getRangeEnd(contentLength);

                long resourceLen = end - start;
                long rangeLen = Math.min(chunk, resourceLen);

                return new ResourceRegion(urlResource, start, rangeLen);
            }else {
                return new ResourceRegion(urlResource, 0, contentLength);
            }
        }).doOnError(e -> {
            throw Exceptions.propagate(e);
        });
    }

    public Mono<UrlResource> getResourceByName(String name) {
        return videoRepository.getVideoByName(name)
                .flatMap(this::createUriResourceFromVideo);
    }

    private Mono<UrlResource> createUriResourceFromVideo(Video videoObj) {
        return Mono.<UrlResource>create(monoSink -> {
            try {
                UrlResource video = new UrlResource(videoObj.getLocation().toUri());
                monoSink.success(video);
            } catch(MalformedURLException e) {
                monoSink.error(e);
            }
        }).doOnError(e -> {
            throw Exceptions.propagate(e);
        });
    }

    public Flux<Video> getAllVideos() {
        return videoRepository.getAllVideos();
    }

    public long lengthOf(UrlResource urlResource) {
        long fileLen;

        try {
            fileLen = urlResource.contentLength();
        } catch (IOException e) {
            throw Exceptions.propagate(e); // 오류가 나는 경우는 404
        }

        return fileLen;
    }

    public long getChunkSize(int size) {
        return switch (size) {
            case 1 -> CHUNK_SIZE_VERY_LOW;
            case 2 -> CHUNK_SIZE_LOW;
            case 4 -> CHUNK_SIZE_HIGH;
            case 5 -> CHUNK_SIZE_VERY_HIGH;
            default -> CHUNK_SIZE_MID;
        };
    }
}
