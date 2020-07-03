package canvas;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class Main {
	// 每个单位块的大小
	public static int sideLen;
	// 是否选择可视化:0-可视化   1-直接出结论
	public static int type;
	// 策略选择，默认值为0
	// 指派车辆策略（0-随机指派    1-就近指派）
	public static int chooseVehicleChoice;
	// 派送货物策略 （0-顺序派送    1-TSP派送）
	public static int sendGoodChoice;
	// 完成派送后策略（0-回到起点    1-原地待命）
	public static int afterSendChoice;
	
	public static boolean clickButton;
	public static boolean pause;
	public static boolean restart;
	static Timer timer2;
	// 刷新画面的时间间隔(只是左侧状态栏画面，主要部分由于要控制小车平滑移动，刷新时间更短)
	public static int flashTime;
	// 生成订单的时间间隔,单位ms
	public static int createOrderTime;
	// 刷新Panel间隔 1000ms
	public static int updatePanelTime;
	// 程序运行总时间
	public static double totalTime;
	// 总订单数、派送中订单数、已送达订单数
	static int totalOrderCount;
	static int deliveringOrderCount;
	static int delivededOrderCount;
	// 刷新订单信息
	static Timer timer1;
	// 车、货架、装载点的数量/容量
	static int vehicleCo;
	static int rackCo;
	static int portCo;
	static Logic mainPart;
	static JScrollPane mainScrollPane;
	static JScrollPane stateScrollPane;
	static State statePart;
	static JFrame frame;
	// frame窗口大小
	public static int frameWidth;
	public static int frameHeight;
	// 滚动条宽度
	public static int barWidth = 35;
	// 判断调整窗口大小是否完成
	static boolean isFinishResize;
	static int resizeCount;
	
	public static void main(String[] args) {
		new MyJson("source/warehouse.json");
		new MyMap();
		sideLen = MyJson.sideLen;
		
		// 默认策略
		chooseVehicleChoice = 0;
		sendGoodChoice = 0;
		afterSendChoice = 0;
		
		frame = new JFrame();
		// 获取屏幕大小，以确定frame大小
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		int w = (int)screensize.getWidth();
		int h = (int)screensize.getHeight();
//		System.out.println(w + "  " + h);
		if(csvTOarray.colCount * sideLen + 200 + barWidth <= w && csvTOarray.rowCount * sideLen + barWidth <= h) {
			frameWidth = csvTOarray.colCount * sideLen + 200 + barWidth;
			frameHeight = csvTOarray.rowCount * sideLen + barWidth;
		} else {
			frameWidth = w - 100;
			frameHeight = h - 100;
		}
		
//		System.out.println(frameWidth + " " + frameHeight);
		frame.setSize(frameWidth, frameHeight);
		frame.setLocationRelativeTo(null);
		frame.setTitle("智能仓库");
		frame.setResizable(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		initMain();
		
		frame.setVisible(true);
		frame.setFocusable(true);
	}
	
	public static int xGo(int x,char dir) {
		if (dir=='L') x--;
		if (dir=='R') x++;
		return x;
	}
	public static int yGo(int y,char dir) {
		if (dir=='U') y--;
		if (dir=='D') y++;
		return y;
	}
 	
	public static void initMain() {
		type = MyJson.Type;
		pause = false;
		restart = false;
		clickButton = false;
		flashTime = 1000;
		createOrderTime = MyJson.createOrderTime;
		updatePanelTime = 1000;
		totalTime = 0;
		isFinishResize = true;
		resizeCount = 5;
		mainPart = new Logic();
		statePart = new State(mainPart.getVehicles(), mainPart.getRacks(), mainPart.getPorts());
		int tempWidth = (int)frame.getSize().getWidth();
		int tempHeight = (int)frame.getSize().getHeight();
		// mainPanel的滚动框
		JPanel innerPanel = mainPart.getPanel();
		mainScrollPane = new JScrollPane(innerPanel);
		mainScrollPane.setPreferredSize(new Dimension(tempWidth - 200 - barWidth - 15, tempHeight - barWidth));
//		System.out.println(mainScrollPane.getHorizontalScrollBar().getWidth() + " " + mainScrollPane.getVerticalScrollBar().getWidth());
		// statePanel的滚动框
		stateScrollPane = new JScrollPane(statePart);
		stateScrollPane.setPreferredSize(new Dimension(200 + barWidth, tempHeight - barWidth));
//		System.out.println(stateScrollPane.getHorizontalScrollBar().getWidth() + " " + stateScrollPane.getVerticalScrollBar().getWidth());
//		mainScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//		mainScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//		stateScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//		stateScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		frame.add(mainScrollPane);
		frame.add(stateScrollPane);
		// 设置frame改变大小时两个JPanel跟着变
		frame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
//				System.out.println(frame.getSize());
				if(isFinishResize == false)
					isFinishResize = true;
			}
		});
		// 重新开始时卡顿解决方法：重新设置窗口大小（必须改变）以触发addComponentListener事件
		frame.setSize(new Dimension(tempWidth - 1, tempHeight - 1));
		// 设置最小窗口大小(设置为500*500，其实是400*400，我也不清楚原理)
		frame.setMinimumSize(new Dimension(500, 500));
		
		setTimer1();
		setTimer2();
	}
	
	public static void setTimer1() {
		timer1 = new Timer();
		try {
			timer1.schedule(new TimerTask() {		
				@Override
				public void run() {
					totalOrderCount = mainPart.getTotalOrderCount();
					deliveringOrderCount = mainPart.getDeliveringOrderCount();
					delivededOrderCount = mainPart.getDeliveredOrderCount();
					statePart.setOrders(totalOrderCount, deliveringOrderCount, delivededOrderCount);
					totalTime += flashTime;	
				}
			}, 0, flashTime);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// 检查、改变状态
	public static void setTimer2() {
		timer2 = new Timer();
		try {
			timer2.schedule(new TimerTask() {		
				@Override
				public void run() {
					// 暂停开始
					if(clickButton == true) {
						clickButton = false;
						if(pause == false) {
							cancelTimer();
							pause = true;
						} else if(pause == true) {
							startTimer();
							pause = false; 
						}
					}
					// 重新开始
					if(restart == true) {
						restart = false;
						cancelTimer();
						frame.remove(mainScrollPane);
						frame.remove(stateScrollPane);
						initMain();
					}
					// 调整窗口大小完成
					if(isFinishResize == true) {
						int tempWidth = (int)frame.getSize().getWidth();
						int tempHeight = (int)frame.getSize().getHeight();
						mainScrollPane.setPreferredSize(new Dimension(tempWidth - 200 - barWidth - 15, tempHeight - barWidth));
						stateScrollPane.setPreferredSize(new Dimension(200 + barWidth, tempHeight - barWidth));
//						System.out.println(mainScrollPane.getPreferredSize() + " " + stateScrollPane.getPreferredSize());
						resizeCount--;
						if(resizeCount <= 0) {
							resizeCount = 200;
							isFinishResize = false;
						}
					}
				}
			}, 0, 100);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	// 暂停
	public static void cancelTimer() {
		timer1.cancel();
		mainPart.cancelTimer();
		statePart.cancelTimer();
	}
	// 重启
	public static void startTimer() {
		setTimer1();
		mainPart.startTimer();
		statePart.startTimer();
	}
}
