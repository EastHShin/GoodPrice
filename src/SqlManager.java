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
				"  marketName varchar(20), city varchar(20), AvgRate float, \r\n" + 
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
		String account = "CREATE TABLE if not exists Account ( Id_No int, user_Id varchar(20), password varchar(10), name varchar(10), email varchar(30),\r\n" + 
				"primary key(Id_No)\r\n);";
		String comment = "CREATE TABLE if not exists Comment (\r\n" + 
				"Id_No int, marketName varchar(20), comment varchar(50), marketRate float, \r\n" + 
				"primary key(Id_No, marketName),\r\n" + 
				"foreign key(marketName) references Market(marketName),\r\n" + 
				"foreign key(Id_No) references Account(Id_No)\r\n" + 
				");";
		int ret = st.executeUpdate(item);
		ret = st.executeUpdate(market);
		ret = st.executeUpdate(sell);
		ret = st.executeUpdate(account);
		ret = st.executeUpdate(comment);
		
		String trigger = "create or replace function TTTrg_func()\r\n" + 
				"returns trigger AS $$\r\n" + 
				"BEGIN\r\n" + 
				"update Market set AvgRate = (select AVG(marketRate) from Comment where marketName = New.marketName)\r\n" + 
				"where marketName = New.marketName;\r\n" + 
				"return New;\r\n" + 
				"END;\r\n" + 
				"$$ LANGUAGE 'plpgsql';\r\n" + 
				"\r\n" + 
				"create trigger T\r\n" + 
				"after insert on Comment\r\n" + 
				"for each row\r\n" + 
				"EXECUTE PROCEDURE TTTrg_func();";
		ret = st.executeUpdate(trigger);
		
		String accounts = "insert into Account values(1, 'ShinSeungheon', 'asdf', '??????', 'gody8756@ajou.ac.kr');";
		ret = st.executeUpdate(accounts);
		
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
			
			String query = "insert into Market values ('"+t.marketName+"', '"+t.city+"')"
					+ "on conflict (marketName) do nothing";
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
			
			String query = "insert into Item values ('"+t.stKind+"', '"+t.stItem+"', '"+t.stItemCode+"')"
					+ "on conflict (stKind) do nothing";
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
					t.month+"', '"+t.unit+"', '"+t.monAvgPrice+"')"
							+ "on conflict (stkind, marketName, month) do nothing";
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
		
		String query = "drop table if exists Market, Sell, Item, Comment, Account cascade";
		int ret = st.executeUpdate(query);
		System.out.println("table reset complete.");
	}
	
	public void printItemByKeyword(String keyword) throws SQLException {
		String query = "select distinct stKind from Item where stItem like '%"+keyword+"%' " +
				"order by stKind;";
		String[] types = {"s"};
		
		this.executeAndPrintQuery(query, types);
        
	}
	
	public void printSpecificItemByKeyword(String keyword) throws SQLException {
		String query = "select distinct stKind, marketName, unit, monAvgPrice\r\n" + 
				"from Sell\r\n" + 
				"where stKind = '"+keyword+"'" +
				"order by monAvgPrice";
		String[] types = {"s", "s", "s", "i"};
		
		this.executeAndPrintQuery(query, types);
        
	}
	
	public void printPriceByPlace(String keyword, String place, int month) throws SQLException {
		String query = "select distinct stKind, Sell.marketName, unit, monAvgPrice\r\n" + 
				"from Sell inner join market on Sell.marketname = market.marketname\r\n" + 
				"where stKind = '"+keyword+"' and month="+month+" and city like '%"+place+"%'\r\n" + 
				"order by monAvgPrice;";
		String[] types = {"s", "s", "s", "i"};
		
		this.executeAndPrintQuery(query, types);
	}
	
	public void printPriceByPlaceView(String keyword, String place, int month) throws SQLException {
		String query = "create view PriceByPlace as select distinct stKind, Sell.marketName, unit, monAvgPrice, month, city from Sell inner join market on Sell.marketname = market.marketname;";
		
		Statement st = conn.createStatement();
		int ret = st.executeUpdate(query);
		query = "select * from PriceByPlace where stKind = '"+keyword+"' and month='"+month+"' and city like '%"+place+"%' order by monAvgPrice;";
		String[] types = {"s", "s", "s", "i"};
		this.executeAndPrintQuery(query, types);
	}
	
	public void insertComment(int idNumber, String marketName, String comment, float rate) throws SQLException {
		String query = "insert into Comment values("+idNumber+", '"+marketName+"', '"+comment+"', "+rate+");";
		Statement st = conn.createStatement();
		int ret = st.executeUpdate(query);
	}
	
	public void printComments(String marketName) throws SQLException {
		String query = "select * from Comment where marketName = '"+marketName+"';";
		String[] types = {"i", "s", "s", "d"};
		this.executeAndPrintQuery(query, types);
	}
	
	public void recommandByMonth(String keyword) throws SQLException {
		String query = "select stKind, month, unit, round(avg(monAvgPrice)) as AvgPrice\r\n" + 
				"from Sell\r\n" + 
				"where stKind like '%"+keyword+"%'\r\n" + 
				"group by stKind, month, unit\r\n" + 
				"order by AvgPrice;";
		String[] types = {"s", "i", "s", "i"};
		
		//print attribute
		System.out.println(" ");
		System.out.print(String.format("%-10s%-10s%-10s%-10s","stkind","month","unit","avgprice"));
		System.out.print("\n");
		System.out.println("----------------------------------------------------------------------------");
		this.executeAndPrintQuery(query, types);
	}
	
	public void printItemByMarket(String marketName) throws SQLException {
		String query = "select * from Item where stKind in (select stKind from Sell where marketName = '"+marketName+"');";
		String[] types = {"s", "s", "i"};
		
		//print attribute
		System.out.println(" ");
		System.out.print(String.format("%-10s%-10s%-10s","stkind","stitem","stitemcode"));
		System.out.print("\n");
		System.out.println("----------------------------------------------------------------------------");
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
	
	public void executeAndPrintQuery2(String query, String[] types) throws SQLException {
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);

		while (rs.next()) {
        	for(int i=0; i<types.length; i++) {
        		int hangle = 0;
            	if(types[i] == "s") {
            		hangle = 18 - rs.getString(i+1).length();
            		
            		System.out.print(String.format("%-"+hangle+"s",rs.getString(i+1)));
            		continue;
            	}
        		System.out.print(String.format("%-10s", rs.getString(i+1)));
            }
            System.out.print("\n");
        }
	}
	
	public void disconnect() throws SQLException {
		conn.close();
        System.out.print("\nExit SQL.");
	}
}

