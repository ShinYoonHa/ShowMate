package com.culture.CultureService.repository;

import com.culture.CultureService.dto.PlaceSearchDto;
import com.culture.CultureService.entity.PlaceEntity;
import com.culture.CultureService.entity.QPlaceEntity;
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

import java.util.List;

@Repository
public class PlaceRepositoryCustomImpl implements PlaceRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Autowired
    public PlaceRepositoryCustomImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }

    private BooleanExpression searchByEq(String searchBy, String searchQuery) {
        if (StringUtils.isEmpty(searchQuery)) {
            return null;
        }
        if ("name".equals(searchBy)) {
            return QPlaceEntity.placeEntity.name.containsIgnoreCase(searchQuery);
        } else if ("awardName".equals(searchBy)) {
            return QPlaceEntity.placeEntity.placeId.containsIgnoreCase(searchQuery);
        }
        return null;
    }

    @Override
    public Page<PlaceEntity> getPlaceListPage(PlaceSearchDto placeSearchDto, Pageable pageable) {
        QPlaceEntity place = QPlaceEntity.placeEntity;

        QueryResults<PlaceEntity> results = queryFactory
                .selectFrom(place)
                .where(
                        searchByEq(placeSearchDto.getSearchBy(), placeSearchDto.getSearchQuery())
                )
                .orderBy(place.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<PlaceEntity> places = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(places, pageable, total);
    }
}
