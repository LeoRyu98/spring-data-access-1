package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * JDBC - ConnectionParam
 */
@Slf4j
public class MemberRepositoryV2 {

    // DataSource 의존관계 주입
   private final DataSource dataSource;

    public MemberRepositoryV2(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Member save(Member member) throws SQLException {
        // 데이터베이스에 전달할 SQL 정의
        String sql = "insert into member(member_id, money) values (?, ?)";

        // finally 안 에서 사용할 수 있도록 미리 객체 선언
        // PreparedStatement 를 통해 파라미터 바인딩 방식을 사용 (Statement 의 자식 타입)
        Connection con = null;
        PreparedStatement pstmt = null;

        try{
            // 이전에 만들어둔 DBConnectionUtil 를 통해서 데이터베이스 커넥션을 획득
            con = getConnection();
            // 데이터베이스에 전달할 SQL 과 파라미터로 전달할 데이터들을 준비
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            // Statement 를 통해 준비된 SQL 을 커넥션을 통해 실제 데이터베이스에 전달
            // 영향받은 DB row 수(int)를 반환 가능
            pstmt.executeUpdate();
            return member;
        }catch (SQLException e){
            log.error("db error", e);
            throw e;
        }finally {
            // 쿼리를 실행하고 리소스 정리 (역순으로 정리)
            close(con, pstmt, null);
        }
    }

    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            // 데이터를 변경할 때 : executeUpdate()
            // 데이터를 조회할 때 : executeQuery()
            // 조회 결과를 ResultSet 에 담는다.
            rs = pstmt.executeQuery();
            // ResultSet 내부에 있는 커서(cursor)를 이동해서 다음 데이터를 조회
            // rs.next() 의 결과가 true 면 데이터 존재, false 면 더 이상 커서가 가리키는 데이터 존재 X
            if(rs.next()) { // 여러 개의 데이터를 조회할 경우 while 문 사용
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            }else {
                throw new NoSuchElementException("member not found memberId="+ memberId);
            }

        } catch (SQLException e){
            log.error("db error", e);
            throw e;
        }finally {
            close(con, pstmt, rs);
        }
    }

    public Member findById(Connection con, String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery();

            if(rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            }else {
                throw new NoSuchElementException("member not found memberId="+ memberId);
            }

        } catch (SQLException e){
            log.error("db error", e);
            throw e;
        }finally {
            // Connection 은 여기서 닫지 않는다.
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(pstmt);
        }
    }

    public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money=? where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);
        }catch (SQLException e){
            log.error("db error", e);
            throw e;
        }finally {
            close(con, pstmt, null);
        }
    }

    public void update(Connection con, String memberId, int money) throws SQLException {
        String sql = "update member set money=? where member_id=?";

        PreparedStatement pstmt = null;

        try{
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            pstmt.executeUpdate();

        }catch (SQLException e){
            log.error("db error", e);
            throw e;
        }finally {
            // Connection 은 여기서 닫지 않는다.
            JdbcUtils.closeStatement(pstmt);
        }
    }

    public void delete(String memberId) throws SQLException {
        String sql= "delete from member where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            pstmt.executeUpdate();

        }catch (SQLException e){
            log.error("db error", e);
            throw e;
        }finally {
            close(con, pstmt, null);
        }
    }

    /**
     * 스프링은 JDBC 를 편리하게 다룰 수 있는 JdbcUtils 라는 편의 메서드를 제공
     */
    private void close(Connection con, Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(con);
    }

    private Connection getConnection() throws SQLException {
        Connection con = dataSource.getConnection();
        log.info("get connection={}, class={}", con, con.getClass());
        return con;
    }
}

