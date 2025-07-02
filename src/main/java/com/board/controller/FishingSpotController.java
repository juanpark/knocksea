package com.board.controller;

import com.board.domain.FishingSpot;
import com.board.repository.FishingSpotRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/map")
@RequiredArgsConstructor
public class FishingSpotController {

  private final FishingSpotRepository repository;

  @GetMapping("/fishing-spots")
  public List<FishingSpot> getAllSpots() {
    return repository.findAll();
  }
}
