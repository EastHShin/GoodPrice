import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class SqlManager {
	Connection conn;
	ArrayList<Transaction> markets;
	ArrayList<Transaction> items;
	
	public SqlManager() {
		markets = new ArrayList<>();
		items = new ArrayList<>();
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
				"  marketName varchar(20),marketRate float, city varchar(20),\r\n" + 
				"primary key(marketName));";
		String sell = "CREATE TABLE if not exists Sell (\r\n" + 
				"  stKind varchar(20), marketName varchar(20), itemRate varchar(20), month int, unit varchar(20), monAvgPrice int,\r\n" + 
				"primary key(stKind,marketName, month),\r\n" + 
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
	
	public void insertMarket(Transaction[] trans) throws SQLException {
		
		
		for(int i=0; i<trans.length; i++) {
			Transaction t = trans[i];

			int dupIndex = 0;
			if((dupIndex = isDuplicatedMarket(t, markets)) >= 0) {
				continue;
			}
			
			else {
				markets.add(t);
			}
		}
		
		for(int i=0; i<markets.size(); i++) {
			Transaction t = markets.get(i);
			Statement st = conn.createStatement();
			ResultSet rs;
			
			String query = "insert into Market values ('"+t.marketName+"', '"+t.marketRate+"', '"+t.city+"')";
			int ret = st.executeUpdate(query);
		}
		
		System.out.println("market inserted.");
	}
	
	public void insertItem(Transaction[] trans) throws SQLException {
		
		for(int i=0; i<trans.length; i++) {
			Transaction t = trans[i];

			int dupIndex = 0;
			if((dupIndex = isDuplicatedItem(t, items)) >= 0) {
				continue;
			}
			
			else {
				items.add(t);
			}
		}
		
		for(int i=0; i<items.size(); i++) {
			Transaction t = items.get(i);
			Statement st = conn.createStatement();
			ResultSet rs;
			
			String query = "insert into Item values ('"+t.stKind+"', '"+t.stItem+"', '"+t.stItemCode+"')";
			int ret = st.executeUpdate(query);
		}
		
		System.out.println("item inserted.");
	}
	
	public void insertSell(Transaction[] trans) throws SQLException {
		for(int i=0; i<trans.length; i++) {
			Transaction t = trans[i];
			Statement st = conn.createStatement();
			ResultSet rs;
			
			String query = "insert into Sell values ('"+t.stKind+"', '"+t.marketName+"', '"+t.itemRate+"', '"+
					t.month+"', '"+t.unit+"', '"+t.monAvgPrice+"')";
			int ret = st.executeUpdate(query);
		}
		
		System.out.println("sell inserted.");
	}
	
	public int isDuplicatedMarket(Transaction t, ArrayList<Transaction> markets) {
		for(int i=0; i<markets.size(); i++) {
			Transaction temp = markets.get(i);
			if(t.marketName.compareTo(temp.marketName) == 0) {
				return i;
			}
		}
		
		return -1;
	}
	
	public int isDuplicatedItem(Transaction t, ArrayList<Transaction> items) {
		for(int i=0; i<items.size(); i++) {
			if(t.stKind.compareTo(items.get(i).stKind) == 0) {
				return i;
			}
		}
		return -1;
	}
	
	public void resetTables() throws SQLException {
		Statement st = conn.createStatement();
		
		String query = "drop table if exists Market, Sell, Item, Comment, Account";
		int ret = st.executeUpdate(query);
		System.out.println("table reset complete.");
	}
	
	public void printItemByKeyword(String keyword) throws SQLException {
		String query = "select distinct stKind, marketName, monAvgPrice \r\n" + 
				"from Sell\r\n" + 
				"where stKind in (select stKind from Item where stItem like '%"+keyword+"%') " +
				"order by stKind, marketName";
		String[] types = {"s", "s", "i"};
		
		this.executeAndPrintQuery(query, types);
        
	}
	
	public void printSpecificItemByKeyword(String keyword) throws SQLException {
		String query = "select distinct stKind, marketName, monAvgPrice \r\n" + 
				"from Sell\r\n" + 
				"where stKind = '"+keyword+"'" +
				"order by monAvgPrice";
		String[] types = {"s", "s", "i"};
		
		this.executeAndPrintQuery(query, types);
        
	}
	
	public void printPriceByPlace(String keyword, String place) throws SQLException {
		String query = "select distinct stKind, Sell.marketName, monAvgPrice, month, city\r\n" + 
				"from Sell inner join market on Sell.marketname = market.marketname\r\n" + 
				"where stKind = '"+keyword+"' and city like '%"+place+"%'\r\n" + 
				"order by monAvgPrice;";
		String[] types = {"s", "s", "i", "i", "s"};
		
		this.executeAndPrintQuery(query, types);
	}
	
	public void executeAndPrintQuery(String query, String[] types) throws SQLException {
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);

		while (rs.next()) {
        	for(int i=0; i<types.length; i++) {
            	if(i>=1) System.out.print(" | ");
            	switch(types[i]) {
	            	case "s":
	            		System.out.print(String.format("%-15s", rs.getString(i+1)));
	            		break;
	            	case "i":
	            		System.out.print(String.format("%-15s", rs.getInt(i+1)));
	            		break;
	            	default:
	            		System.out.print(String.format("%-15s", rs.getDouble(i+1)));
	            		break;
            	}
            }
            System.out.print(" | \n");
        }
	}
	
	public void disconnect() throws SQLException {
		conn.close();
        System.out.print("\nExit SQL.");
	}
}
