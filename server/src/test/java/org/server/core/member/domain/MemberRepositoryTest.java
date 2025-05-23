package org.server.core.member.domain;

import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.server.core.member.exception.MemberException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    private Member testMember;

    @BeforeEach
    void setUp() {
        testMember = new Member("testOAuthId", "testProfileUrl", "testName");
    }

    @Test
    @DisplayName("회원 객체 저장 테스트")
    void t001() {
        //given
        memberRepository.save(testMember);

        //when
        Optional<Member> maybeMember = memberRepository.findById(1L);

        //then
        Assertions.assertThat(maybeMember.isPresent()).isTrue();
        Assertions.assertThat(maybeMember.get().getNickname()).isEqualTo("testName");
    }

    @Test
    void t002() {
        Assertions.assertThatCode(() ->
                OAuthProvider.getProvider("github")
        ).doesNotThrowAnyException();

        Assertions.assertThatThrownBy(() ->
                OAuthProvider.getProvider("test")
        ).isInstanceOf(MemberException.class);
    }
}