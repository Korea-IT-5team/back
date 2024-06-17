package com.project.back.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name="favoriteRestaurant")
@Table(name="Favorite_Restaurant")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteRestaurantEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer favoriteNumber;
    private String favoriteUserId; 
    private Integer favoriteRestaurantId;

    public FavoriteRestaurantEntity(String userEmailId, int restaurantId) {
        this.favoriteUserId = userEmailId;
        this.favoriteRestaurantId = restaurantId;
    }
}