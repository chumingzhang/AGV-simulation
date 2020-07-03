package canvas;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class MyJson {
	String jsonStr = "";
	public static int Type;
	public static int sideLen;
	public static int VehicleCapacityDefault;
	JSONArray VehicleCapacities;
	public static int[] VehicleIds;
	public static int[]	VehicleCaps;
	public static int RackCapacity;
	public static int PortCapacity;
	String PosibilitiesStr;
	public static double[] Posibilities;
	public static int MaxGoodCount;
	public static int createOrderTime;
	public static int LoadAndUnloadSpeed;
	public static String CsvPath;
	
	public MyJson(String JsonPath) {
		// 读取JSON数据到 jsonStr
		try {
			File file = new File(JsonPath);
			FileReader fileReader = new FileReader(file);
			Reader reader = new InputStreamReader(new FileInputStream(file), "Utf-8");
			int ch = 0;
			StringBuffer sbBuffer = new StringBuffer();
			while((ch = reader.read()) != -1) {
				sbBuffer.append((char) ch);
			}
			fileReader.close();
			reader.close();
			jsonStr = sbBuffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONObject jsonObject = JSONObject.parseObject(jsonStr);
		Type = jsonObject.getInteger("Type");
		sideLen = jsonObject.getInteger("sideLen");
		VehicleCapacityDefault = jsonObject.getInteger("VehicleCapacityDefault");
		VehicleCapacities = jsonObject.getJSONArray("VehicleCapacities");
		RackCapacity = jsonObject.getInteger("RackCapacity");
		PortCapacity = jsonObject.getInteger("PortCapacity");
		PosibilitiesStr = jsonObject.getString("Posibilities");
		MaxGoodCount = jsonObject.getInteger("MaxGoodCount");
		createOrderTime = jsonObject.getInteger("createOrderTime");
		LoadAndUnloadSpeed = jsonObject.getInteger("LoadAndUnloadSpeed");
		CsvPath = jsonObject.getString("CsvPath");
		
//		System.out.println(jsonObject.getInteger("VehicleCapacityDefault"));
//		System.out.println(jsonObject.getInteger("RackCapacity"));
//		System.out.println(jsonObject.getInteger("PortCapacity"));
//		System.out.println(jsonObject.getString("Posibilities"));
//		System.out.println(jsonObject.getInteger("MaxGoodCount"));
//		System.out.println(jsonObject.getInteger("VehicleSpeed"));
//		System.out.println(jsonObject.getInteger("LoadAndUnloadSpeed"));
//		System.out.println(jsonObject.getString("CsvPath"));
		
		// PosibilitiesStr转为tempPosibilities数组(str-double)
		String[] tempStrings = PosibilitiesStr.split(",");
		double[] tempPosibilities = new double[tempStrings.length];
		double total = 0;
		for(int i = 0; i < tempStrings.length; ++i) {
//			System.out.println(tempStrings[i]);
			tempPosibilities[i] = Double.parseDouble(tempStrings[i]);
			total += tempPosibilities[i];
		}
		
		// tempPosibilities数组转化为Posibilities(前缀和，归一化处理)
		Posibilities = new double[tempStrings.length];
		for(int i = 0; i < tempPosibilities.length; ++i) {
			Posibilities[i] = tempPosibilities[i] / total;
			if(i > 0)
				Posibilities[i] += Posibilities[i - 1];
//			System.out.println(Posibilities[i]);
		}
		
		VehicleIds = new int[VehicleCapacities.size()];
		VehicleCaps = new int[VehicleCapacities.size()];
		for (int i = 0; i < VehicleCapacities.size(); i++){
            JSONObject tempObject = VehicleCapacities.getJSONObject(i);
            VehicleIds[i] = tempObject.getInteger("id");
            VehicleCaps[i] = tempObject.getInteger("cap");
//            System.out.println(tempObject.getInteger("id")+":"+tempObject.getInteger("cap"));
        }
	}
}
