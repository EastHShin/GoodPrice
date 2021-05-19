import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class SqlManager {
	Connection conn;
	
	public SqlManager() {
		
	}
	
	public void connect() throws SQLException {
		String url = "jdbc:postgresql://localhost/postgres";
        String user = "postgres";
        
        Scanner scan = new Scanner(System.in);
        System.out.print("Insert PostgreSQL Password :");
        String password = scan.nextLine();
        
        System.out.println("Connecting PostgreSQL database");
        conn = DriverManager.getConnection(url, user, password);
        
        System.out.println("Connected");
        
	}
	
	public void makeTables() throws SQLException {
		Statement st = conn.createStatement();
		ResultSet rs;
		
		String market = "CREATE TABLE if not exists Market (\r\n" + 
				"  marketName varchar(20),marketRate float, stKind varchar(20), city varchar(20),\r\n" + 
				"primary key(marketName), foreign key(stKind) references Item(stKind)\r\n);";
		String sell = "CREATE TABLE if not exists Sell (\r\n" + 
				"  stKind varchar, marketName varchar, itemRate float, month int, unit int, monAvgPrice int,\r\n" + 
				"primary key(stKind,marketName),\r\n" + 
				"foreign key(stKind) references Item(stKind),\r\n" + 
				"foreign key(marketName) references Market(marketName)\r\n" + 
				");";
		String item = "CREATE TABLE if not exists Item (\r\n" + 
				"  stKind varchar(20), stItem varchar(20), stItemCode int, primary key(stKind)\r\n" + 
				");";
		String account = "CREATE TABLE if not exists Account ( Id_No int, user_Id varchar(20), password varchar(10), name varchar(10), email varchar(15),\r\n" + 
				"primary key(Id_No)\r\n);";
		String comment = "CREATE TABLE if not exists Comment (\r\n" + 
				"  user_Id varchar(20), marketName varchar(20), Id_No int, comment varchar(50),\r\n" + 
				"primary key(Id_No, marketName),\r\n" + 
				"foreign key(marketName) references Market(marketName),\r\n" + 
				"foreign key(Id_No) references Account(Id_No)\r\n" + 
				");";
		int ret = st.executeUpdate(item);
		ret = st.executeUpdate(market);
		ret = st.executeUpdate(sell);
		
		ret = st.executeUpdate(account);
		ret = st.executeUpdate(comment);
		
		System.out.println("Table made.");
		
	}
	
	public void disconnect() throws SQLException {
		conn.close();
        System.out.print("\nExit SQL.");
	}
}
