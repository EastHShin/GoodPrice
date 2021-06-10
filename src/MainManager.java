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
		
//		System.out.println("GoodPrice - �����깰 ���� ��ȸ �� ��õ ����");

		int idNumber = 1;
		
		while(true) {
			
			
			//---------�ó����� 3. �ڸ�Ʈ �Է� �� ��ȸ�ϱ�
			if(!checkInput(scan)) System.exit(0);
			System.out.println("\n---�ڸ�Ʈ �Է��ϱ�---");
			System.out.print("���� �Է� :");
			String marketName = scan.nextLine();
			System.out.print("�ڸ�Ʈ �Է� :");
			String comment = scan.nextLine();
			System.out.print("���� �Է� :");
			float rate = scan.nextFloat();
			scan.nextLine();
			
			sm.insertComment(idNumber++, marketName, comment, rate);
			
			if(!checkInput(scan)) System.exit(0);
			System.out.println("\n---�ڸ�Ʈ ��ȸ�ϱ�---");
			System.out.print("���� �Է� :");
			marketName = scan.nextLine();
			
			sm.printComments(marketName);
			
			
			break;
		}
		
		System.out.println("\n�ý��� ����.");
		
		
		sm.disconnect();
	}
	
}
