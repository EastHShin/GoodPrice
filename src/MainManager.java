import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class MainManager {
	public static boolean checkInput(Scanner scan) {
		System.out.print("Continue? (Enter 1 for continue) : ");
		String input = scan.nextLine();
		
        if(input.compareTo("1") != 0) {
        	System.out.println("Program exit");
        	return false;
        }
        return true;
	}
	
	public static void main(String args[]) throws IOException, SQLException {
		Scanner scan = new Scanner(System.in);
		FileManager fm = new FileManager();
		
		//fm.readFile("C:\\Users\\gody8\\eclipse-workspace\\GoodPrice\\data\\원천조사가격정보_202104.csv");
		
		for(int i=4; i<10; i++) {
			if(i==8) continue;
			fm.readFile("./data/원천조사가격정보_20200"+i+".csv");
		}
		for(int i=0; i<3; i++) {
			fm.readFile("./data/원천조사가격정보_20201"+i+".csv");
		}
		for(int i=1; i<5; i++) {
			fm.readFile("./data/원천조사가격정보_20210"+i+".csv");
		}
		
		
		SqlManager sm = new SqlManager();
		Transaction[] ts = fm.getTransactions();
		sm.connect();
		sm.resetTables();
		sm.makeTables();
		sm.insertItem(ts);
		sm.insertMarket(ts);
		sm.insertSell(ts);
		
//		System.out.println("GoodPrice - 농수축산물 정보 조회 및 추천 서비스");

		int idNumber = 1;
		
		while(true) {
			
			
			//---------시나리오 3. 코멘트 입력 및 조회하기
			if(!checkInput(scan)) System.exit(0);
			System.out.println("\n---코멘트 입력하기---");
			System.out.print("시장 입력 :");
			String marketName = scan.nextLine();
			System.out.print("코멘트 입력 :");
			String comment = scan.nextLine();
			System.out.print("평점 입력 :");
			float rate = scan.nextFloat();
			scan.nextLine();
			
			sm.insertComment(idNumber++, marketName, comment, rate);
			
			if(!checkInput(scan)) System.exit(0);
			System.out.println("\n---코멘트 조회하기---");
			System.out.print("시장 입력 :");
			marketName = scan.nextLine();
			
			sm.printComments(marketName);
			
			
			break;
		}
		
		System.out.println("\n시스템 종료.");
		
		
		sm.disconnect();
	}
	
}
