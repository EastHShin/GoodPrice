import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileManager {
	ArrayList<Transaction> trans; //랜던 선택한 튜플 리스트
	
	public FileManager() {
		this.trans = new ArrayList<>();
	}
	
	public void readFile(String path) throws IOException {
		Path filePath = Paths.get(path);
		List<String> lines = Files.readAllLines(filePath);
		
		int selectSize = 100;
		int[] randNums = this.generateRandNums(lines.size(), selectSize); //전체크기 중 랜덤 선택
		
		for(int i=0; i<selectSize; i++) {
			int index = randNums[i];
			if(index == 0) continue; //머릿글(cloumn 이름) 제외
			
			String[] token = lines.get(index).split(",", -1);
			Transaction t = new Transaction(token);
			
			int dupIndex = 0;
			if((dupIndex = this.isDuplicated(t)) >= 0) { //중복 튜플이 존재한다면
				Transaction duped= trans.get(dupIndex);
				
				if(duped.dupCount > 5) { //5개 이상 중복된 튜플이라면
					continue;
				}
				else {
					duped.monAvgPrice = (int)((duped.monAvgPrice + t.monAvgPrice) / 2);
					duped.dupCount++;
				}
			}
			
			else {
				trans.add(t);
			}
		}
		this.printTransactions();
		
		
	}
	
	public int[] generateRandNums(int inputLen, int outputLen) { //랜덤 인덱스 배열 리턴
		int[] ret = new int[outputLen];
		List<Integer> listInputs = new ArrayList<>();
		
		for(int i=0; i<inputLen; i++) listInputs.add(i);
		Collections.shuffle(listInputs);
		for(int i=0; i<outputLen; i++) ret[i] = listInputs.get(i);
		return ret;
	}
	
	public int isDuplicated(Transaction t) { //중복된 튜플 확인
		for(int i=0; i<trans.size(); i++) {
			Transaction temp = trans.get(i);
			if((temp.stKind.compareTo(t.stKind) == 0) && (temp.marketName.compareTo(t.marketName) == 0)
					&& temp.month == t.month) {
				return i;
			}
		}
		
		return -1;
	}
	
	public Transaction[] getTransactions() {
		return trans.toArray(new Transaction[trans.size()]);
	}
	
	public void printTransactions() {
		for(int i=0; i<trans.size(); i++) {
			Transaction t = trans.get(i);
			System.out.println(t.stKind+", "+t.stItem+", "+t.marketName+", "+t.itemRate
					+", "+t.unit+", "+t.monAvgPrice+", "+t.month);
		}
	}
}
