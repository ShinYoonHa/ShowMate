package com.culture.CultureService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@NoArgsConstructor  // 기본 생성자 자동 생성
@AllArgsConstructor
@Data
public class GenreStatisticEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String genre; // 장르
    private Integer performanceCount; // 공연건수
    private Integer showCount; // 상연횟수
    private Integer reservationCount; // 예매수
    private Integer cancellationCount; // 취소수
    private Integer totalTicketSales; // 총 티켓판매수
    private Double totalTicketRevenue; // 총 티켓판매액
}
