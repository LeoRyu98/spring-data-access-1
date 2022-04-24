package hello.jdbc.domain;

import lombok.Data;

/**
 * member 테이블에 데이터를 저장하고 조회할 때 사용
 */
@Data
public class Member {

    private String memberId;
    private int money;

    public Member() {
    }

    public Member(String memberId, int money) {
        this.memberId = memberId;
        this.money = money;
    }
}
