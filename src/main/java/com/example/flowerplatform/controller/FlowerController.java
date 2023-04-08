package com.example.flowerplatform.controller;

import com.example.flowerplatform.controller.dto.response.FlowerDto;
import com.example.flowerplatform.dto.MessageFormat;
import com.example.flowerplatform.repository.entity.Flower;
import com.example.flowerplatform.service.FlowerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/flowers")
@RequiredArgsConstructor
@Slf4j
public class FlowerController
{
    private final FlowerService flowerService;

    @GetMapping
    public ResponseEntity<MessageFormat> recommendFlowers()
    {
        Iterable<Flower> tempFlowers = flowerService.getFlowers();

        List<FlowerDto> flowers = new ArrayList<>();

        tempFlowers.forEach(flower -> {
            flowers.add(new FlowerDto(flower));
        });

        MessageFormat messageToReturn = MessageFormat.<List<FlowerDto>>builder()
                .message("flower recommendation succeeded")
                .data(flowers)
                .status(HttpStatus.OK.value())
                .timestamp(new Date())
                .build();

        return ResponseEntity.ok(messageToReturn);

    }
}
