package com.example.flowerplatform.controller.dto.response;

import com.example.flowerplatform.repository.entity.Flower;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FlowerDto
{
    private long flowerId;

    private String flowerName;

    private String flowerProfileImgUrl;

    private String flowerFloriography;

    private String flowerStory;

    private List<String> flowerPicturesUrl;

    public FlowerDto(Flower flower)
    {
        this.flowerFloriography = flower.getFloriography();
        this.flowerId = flower.getFlowerId();
        this.flowerName = flower.getFlowerName();
        this.flowerProfileImgUrl = flower.getProfilePictUrl();
        this.flowerStory = flower.getFlowerStory();
        this.flowerPicturesUrl = flower
                .getFlowerPictUrls()
                .stream()
                .map(flowerPicture
                        -> flowerPicture.getPictUrl())
                .toList();
    }

}
