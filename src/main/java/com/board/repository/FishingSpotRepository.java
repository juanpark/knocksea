package com.board.repository;

import com.board.domain.FishingSpot;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FishingSpotRepository extends MongoRepository<FishingSpot, String> {

}
