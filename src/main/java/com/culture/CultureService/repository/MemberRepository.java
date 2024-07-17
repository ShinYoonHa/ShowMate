package com.culture.CultureService.repository;


import com.culture.CultureService.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByEmail(String email);
    Member findByTel(String tel);
    Member findByNameAndTel(String name, String tel);

}
