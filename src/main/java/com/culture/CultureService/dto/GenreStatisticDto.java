package com.culture.CultureService.dto;

import com.culture.CultureService.entity.GenreStatisticEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GenreStatisticDto {
    private Long id;
    private String genre;
    private Integer performanceCount;
    private Integer showCount;
    private Integer reservationCount;
    private Integer cancellationCount;
    private Integer totalTicketSales;
    private Double totalTicketRevenue;

    public static GenreStatisticDto of(GenreStatisticEntity entity) {
        return new GenreStatisticDto(
                entity.getId(),
                entity.getGenre(),
                entity.getPerformanceCount(),
                entity.getShowCount(),
                entity.getReservationCount(),
                entity.getCancellationCount(),
                entity.getTotalTicketSales(),
                entity.getTotalTicketRevenue()
        );
    }
}
