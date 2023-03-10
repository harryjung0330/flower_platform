package com.example.flowerplatform.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "YYYY-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;

    private final int status;

}
