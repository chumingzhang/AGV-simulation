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
		
		// 求csv的行数列数
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
		
		// file需要重新读取
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
					// 要用if-else进行一些转换(0-无、 1-车、 2-货架、 3-装载点、 4-路)
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
