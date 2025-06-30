package com.springkafka.config;

import com.springkafka.model.Video;
import com.springkafka.repository.VideoRepository;
import com.springkafka.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Bootstrap implements CommandLineRunner {

    private final VideoRepository videoRepository;
    private final FileService fileService;

    @Override
    public void run(String... args)  {
        fileService.getAllFiles()
                .doOnNext(path -> log.info("found file in |{}| and name is |{}|", path.toUri(), path.getFileName()))
                .flatMap(path -> {
                    Video video = new Video();
                    video.setLocation(path);
                    video.setName(path.getFileName().toString());
                    return videoRepository.saveVideo(video);
                })
                .subscribe();

        videoRepository.getAllVideos()
                .doOnNext(video ->
                    System.out.println("video saved / " + video.getName())
                )
                .subscribe();
    }
}
