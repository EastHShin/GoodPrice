
public class Transaction { //튜플 1개의 정보
	String stKind;
	String stItem;
	String marketName;
	String itemRate;
	String unit;
	int monAvgPrice;
	int month;
	int stItemCode;
	String city;
	int dupCount;
	
	public Transaction(String[] token) {
		this.stKind = token[4];
		this.stItem = token[8];
		this.marketName = token[21].substring(0, token[21].length()-1);
		this.itemRate = token[12];
		this.unit = token[15];
		this.monAvgPrice = Integer.parseInt(token[16]);
		String temp = token[0].substring(5, 7);
		if(temp.substring(0, 1).compareTo("-") == 0) temp = token[0].substring(6, 8); //4월 : "-0" -> "04"
		this.month = Integer.parseInt(temp);
		this.stItemCode = Integer.parseInt(token[3]);
		this.city = token[19];
		this.dupCount = 0;
	}
}
