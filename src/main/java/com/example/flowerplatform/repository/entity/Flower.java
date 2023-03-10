package com.example.flowerplatform.repository.entity;

import com.example.flowerplatform.repository.entity.flowerPictures.FlowerPicture;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.EAGER;

@Entity
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
@ToString
@Table(name="flower")
@Builder
public class Flower
{
    @Id
    @Column(name="flower_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long flowerId;

    @Column(name = "profile_pict_url")
    private final String profilePictUrl;

    @Column(name = "floriography")
    private final String floriography;

    @Column(name = "flower_name")
    private final String flowerName;

    @Column(name = "flower_story")
    private final String flowerStory;



    @OneToMany( mappedBy =  "flower", cascade = ALL, fetch = EAGER)
    private final List<FlowerPicture> flowerPictUrls;

}
