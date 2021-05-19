import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class MainManager {
	public static void main(String args[]) throws IOException, SQLException {
		Scanner scan = new Scanner(System.in);
		FileManager fm = new FileManager();
		fm.readFile("C:\\Users\\gody8\\eclipse-workspace\\GoodPrice\\data\\원천조사가격정보_202104.csv");
		fm.readFile("C:\\Users\\gody8\\eclipse-workspace\\GoodPrice\\data\\원천조사가격정보_202103.csv");
		
		SqlManager sm = new SqlManager();
		Transaction[] ts = fm.getTransactions();
		sm.connect();
		sm.makeTables();
		sm.insertItem(ts);
		sm.insertMarket(ts);
		sm.insertSell(ts);
		
		System.out.println("GoodPrice - 농수축산물 정보 조회 및 추천 서비스");
		System.out.println("1. 키워드로 품종 검색");
		int ans = scan.nextInt();
		scan.nextLine();
		
		switch(ans) {
			case 1:
				System.out.print("키워드 입력 :");
				String keyword = scan.nextLine();
				sm.printItemByKeyword(keyword);
				
				System.out.print("\n세부 품목 검색 :");
				keyword = scan.nextLine();
				sm.printSpecificItemByKeyword(keyword);
				
				System.out.print("\n1. 지역 검색\t2. 종료  :");
				ans = scan.nextInt();
				scan.nextLine();
				
				if(ans == 1) {
					System.out.println("지역 입력 :");
					String place = scan.nextLine();
					System.out.println("'"+keyword+"' 에 대한 "+place+" 시장별 가격");
					sm.printPriceByPlace(keyword, place);

				}

				break;
			
		
		}
		
		
		sm.disconnect();
	}
}
