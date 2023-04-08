package com.example.flowerplatform.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Builder
public class MessageFormat<T>{

    private final String message;

    private final T data;

    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "YYYY-MM-dd HH:mm:ss")
    @JsonSerialize(as = Date.class)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",shape = JsonFormat.Shape.STRING)
    private Date timestamp;

    private final int status;

}
