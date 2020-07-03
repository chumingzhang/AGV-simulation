package canvas;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class csvTOarray {
	int[][] result;
	public static int rowCount;
	public static int colCount;
	
	public csvTOarray(String filePath) {
		
		FileReader file = null;
		try {
			file = new FileReader(filePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		// ��csv����������
		BufferedReader tempReader = new BufferedReader(file);
		try {	
			String[] temp = null;
			String line = tempReader.readLine();
			temp = line.split(",");
			colCount = temp.length;
			rowCount++;
			while((line = tempReader.readLine()) != null) {
				rowCount++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
//		System.out.println(rowCount + "  " +  colCount);
		result = new int[rowCount][colCount];
		
		// file��Ҫ���¶�ȡ
		try {
			file = new FileReader(filePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(file);
		try {	
			String[] temp = null;
			int count = 0;
			String line = null;
			while((line = br.readLine()) != null) {
				temp = line.split(",");
				for(int i = 0; i < temp.length; ++i) {
					// Ҫ��if-else����һЩת��(0-�ޡ� 1-���� 2-���ܡ� 3-װ�ص㡢 4-·)
					result[count][i] = Integer.parseInt(temp[i]);
				}
				count++;
			}
			
//			for(int i = 0; i < rowCount; ++i) {
//				for(int j = 0; j < colCount; ++j) {
//					System.out.print(result[i][j] + " ");
//				}
//				System.out.println();
//			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int[][] getMap() {
		return result;
	}
}
