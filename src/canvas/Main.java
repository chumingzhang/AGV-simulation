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
	// ÿ����λ��Ĵ�С
	public static int sideLen;
	// �Ƿ�ѡ����ӻ�:0-���ӻ�   1-ֱ�ӳ�����
	public static int type;
	// ����ѡ��Ĭ��ֵΪ0
	// ָ�ɳ������ԣ�0-���ָ��    1-�ͽ�ָ�ɣ�
	public static int chooseVehicleChoice;
	// ���ͻ������ ��0-˳������    1-TSP���ͣ�
	public static int sendGoodChoice;
	// ������ͺ���ԣ�0-�ص����    1-ԭ�ش�����
	public static int afterSendChoice;
	
	public static boolean clickButton;
	public static boolean pause;
	public static boolean restart;
	static Timer timer2;
	// ˢ�»����ʱ����(ֻ�����״̬�����棬��Ҫ��������Ҫ����С��ƽ���ƶ���ˢ��ʱ�����)
	public static int flashTime;
	// ���ɶ�����ʱ����,��λms
	public static int createOrderTime;
	// ˢ��Panel��� 1000ms
	public static int updatePanelTime;
	// ����������ʱ��
	public static double totalTime;
	// �ܶ������������ж����������ʹﶩ����
	static int totalOrderCount;
	static int deliveringOrderCount;
	static int delivededOrderCount;
	// ˢ�¶�����Ϣ
	static Timer timer1;
	// �������ܡ�װ�ص������/����
	static int vehicleCo;
	static int rackCo;
	static int portCo;
	static Logic mainPart;
	static JScrollPane mainScrollPane;
	static JScrollPane stateScrollPane;
	static State statePart;
	static JFrame frame;
	// frame���ڴ�С
	public static int frameWidth;
	public static int frameHeight;
	// ���������
	public static int barWidth = 35;
	// �жϵ������ڴ�С�Ƿ����
	static boolean isFinishResize;
	static int resizeCount;
	
	public static void main(String[] args) {
		new MyJson("source/warehouse.json");
		new MyMap();
		sideLen = MyJson.sideLen;
		
		// Ĭ�ϲ���
		chooseVehicleChoice = 0;
		sendGoodChoice = 0;
		afterSendChoice = 0;
		
		frame = new JFrame();
		// ��ȡ��Ļ��С����ȷ��frame��С
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
		frame.setTitle("���ֿܲ�");
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
		// mainPanel�Ĺ�����
		JPanel innerPanel = mainPart.getPanel();
		mainScrollPane = new JScrollPane(innerPanel);
		mainScrollPane.setPreferredSize(new Dimension(tempWidth - 200 - barWidth - 15, tempHeight - barWidth));
//		System.out.println(mainScrollPane.getHorizontalScrollBar().getWidth() + " " + mainScrollPane.getVerticalScrollBar().getWidth());
		// statePanel�Ĺ�����
		stateScrollPane = new JScrollPane(statePart);
		stateScrollPane.setPreferredSize(new Dimension(200 + barWidth, tempHeight - barWidth));
//		System.out.println(stateScrollPane.getHorizontalScrollBar().getWidth() + " " + stateScrollPane.getVerticalScrollBar().getWidth());
//		mainScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//		mainScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//		stateScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//		stateScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		frame.add(mainScrollPane);
		frame.add(stateScrollPane);
		// ����frame�ı��Сʱ����JPanel���ű�
		frame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
//				System.out.println(frame.getSize());
				if(isFinishResize == false)
					isFinishResize = true;
			}
		});
		// ���¿�ʼʱ���ٽ���������������ô��ڴ�С������ı䣩�Դ���addComponentListener�¼�
		frame.setSize(new Dimension(tempWidth - 1, tempHeight - 1));
		// ������С���ڴ�С(����Ϊ500*500����ʵ��400*400����Ҳ�����ԭ��)
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
	
	// ��顢�ı�״̬
	public static void setTimer2() {
		timer2 = new Timer();
		try {
			timer2.schedule(new TimerTask() {		
				@Override
				public void run() {
					// ��ͣ��ʼ
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
					// ���¿�ʼ
					if(restart == true) {
						restart = false;
						cancelTimer();
						frame.remove(mainScrollPane);
						frame.remove(stateScrollPane);
						initMain();
					}
					// �������ڴ�С���
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
	// ��ͣ
	public static void cancelTimer() {
		timer1.cancel();
		mainPart.cancelTimer();
		statePart.cancelTimer();
	}
	// ����
	public static void startTimer() {
		setTimer1();
		mainPart.startTimer();
		statePart.startTimer();
	}
}
