package com.example.flowerplatform.service.implementation;

import com.example.flowerplatform.repository.FlowerRepository;
import com.example.flowerplatform.repository.entity.Flower;
import com.example.flowerplatform.service.FlowerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@RequiredArgsConstructor
public class FlowerServiceImpl implements FlowerService
{
    private final FlowerRepository flowerRepository;


    @Transactional
    public Iterable<Flower> getFlowers()
    {
        return flowerRepository.findAll();
    }


}
