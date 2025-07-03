package com.board.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "fishing_spot")
@Data
public class FishingSpot {
  @Id
  private String id;

  @Field("낚시터명")
  private String name;

  @Field("WGS84위도")
  private double latitude;

  @Field("WGS84경도")
  private double longitude;

  @Field("source_file")
  private String sourceFile;
}
