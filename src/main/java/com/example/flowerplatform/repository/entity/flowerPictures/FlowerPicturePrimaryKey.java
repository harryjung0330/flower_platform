package com.example.flowerplatform.repository.entity.flowerPictures;

import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
public class FlowerPicturePrimaryKey implements Serializable {
    private String pictUrl;

    private long flowerId;
}
