package object;

import java.awt.Image;
import java.util.Vector;

import javax.swing.ImageIcon;

import canvas.Logic;
import canvas.Main;

public class Rack {
	public static int WIDTH = Main.sideLen, HEIGHT = Main.sideLen;
	
	private int id;
	public static int counter = 0;
	private int capacity;
	private int curCap;
//	private int type;
	private Position position;	//画图位置
	private Position entrance;	//存取货物的出入口
	private boolean isFull;
	private Vector<Good> goods;
	// 辅助判断是否要刷新状态栏信息框
	private boolean isChange;
	// 导入图片
	ImageIcon hj_fIcon = new ImageIcon("source/image/hj_f.png");
	ImageIcon hj_hIcon = new ImageIcon("source/image/hj_h.png");
	ImageIcon hj_eIcon = new ImageIcon("source/image/hj_e.png");
	
	public Rack(int cap, Position pos, Position ent) {
		counter++;
		id = counter;
		capacity = cap;
		curCap = 0;
		position = new Position(pos);
		entrance = new Position(ent);
		isFull = false;
		goods = new Vector<Good>();
		isChange = false;
		
		hj_fIcon = change(hj_fIcon, Port.WIDTH, Port.HEIGHT);
		hj_hIcon = change(hj_hIcon, Port.WIDTH, Port.HEIGHT);
		hj_eIcon = change(hj_eIcon, Port.WIDTH, Port.HEIGHT);
	}
	
	public void show() {
		System.out.print("Rack " + id + " Position " + position.getX() + " " + position.getY());
		System.out.print(" Entrance " + entrance.getX() + " " + entrance.getY());
		System.out.println(" " + capacity + " " + curCap);
	}
	
	public int getId() {
		return id;
	}
	
	public int getCapacity() {
		return capacity;
	}
	
	public int getCurCap() {
		return curCap;
	}
	
//	public void setCurCap(int c) {
//		curCap = c;
//	}
	
	public boolean getIsFull() {
		return isFull;
	}
	
	public void setIsFull(boolean f) {
		isFull = f;
	}
	
	public Position getPosition() {
		return position;
	}
	
	public Position getEntrance() {
		return entrance;
	}
	
	public Vector<Good> getGoods() {
		return goods;
	}
	
	public void setIsChange(boolean ic) {
		isChange = ic;
	}
	
	public boolean getIsChange() {
		return isChange;
	}
	
	public void put_in(Good g) {
		if(isFull == false) {
			goods.add(g);
			curCap += 1;
			isChange = true;
			if(curCap == capacity)
				setIsFull(true);
		} else {
			Logic.overGoodCount++;
			System.out.println("货架已满！-Rack" + id);
		}
	}
	
	public ImageIcon getIcon() {
		if(curCap == 0)
			return hj_eIcon;
		else if(isFull == true)
			return hj_fIcon;
		else
			return hj_hIcon;
	}
	
	// 缩放图片
	public ImageIcon change(ImageIcon image, int w, int h) {
		int width = w;
		int height = h;
		Image img = image.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT);
		ImageIcon image2 = new ImageIcon(img);
		
		return image2;
	}
	
//	public Good get_out() {
//		Good temp = null;
//		if(curCap > 0) {
//			temp = goods.firstElement();
//			curCap--;
//			if(isFull)
//				setIsFull(false);
//		} else {
//			System.out.println("装载点已为满！-Rack" + id);
//		}
//		return temp;
//	}
}
