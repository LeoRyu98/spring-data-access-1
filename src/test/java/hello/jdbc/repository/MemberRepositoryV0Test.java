package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
class MemberRepositoryV0Test {
    
    MemberRepositoryV0 repository = new MemberRepositoryV0();
    
    @Test
    public void crud() throws Exception {
        // save
        Member member = new Member("memberV2", 10000);
        repository.save(member);

        //findById
        Member findMember = repository.findById(member.getMemberId());
        log.info("findMember={}",findMember);
        log.info("member == findMember {}", member == findMember);
        log.info("member equals findMember {}", member.equals(findMember));
        Assertions.assertThat(findMember).isEqualTo(member);
    }
}