package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import dto.FishDTO;

// fishdata table CRUD
public class FishDAO {
	
	private String username = "root";
	private String password = "11111111";
	private String url = "jdbc:oracle:thin:@localhost:1521:orcl";
	private String driverName = "oracle.jdbc.driver.OracleDriver";
	private Connection conn = null;		// 커넥션 자원 변수
	public static FishDAO fishdao = null;		// 자기자신의 객체 주소 변수(public static)
	
	// 싱글톤 기법을 이용하여 FishDAO객체는 자기자신만이 만들수 있도록 설계
	private FishDAO(){		// 생성자의 접근제어자는 private로 하여 클래스 내에서만 사용 가능하도록
		init();
	}
	
	public static FishDAO getInstance() {	// 이 메소드를 통하여 클래스내에서 생성된 객체의 주소를 다른 클래스에 접근하여 사용가능하도록 함
		if(fishdao == null) {
			fishdao = new FishDAO();
		}
		return fishdao;
	}
	
	private void init() {		// 드라이버 로드
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			System.out.println("오라클 드라이버 로드 성공");	// 빌드가 정확하게 됐을 때 이 문구가 출력될 것임.
			// 이 문구가 제대로 출력된다면, 오라클사에서 배포한 라이브러리를 사용할 준비가 완료된것을 의미함.
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private boolean conn() {		// 커넥션 가져오는 공통 코드를 메서드로 정의
		try {
			conn = DriverManager.getConnection(url/*포트넘버 1521*/,"system"/*아이디*/,password /*비밀번호*/);
			System.out.println("커넥션 자원 획득 성공");
			return true;		// 커넥션 자원을 정상적으로 획득 할 경우
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;	// 커넥션 자원을 획득하지 못한 경우
	}
	
	public ArrayList<FishDTO> selectAll(){	// selectAll입장에서는 select를 통하여 받아온 결과값 내용에 대해서는 알필요없음(데이터 가져오기 담당)
		// select를 통하여 받아온 튜플의 값을 ArrayList의 형태로 메소드를 호출한 곳으로 보내주기 위하여, 리턴타입 설정
		ArrayList<FishDTO> flist = new ArrayList<>();
		if(conn()) {
			try {
				String sql = "select * from fishdata";
				PreparedStatement psmt = conn.prepareStatement(sql);
				ResultSet rs = psmt.executeQuery();
				// Resultset은 테이블 형식으로 가져온다고 이해함.
				while(rs.next()) {	// next()메서드는 rs에서 참조하는 테이블에서 튜플을 순차적으로 하나씩 접근하는 메서드
					FishDTO fishTemp = new FishDTO();
					// rs.getString("id") rs가 접근한 튜플에서 id컬럼의 값을 가져옴
					fishTemp.setId(rs.getString("id"));
					fishTemp.setPwd(rs.getString("pwd"));
					fishTemp.setIndate(rs.getString("indate"));
					flist.add(fishTemp);
				}
				return flist;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}finally {	// finally에 conn자원 반납코드 추가
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else {
			System.out.println("데이터베이스 커넥션 실패");
		}
		return null;
	}
	
	public FishDTO selectOne(String findId){
		if(conn()) {
			try {
				String sql = "select * from fishdata where id = ? ";
				PreparedStatement psmt = conn.prepareStatement(sql);
				psmt.setString(1, findId);
				ResultSet rs = psmt.executeQuery();
				
				if(rs.next()) {	// 쿼리 결과가 튜플 하나일 경우는 이렇게 해도됨, while도 가능함
					FishDTO fishTemp = new FishDTO();
					// rs.getString("id") rs가 접근한 튜플에서 id컬럼의 값을 가져옴
					fishTemp.setId(rs.getString("id"));
					fishTemp.setPwd(rs.getString("pwd"));
					fishTemp.setIndate(rs.getString("indate"));
					return fishTemp;
				}
			} catch (SQLException e) {	
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {	// finally에 conn자원 반납코드 추가
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}			
		}else {
			System.out.println("데이터베이스 커넥션 실패");
		}
		return null;
	}
	
	public void delete(String delId) {
		if(conn()) {
			try {
				// 쿼리 작성
				String sql = "delete from fishdata where id = ?";
				PreparedStatement psmt = conn.prepareStatement(sql);
				// mapping
				psmt.setString(1, delId);
				int resultInt = psmt.executeUpdate();
				if(resultInt>0) {	// '트랜잭션'에 대한 처리 정의
					conn.commit();	// 현재작업 수행 -> 영구적 저장
					System.out.println("해당 삭제 저장완료");
				}else {
					conn.rollback();	// 현재 작업 취소
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else {
			System.out.println("데이터베이스 커넥션 실패");
		}
	}
	
	public void update(FishDTO fdto) {
		if(conn()) {
			try {
				String sql = "update fishdate set pwd = ? where id = ?";
				PreparedStatement psmt = conn.prepareStatement(sql);
				// mapping
				psmt.setString(1, fdto.getPwd());
				psmt.setString(2, fdto.getId());
				int resultInt = psmt.executeUpdate();
				if(resultInt>0) {	// '트랜잭션'에 대한 처리 정의
					conn.commit();	// 현재작업 수행 -> 영구적 저장
					System.out.println("해당 수정 저장완료");
				}else {
					conn.rollback();	// 현재 작업 취소
				}
			} catch (Exception e) {
				// TODO: handle exception
			}finally {
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else {
			System.out.println("데이터베이스 커넥션 실패");
		}
	}
	
	public void add(FishDTO fdto) {		// CRUD - insert 담당의 메서드 / mapping하기 위해 매개변수 받아옴
		if(conn()) {
			try {
				// 쿼리 작성
				String sql = "insert into fishdata values(?,?,default)";
				PreparedStatement psmt = conn.prepareStatement(sql);
				// mapping
				psmt.setString(1, fdto.getId());
				psmt.setString(2, fdto.getPwd());
				// 쿼리 실행
//				psmt.executeUpdate();
				// 쿼리 실행 리턴받아서 다음 처리 작업을 정의하겠다.
				int resultInt = psmt.executeUpdate();
				if(resultInt>0) {	// '트랜잭션'에 대한 처리 정의
					conn.commit();	// 현재작업 수행 -> 영구적 저장
					System.out.println("해당 내역 저장완료");
				}else {
					conn.rollback();	// 현재 작업 취소
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			System.out.println("데이터베이스 커넥션 실패");
		}
		
		
	}
	
}
