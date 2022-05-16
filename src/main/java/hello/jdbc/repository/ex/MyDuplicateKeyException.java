package hello.jdbc.repository.ex;

/**
 * 기존에 사용했던 MyDbException 을 상속받아서 의미있는 계층을 형성
 * 데이터베이스 관련 예외라는 계층을 만들 수 있다.
 * 데이터가 중복일 경우 사용하는 언체크 예외
 */
public class MyDuplicateKeyException extends MyDbException {

    public MyDuplicateKeyException() {
    }

    public MyDuplicateKeyException(String message) {
        super(message);
    }

    public MyDuplicateKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyDuplicateKeyException(Throwable cause) {
        super(cause);
    }
}
