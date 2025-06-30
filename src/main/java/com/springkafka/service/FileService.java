package com.springkafka.service;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.util.stream.BaseStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;

import java.nio.file.Path;
import java.nio.file.Paths;


@Service
@Slf4j
public class FileService {

    @Value("${video.location}")
    private String videoLocation;

    public Flux<Path> getAllFiles() {
        return fromPath(Paths.get(videoLocation));
    }

    private Flux<Path> fromPath(Path path) {
        return Flux.using(() -> Files.walk(path, FileVisitOption.FOLLOW_LINKS),
                        Flux::fromStream,
                        BaseStream::close)
                .doOnDiscard(BaseStream.class, BaseStream::close)
                .doOnError(err -> {
                    throw Exceptions.propagate(
                            new RuntimeException("FUCK")
                    );
                })
                .filter(filePath -> !filePath.toFile().isDirectory())
                .filter(filePath -> !filePath.getFileName().toString().startsWith("."));
    }
}
