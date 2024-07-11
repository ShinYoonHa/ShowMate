package com.culture.CultureService.repository;

import com.culture.CultureService.entity.GenreStatisticEntity;
import com.culture.CultureService.entity.QGenreStatisticEntity;
import com.culture.CultureService.entity.QShowEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.thymeleaf.util.StringUtils;

import java.util.List;
@Primary
@Repository
public class GenreStatisticRepositoryCustomImpl implements GenreStatisticRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Autowired
    public GenreStatisticRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    private BooleanExpression searchGenreEq(String searchGenre) {
        if(StringUtils.isEmpty(searchGenre)) {
            return null;
        }
        switch (searchGenre) {
            case "theater":
                return QShowEntity.showEntity.genre.eq("연극");
            case "musical":
                return QShowEntity.showEntity.genre.eq("뮤지컬");
            case "classic":
                return QShowEntity.showEntity.genre.eq("서양음악(클래식)");
            case "kmusic":
                return QShowEntity.showEntity.genre.eq("한국음악(국악)");
            case "popularmusic":
                return QShowEntity.showEntity.genre.eq("대중음악");
            case "dance":
                return QShowEntity.showEntity.genre.eq("무용");
            case "populardance":
                return QShowEntity.showEntity.genre.eq("대중무용");
            case "circus/magic":
                return QShowEntity.showEntity.genre.eq("서커스/마술");
            case "complex":
                return QShowEntity.showEntity.genre.eq("복합");
            case "kid":
                return QShowEntity.showEntity.genre.eq("아동");
            default:
                return null;

        }
    }

    /*
    GenreStatisticRepositoryCustom 인터페이스의 메서드를 구현
    입력 파라미터로 String genre 를 받아 특정 장르에 대한 통계 데이터를 검색
    반환타입은 List<GenreStatisticEntity> 로, 검색된 통계 데이터를 리스트로 반환
     */
    @Override
    public List<GenreStatisticEntity> searchGenreStatistics(String genre) {
        //Q객체 초기화
        QGenreStatisticEntity genreStatisticEntity = QGenreStatisticEntity.genreStatisticEntity;
        //queryFactory 는 genreStatisticEntity 테이블로부터 데이터 선택하는 쿼리 시작
        return queryFactory.selectFrom(genreStatisticEntity)
                //searchGenreEq 메서드 호출 후 특정 장르에 대한 조건 설정
                //searchGenreEq(genre) 메서드는 주어진 장르에 해당하는 조건 반환, 이 조건은 쿼리의 Where 절에 추가
                .where(searchGenreEq(genre))
                //쿼리 실행후 결과를 리스트 형태로 반환
                .fetch();
    }

}
