package com.board.controller;

import com.board.domain.FishingSpot;
import com.board.repository.FishingSpotRepository;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/map")
@RequiredArgsConstructor
public class FishingSpotController {

  private final FishingSpotRepository repository;
  @Value("${image.secret}")
  private String imageClientId;
  @GetMapping("/fishing-spots")
  public List<FishingSpot> getAllSpots() {
    return repository.findAll();
  }

  @GetMapping("/spot-image")
  public ResponseEntity<Map<String, String>> getSpotImage() throws IOException {
    String requestUrl = "https://api.unsplash.com/search/photos?query=fishing&client_id=" + imageClientId;

    InputStream in = new URL(requestUrl).openStream();
    String json = new String(in.readAllBytes(), StandardCharsets.UTF_8);

    // 모든 "small": "https://..." 항목 추출
    Pattern pattern = Pattern.compile("\"small\"\\s*:\\s*\"(https:[^\"]+)\"");
    Matcher matcher = pattern.matcher(json);

    List<String> imageUrls = new ArrayList<>();
    while (matcher.find()) {
      imageUrls.add(matcher.group(1)); // 모든 URL 수집
    }
    // 랜덤으로 하나 선택
    if (!imageUrls.isEmpty()) {
      String randomUrl = imageUrls.get(new Random().nextInt(imageUrls.size()));
      Map<String, String> response = new HashMap<>();
      response.put("imageUrl", randomUrl);
      return ResponseEntity.ok(response);
    } else {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 또는 기본 이미지 URL 제공
    }

  }
}
