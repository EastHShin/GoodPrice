import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class MainManager {
	public static void main(String args[]) throws IOException, SQLException {
		Scanner scan = new Scanner(System.in);
		FileManager fm = new FileManager();
		
		//fm.readFile("C:\\Users\\gody8\\eclipse-workspace\\GoodPrice\\data\\��õ���簡������_202104.csv");
		
		for(int i=4; i<10; i++) {
			if(i==8) continue;
			fm.readFile("./data/��õ���簡������_20200"+i+".csv");
		}
		for(int i=0; i<3; i++) {
			fm.readFile("./data/��õ���簡������_20201"+i+".csv");
		}
		for(int i=1; i<5; i++) {
			fm.readFile("./data/��õ���簡������_20210"+i+".csv");
		}
		
		
		SqlManager sm = new SqlManager();
		Transaction[] ts = fm.getTransactions();
		sm.connect();
		sm.resetTables();
		sm.makeTables();
		sm.insertItem(ts);
		sm.insertMarket(ts);
		sm.insertSell(ts);
		
		System.out.println("GoodPrice - �����깰 ���� ��ȸ �� ��õ ����");
		System.out.println("1. Ű����� ǰ�� �˻�");
		int ans = scan.nextInt();
		scan.nextLine();
		
		switch(ans) {
			case 1:
				System.out.print("Ű���� �Է� :");
				String keyword = scan.nextLine();
				sm.printItemByKeyword(keyword);
				
				System.out.print("\n���� ǰ�� �˻� :");
				keyword = scan.nextLine();
				sm.printSpecificItemByKeyword(keyword);
				
				System.out.print("\n1. ���� �˻�\t2. ����  :");
				ans = scan.nextInt();
				scan.nextLine();
				
				if(ans == 1) {
					System.out.println("���� �Է� :");
					String place = scan.nextLine();
					System.out.println("'"+keyword+"' �� ���� "+place+" ���庰 ����asfd");
					//sm.printPriceByPlace(keyword, place, 4);
					
					sm.printPriceByPlaceView(keyword, place, 4);
				}

				break;
		}
		
		
		sm.disconnect();
	}
}
