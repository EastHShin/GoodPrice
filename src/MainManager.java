import java.io.IOException;
import java.sql.SQLException;

public class MainManager {
	public static void main(String args[]) throws IOException, SQLException {
		FileManager fm = new FileManager();
		fm.readFile("C:\\Users\\gody8\\eclipse-workspace\\GoodPrice\\data\\원천조사가격정보_202104.csv");
		
		SqlManager sm = new SqlManager();
		sm.connect();
		sm.makeTables();
		sm.disconnect();
		
	}
}
