package com.culture.CultureService.dto;

import com.culture.CultureService.entity.PlaceEntity;
import lombok.*;
import org.modelmapper.ModelMapper;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PlaceDto {
    private Long id; //db 저장 시 아이디

    private String placeId; //시설 아이디
    private String name; //시설명
    private String feature; //시설 특성
    private String seat; //좌석수
    private String address; //주소
    private String url; //홈페이지 주소
    private String longitude; //경도
    private String latitude; //위도


    public PlaceDto(String placeId, String name, String feature,String seat,
                    String address, String url, String longitude, String latitude) {
        this.placeId = placeId;
        this.name = name;
        this.feature = feature;
        this.seat = seat;
        this.address = address;
        this.url = url;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    //Model Mapper
    private static ModelMapper modelMapper = new ModelMapper();

    public static PlaceDto of(PlaceEntity placeEntity) { //PlaceEntity 정보를 ShowDto에 띄워주는 메소드
        return modelMapper.map(placeEntity, PlaceDto.class);
    }
}
