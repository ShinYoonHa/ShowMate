package com.culture.CultureService.repository;

import com.culture.CultureService.dto.AwardSearchDto;
import com.culture.CultureService.entity.AwardEntity;
import com.culture.CultureService.entity.QAwardEntity;
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
public class AwardRepositoryCustomImpl implements AwardRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Autowired
    public AwardRepositoryCustomImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }
    private BooleanExpression searchGenreEq(String genre) {
        return StringUtils.isEmpty(genre) ? null : QAwardEntity.awardEntity.genre.eq(genre);
    }
    private BooleanExpression searchByEq(String searchBy, String searchQuery) {
        if (StringUtils.isEmpty(searchQuery)) {
            return null;
        }
        if ("title".equals(searchBy)) {
            return QAwardEntity.awardEntity.title.containsIgnoreCase(searchQuery);
        } else if ("awardName".equals(searchBy)) {
            return QAwardEntity.awardEntity.awardId.containsIgnoreCase(searchQuery); // 수정된 부분
        }
        return null;
    }



    @Override
    public Page<AwardEntity> getAwardListPage(AwardSearchDto awardSearchDto, Pageable pageable) {
        QAwardEntity award = QAwardEntity.awardEntity;

        QueryResults<AwardEntity> results = queryFactory
                .selectFrom(award)
                .where(
                        searchGenreEq(awardSearchDto.getSearchGenre()),
                        searchByEq(awardSearchDto.getSearchBy(), awardSearchDto.getSearchQuery())
                )
                .orderBy(award.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<AwardEntity> awards = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(awards, pageable, total);
    }


}
