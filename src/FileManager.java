import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileManager {
	ArrayList<Transaction> trans; //���� ������ Ʃ�� ����Ʈ
	
	public FileManager() {
		this.trans = new ArrayList<>();
	}
	
	public void readFile(String path) throws IOException {
		File csv = new File(path);
		BufferedReader br = new BufferedReader(new FileReader(csv));

		Path filePath = Paths.get(path);
		List<String> lines = Files.readAllLines(filePath);
		
		int selectSize = 10;
		int[] randNums = this.generateRandNums(lines.size(), selectSize); //��üũ�� �� ���� ����
		
		for(int i=0; i<selectSize; i++) {
			int index = randNums[i];
			if(index == 0) continue; //�Ӹ���(cloumn �̸�) ����
			
			String[] token = lines.get(index).split(",", -1);
			Transaction t = new Transaction(token);
			
			int dupIndex = 0;
			if((dupIndex = this.isDuplicated(t)) > 0) { //�ߺ� Ʃ���� �����Ѵٸ�
				Transaction duped= trans.get(dupIndex);
				
				if(duped.dupCount > 5) { //5�� �̻� �ߺ��� Ʃ���̶��
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
	
	public int[] generateRandNums(int inputLen, int outputLen) { //���� �ε��� �迭 ����
		int[] ret = new int[outputLen];
		List<Integer> listInputs = new ArrayList<>();
		
		for(int i=0; i<inputLen; i++) listInputs.add(i);
		Collections.shuffle(listInputs);
		for(int i=0; i<outputLen; i++) ret[i] = listInputs.get(i);
		return ret;
	}
	
	public int isDuplicated(Transaction t) { //�ߺ��� Ʃ�� Ȯ��
		for(int i=0; i<trans.size(); i++) {
			Transaction temp = trans.get(i);
			if(temp.stItemCode == t.stItemCode && temp.marketName == t.marketName
					&& temp.month == t.month) {
				return i;
			}
		}
		
		return -1;
	}
	
	public void printTransactions() {
		for(int i=0; i<trans.size(); i++) {
			Transaction t = trans.get(i);
			System.out.println(t.stKind+", "+t.stItem+", "+t.marketName+", "+t.itemRate
					+", "+t.unit+", "+t.monAvgPrice+", "+t.month);
		}
	}
}
