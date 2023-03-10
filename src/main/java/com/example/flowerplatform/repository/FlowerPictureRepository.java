package com.example.flowerplatform.repository;

import com.example.flowerplatform.repository.entity.flowerPictures.FlowerPicture;
import com.example.flowerplatform.repository.entity.flowerPictures.FlowerPicturePrimaryKey;
import org.springframework.data.repository.CrudRepository;

public interface FlowerPictureRepository extends CrudRepository<FlowerPicture, FlowerPicturePrimaryKey>
{
}
