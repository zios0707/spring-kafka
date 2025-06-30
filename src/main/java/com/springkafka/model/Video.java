package com.springkafka.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.nio.file.Path;
import lombok.Data;

@Data
public class Video {

    private String name;

    private String uri;

    @JsonIgnore
    private Path location;
}
