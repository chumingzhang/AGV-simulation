package canvas;

import object.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class APanel extends JPanel{
	// 记录死物位置(port、rack、road)
	// 记录运输车的位置
	private Vector<Vehicle> vehicles;
	private Vector<Rack> racks;
	private Vector<Port> ports;
	// 导入图片
	ImageIcon rIcon = new ImageIcon("source/image/r1.png");
	// 刷新画面
	Timer timer1;
	// 为了平滑而降低的时间间隔倍数
	public static int reduceTimes;
	// 辅助平滑刷新
	public static int tempTimes;
	// 缓存机制防止闪烁
	private Image bf;
	private Graphics bg;
	private Graphics2D g2d;
	
	public APanel(Vector<Vehicle> v, Vector<Rack> r, Vector<Port> p) {
		setBounds(0, 0, csvTOarray.colCount * Main.sideLen, csvTOarray.rowCount * Main.sideLen);
		//setSize(MyJson.CsvCols * Main.sideLen, MyJson.CsvRows * Main.sideLen);
		setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));

		vehicles = new Vector<Vehicle>();
		racks = new Vector<Rack>();
		ports = new Vector<Port>();
		vehicles = v;
		racks = r;
		ports = p;
		tempTimes = 0;
//		showCurMap();
		rIcon = change(rIcon, Main.sideLen, Main.sideLen);
	}
	
	@Override
	public Dimension getPreferredSize() {
	    return new Dimension(csvTOarray.colCount * Main.sideLen, csvTOarray.rowCount * Main.sideLen);
	}
	
	public void paint(Graphics g) {
		if(bf == null) {
			bf = createImage(this.getWidth(), this.getHeight());
		}
		bg = bf.getGraphics();
		g2d = (Graphics2D)bg;
		// 消除文字、图片锯齿
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2d.setColor(Color.BLACK);
		g2d.setFont(new Font(Font.DIALOG, Font.BOLD, (int)(Main.sideLen / 2)));
//		bg.setFont(new Font(Font.DIALOG, Font.BOLD, 50));
		// 绘制 rack、port
		for(int i = 0; i < racks.size(); ++i) {
			Rack tempRack = racks.get(i);
			Image tempImage = tempRack.getIcon().getImage();
			int x = tempRack.getPosition().getX();
			int y = tempRack.getPosition().getY();
			g2d.drawImage(tempImage, x, y, null);
		}
		for(int i = 0; i < ports.size(); ++i) {
			Port tempPort = ports.get(i);
			Image tempImage = tempPort.getIcon().getImage();
			int x = tempPort.getPosition().getX();
			int y = tempPort.getPosition().getY();
			g2d.drawImage(tempImage, x, y, null);
		}
		// 绘制 road、 rack编号、port编号
		int rackCount = 0;
		int portCount = 0;
		for(int i = 0; i < MyMap.map1.length; ++i) {
			for(int j = 0; j < MyMap.map1[i].length; ++j) {
				if(MyMap.map1[i][j] == 2) {
					rackCount++;
					int strWidth = bg.getFontMetrics().stringWidth("" + rackCount);
					g2d.drawString("" + rackCount, j * Main.sideLen + Main.sideLen / 2 - strWidth / 2, i * Main.sideLen + (int)(Main.sideLen / 1.5));
				} else if(MyMap.map1[i][j] == 3) {
					portCount++;
					int strWidth = bg.getFontMetrics().stringWidth("" + portCount);
					g2d.drawString("" + portCount, j * Main.sideLen + Main.sideLen / 2 - strWidth / 2, i * Main.sideLen + (int)(Main.sideLen / 1.5) - 7);
				} else if(MyMap.map1[i][j] == 4 || MyMap.map1[i][j] == 1) {
					g2d.drawImage(rIcon.getImage(), j * Main.sideLen, i * Main.sideLen, null);
				}
			}
		}
		// 绘制 vehicle 及要去的 rack编号
		double reduce = tempTimes * (Vehicle.WIDTH / (double)reduceTimes);
//		System.out.println(Math.ceil(reduce) + " " + tempTimes + " " + (Vehicle.WIDTH / (double)reduceTimes));
		int rreduce = (int)Math.ceil(reduce);
		for(int i = 0; i < vehicles.size(); ++i) {
			Vehicle tempVehicle = vehicles.get(i);
			int x = tempVehicle.getPosition().getX();
			int y = tempVehicle.getPosition().getY();
			ImageIcon tempIcon = tempVehicle.getIcon();
			tempIcon = change(tempIcon, Vehicle.WIDTH, Vehicle.HEIGHT);
			char tempAction = tempVehicle.getAction();
			int rx = x;
			int ry = y;
			if(tempAction == 'U') {
				ry += rreduce;
			} else if(tempAction == 'D') {
				ry -= rreduce;
			} else if(tempAction == 'R') {
				rx -= rreduce;
			} else if(tempAction == 'L') {
				rx += rreduce;
			}		
//			if(tempVehicle.getId() == 1)
//				System.out.println(x + " " + y + " --- " + rx + " " + ry + " --- " + reduce  + " " + tempTimes);
			g2d.drawImage(tempIcon.getImage(), rx, ry, null);
			if(tempVehicle.getCurCap() > 0) {
				int tempRackId = (tempVehicle.getGoods().firstElement()).getRackId();
				int strWidth = bg.getFontMetrics().stringWidth("" + tempRackId);
				g2d.drawString("" + tempRackId, rx + Main.sideLen / 2 - (int)(strWidth / 2.5), ry + (int)(Main.sideLen / 2.5));
			} else if(tempVehicle.getCurCap() == 0) {
				int tempID = tempVehicle.getId();
				int strWidth = bg.getFontMetrics().stringWidth("" + tempID);
				g2d.drawString("" + tempID, rx + Main.sideLen / 2 - (int)(strWidth / 2), ry + (int)(Main.sideLen / 2.5));
			}
		}
		g.drawImage(bf, 0, 0, null);
//		finishLast = true;
		setTimer();
	}
	// 缩放图片
	public ImageIcon change(ImageIcon image, int w, int h) {
		int width = w;
		int height = h;
		Image img = image.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
		ImageIcon image2 = new ImageIcon(img);
		
		return image2;
	}
	
	public void setTimer() {
		timer1 = new Timer();
		try {
			timer1.schedule(new TimerTask() {		
				@Override
				public void run() {
					if(tempTimes > 0) {
						repaint();
						tempTimes--;
					} else {
						Logic.canUpdateAction = true;
					}
					timer1.cancel();
				}
			}, 0);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	// 暂停
	public void cancelTimer() {
		timer1.cancel();
	}
	// 重启
	public void startTimer() {
		setTimer();
	}
	
	public int getTempTimes() {
		return tempTimes;
	}
	
	public void showVehicles() {
		System.out.println("APanel");
		for(int i = 0; i < vehicles.size(); ++i) {
			Vehicle tempVehicle = vehicles.get(i);
			tempVehicle.show();
		}
	}
}
