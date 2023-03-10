package com.example.flowerplatform.service;

import com.example.flowerplatform.repository.entity.Flower;

public interface FlowerService {

    public Iterable<Flower> getFlowers();
}
