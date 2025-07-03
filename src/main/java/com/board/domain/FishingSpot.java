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

  @Field("소재지도로명주소")
  private String address;

  @Field("시/도")
  private String city;

  @Field("구")
  private String gu;

  @Field("동")
  private String dong;

  @Field("주요어종")
  private String fishType;

  @Field("이용요금")
  private String fee;

  @Field("데이터기준일자")
  private String updatedAt;
}
