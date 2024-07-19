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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Repository
public class AwardRepositoryCustomImpl implements AwardRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    // 생성자를 통해 EntityManager를 주입받아 JPAQueryFactory를 초기화
    @Autowired
    public AwardRepositoryCustomImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }
    // 장르에 따라 검색 조건을 생성하는 메소드
    private BooleanExpression searchGenreEq(String genre) {
        // 입력받은 장르 문자열이 null이 아니면 해당 장르와 동일한 항목을 검색하는 조건 반환
        return StringUtils.isEmpty(genre) ? null : QAwardEntity.awardEntity.genre.eq(genre);
    }
    // 검색 유형과 검색 쿼리에 따라 검색 조건을 생성하는 메소드
    private BooleanExpression searchByEq(String searchBy, String searchQuery) {
        if (StringUtils.isEmpty(searchQuery)) {
            return null; // 검색 쿼리가 비어있으면 null 반환
        }
        if ("title".equals(searchBy)) {
            return QAwardEntity.awardEntity.title.containsIgnoreCase(searchQuery);
        } else if ("awardName".equals(searchBy)) {
            return QAwardEntity.awardEntity.awardId.containsIgnoreCase(searchQuery); // 수정된 부분
        }
        return null;
    }


    // 페이징 처리된 수상 정보 리스트 반환하는 메소드
    @Override
    public Page<AwardEntity> getAwardListPage(AwardSearchDto awardSearchDto, Pageable pageable) {
        QAwardEntity award = QAwardEntity.awardEntity;

        QueryResults<AwardEntity> results = queryFactory
                .selectFrom(award)
                .where(
                        searchGenreEq(awardSearchDto.getSearchGenre()),// 장르에따른 검색 조건
                        searchByEq(awardSearchDto.getSearchBy(), awardSearchDto.getSearchQuery()) // 검색 유형에 따른 검색 조건
                )
                .orderBy(award.id.desc()) // ID 기준 내림차순 정렬
                .offset(pageable.getOffset()) // 페이징 처리를 위한 오프셋 설정
                .limit(pageable.getPageSize()) // 페이징 처리를 위한 페이지 크기 설정
                .fetchResults(); // 결과 가져옴

        List<AwardEntity> awards = results.getResults(); // 조회된 데이터 리스트
        long total = results.getTotal(); // 전체 결과 수

        return new PageImpl<>(awards, pageable, total); // PageImpl 객체를 생성하여 반환
    }


}
