package com.example.flowerplatform.repository;

import com.example.flowerplatform.repository.entity.Flower;

import org.springframework.data.repository.CrudRepository;

public interface FlowerRepository extends CrudRepository<Flower, Long>
{

}
