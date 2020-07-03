package object;

import java.awt.Image;
import java.util.Vector;

import javax.swing.ImageIcon;

import canvas.Main;

public class Port {
	public static int WIDTH = Main.sideLen, HEIGHT = Main.sideLen;
	
	private int id;
	public static int counter = 0;
	private int capacity;
	private int curCap;
	private boolean isFull;
	private Position position;
	private Position entrance;
	private Vector<Order> orders;
	// ¸¨ÖúÅÐ¶ÏÊÇ·ñÒªË¢ÐÂ×´Ì¬À¸ÐÅÏ¢¿ò
	private boolean isChange;
	// µ¼ÈëÍ¼Æ¬
	ImageIcon ck_fIcon = new ImageIcon("source/image/ck_f.png");
	ImageIcon ck_hIcon = new ImageIcon("source/image/ck_h.png");
	ImageIcon ck_eIcon = new ImageIcon("source/image/ck_e.png");
	
	public Port(int cap, Position p) {
		counter++;
		id = counter;
		capacity = cap;
		curCap = 0;
		isFull = false;
		position = new Position(p);
		entrance=new Position(p);
		//entrance = new Position(p.getX(), p.getY() + Port.HEIGHT);
		orders = new Vector<Order>();
		isChange = false;
		
		ck_eIcon = change(ck_eIcon, Rack.WIDTH, Rack.HEIGHT);
		ck_hIcon = change(ck_hIcon, Rack.WIDTH, Rack.HEIGHT);
		ck_fIcon = change(ck_fIcon, Rack.WIDTH, Rack.HEIGHT);
	}
	
	public void show() {
		System.out.print("Port " + id + " Position " + position.getX() + " " + position.getY());
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
	
	public void get_order(Order o) {
		orders.add(o);
		curCap += 1;
		isChange = true;
		if(curCap == capacity)
			setIsFull(true);
	}
	
	public Order send_out() {
		Order temp = orders.remove(0);
		curCap--;
		isChange = true;
		if(isFull)
			setIsFull(false);

		return temp;
	}
	
	public void setIsChange(boolean ic) {
		isChange = ic;
	}
	
	public boolean getIsChange() {
		return isChange;
	}
	
	public ImageIcon getIcon() {
		if(curCap == 0)
			return ck_eIcon;
		else if(isFull == true)
			return ck_fIcon;
		else
			return ck_hIcon;
	}
	// Ëõ·ÅÍ¼Æ¬
	public ImageIcon change(ImageIcon image, int w, int h) {
		int width = w;
		int height = h;
		Image img = image.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT);
		ImageIcon image2 = new ImageIcon(img);
		
		return image2;
	}
}
