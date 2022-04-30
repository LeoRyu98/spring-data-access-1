package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 트랜잭션 매니저
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_1 {

//    private final DataSource dataSource;
    private final PlatformTransactionManager transactionManager;
    private final MemberRepositoryV3 memberRepository;

    /**
     * fromId 의 회원을 조회해서 toId 의 회원에게 money 만큼의 돈을 계좌이체 하는 트랜잭션 로직
     */
    public void accountTransfer(String fromId, String toId, int money) {
        // 트랜잭션 시작
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            // 비지니스 로직 수행
            bizLogic(fromId, toId, money);
            transactionManager.commit(status); // 성공시 커밋
        }catch (Exception e){
            transactionManager.rollback(status); // 실패시 롤백
            throw new IllegalStateException(e);
        }
    }

    /**
     * 비지니스 로직
     * 트랜잭션을 관리하는 로직과 실제 비즈니스 로직을 구분
     */
    private void bizLogic(String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money);
    }

    private void validation(Member toMember) {
        if(toMember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체중 예외 발생");
        }
    }

    /**
     * 커넥션 풀을 사용하기 때문에 close 시 커넥션이 종료되는 것이 아니라 풀에 반납된다.
     * 그러므로 수동 오토 커밋 모드를 자동 커밋 모드로 변경해야 한다.
     */
    private void release(Connection con) {
        if(con != null){
            try{
                con.setAutoCommit(true); // 커넥션 풀 고려
                con.close(); // 커넥션 풀에 반납
            } catch (Exception e){
                log.info("error", e);
            }
        }
    }
}
