package com.example.apidressing.model;

import com.example.apidressing.gen.model.ClothingLayer;
import com.example.apidressing.gen.model.ItemStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "clothing_item")
@Getter
@Setter
@NoArgsConstructor
public class ClothingItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    private String name;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private ClothingLayer layer;

    private Integer warmthLevel;

    private Boolean waterproof;

    private Boolean windproof;

    @Enumerated(EnumType.STRING)
    private ItemStatus status;
}
