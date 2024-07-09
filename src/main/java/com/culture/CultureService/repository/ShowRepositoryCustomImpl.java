package com.culture.CultureService.repository;

import com.culture.CultureService.dto.ShowSearchDto;
import com.culture.CultureService.entity.QShowEntity;
import com.culture.CultureService.entity.ShowEntity;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.thymeleaf.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Repository
public class ShowRepositoryCustomImpl implements ShowRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Autowired
    public ShowRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    // 검색 조건에 따른 BooleanExpression 메서드들
    private BooleanExpression searchAgeEq(String searchAge) {
        return StringUtils.isEmpty(searchAge) ? null : QShowEntity.showEntity.age.eq(searchAge);
    }

    private BooleanExpression searchGenreEq(String genre) {
        return StringUtils.isEmpty(genre) ? null : QShowEntity.showEntity.genre.eq(genre);
    }

    private BooleanExpression searchTicketPriceEq(String ticketPrice) {
        return StringUtils.isEmpty(ticketPrice) ? null : QShowEntity.showEntity.ticketPrice.eq(ticketPrice);
    }

    private BooleanExpression withinDateRange(String searchDateType) {
        if (searchDateType == null) {
            return null;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        Calendar now = Calendar.getInstance();
        Date startDate = now.getTime();
        Date endDate = now.getTime();

        try {
            switch (searchDateType) {
                case "1d":
                    now.add(Calendar.DAY_OF_MONTH, 1);
                    endDate = now.getTime();
                    break;
                case "1w":
                    now.add(Calendar.WEEK_OF_YEAR, 1);
                    endDate = now.getTime();
                    break;
                case "1m":
                    now.add(Calendar.MONTH, 1);
                    endDate = now.getTime();
                    break;
                case "6m":
                    now.add(Calendar.MONTH, 6);
                    endDate = now.getTime();
                    break;
                default:
                    return null;
            }

            String formattedEndDate = dateFormat.format(endDate);
            String formattedStartDate = dateFormat.format(startDate);
            return QShowEntity.showEntity.stDate.loe(formattedEndDate).and(QShowEntity.showEntity.edDate.goe(formattedStartDate));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery) {
        if (StringUtils.isEmpty(searchBy) || StringUtils.isEmpty(searchQuery)) {
            return null;
        }

        if ("title".equals(searchBy)) {
            return QShowEntity.showEntity.title.containsIgnoreCase(searchQuery);
        } else if ("placeName".equals(searchBy)) {
            return QShowEntity.showEntity.placeName.containsIgnoreCase(searchQuery);
        }

        return null;
    }

    @Override
    public Page<ShowEntity> getShowListPage(ShowSearchDto showSearchDto, Pageable pageable) {
        QShowEntity show = QShowEntity.showEntity;

        QueryResults<ShowEntity> results = queryFactory
                .selectFrom(show)
                .where(
                        searchGenreEq(showSearchDto.getSearchGenre()),
                        searchTicketPriceEq(showSearchDto.getSearchFee()),
                        withinDateRange(showSearchDto.getSearchPeriod()),
                        searchByLike(showSearchDto.getSearchBy(), showSearchDto.getSearchQuery())
                )
                .orderBy(show.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<ShowEntity> shows = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(shows, pageable, total);
    }
}
