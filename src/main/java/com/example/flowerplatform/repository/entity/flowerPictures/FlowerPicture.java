package com.example.flowerplatform.repository.entity.flowerPictures;


import com.example.flowerplatform.repository.entity.Flower;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
@ToString
@Table(name="flower_pictures")
@Builder
@IdClass(FlowerPicturePrimaryKey.class)
public class FlowerPicture
{
    @Id
    @Column(name="flower_id")
    private final Long flowerId;

    @Id
    @Column(name = "pict_url")
    private final String pictUrl;

    @Column(name = "uploaded_at" ,insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private final Date uploadedAt;


    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flower_id", updatable = false, insertable = false)
    private Flower flower;



}
