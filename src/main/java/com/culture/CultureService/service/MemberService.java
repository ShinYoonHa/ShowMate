package com.culture.CultureService.service;


import com.culture.CultureService.dto.MemberFormDto;
import com.culture.CultureService.entity.Member;
import com.culture.CultureService.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    public static final Logger logger = LoggerFactory.getLogger(MemberService.class);

    public Member saveMember(Member member) {
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }

    private void validateDuplicateMember(Member member) {
        Member findMember = memberRepository.findByEmail(member.getEmail());
        if (findMember != null) {
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
        findMember = memberRepository.findByTel(member.getTel());
        if (findMember != null) {
            throw new IllegalStateException("이미 가입된 전화번호입니다.");
        }
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public void updateMember(String email, MemberFormDto memberFormDto) {
        Member member = memberRepository.findByEmail(email);
        if (member != null) {
            member.setName(memberFormDto.getName());
            member.setAddress(memberFormDto.getAddress());
            member.setTel(memberFormDto.getTel());
            memberRepository.save(member);
        } else {
            throw new IllegalStateException("Member not found");
        }
    }

    public boolean deleteMember(String email, String password) {
        Member member = memberRepository.findByEmail(email);
        if (member != null && passwordEncoder.matches(password, member.getPassword())) {
            memberRepository.delete(member);
            return true;
        }
        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new UsernameNotFoundException(email);
        }
        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .build();
    }

    public String findEmailByNameAndTel(String name, String tel) {
        Member member = memberRepository.findByNameAndTel(name, tel);
        return (member != null) ? member.getEmail() : null;
    }
    public String resetPassword(String email) throws Exception {
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new Exception("회원이 존재하지 않습니다.");
        }
        String tempPassword = generateTempPassword();
        member.setPassword(passwordEncoder.encode(tempPassword));
        memberRepository.save(member);
        return tempPassword;
    }

    private String generateTempPassword() {
        StringBuffer key = new StringBuffer();
        Random r = new Random();

        for (int i = 0; i < 8; i++) {
            int idx = r.nextInt(3);
            switch (idx) {
                case 0:
                    key.append((char) (r.nextInt(26) + 97));
                    break;
                case 1:
                    key.append((char) (r.nextInt(26) + 65));
                    break;
                case 2:
                    key.append(r.nextInt(10));
                    break;
            }
        }
        return key.toString();
    }
}