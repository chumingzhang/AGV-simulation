package canvas;

import java.awt.Choice;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.regex.*;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultTreeCellRenderer;

import object.*;

public class State extends JPanel{
	// 刷新画面
	Timer timer1;
	// 程序暂停时长（每一段分开算）
	public static double lenPauseTime;
	// 总订单数、派送中订单数、已送达订单数、暂停时正在派送的单数
	int totalOrderCount;
	int deliveringOrderCount;
	int pauseDeliveringOrderCount;
	int delivededOrderCount;
	int totalWalkLen;
	// 三个策略选项
	JTextField chooseVehicleField;
	public static Choice chooseVehicleChoice;
	JTextField sendGoodField;
	public static Choice sendGoodChoice;
	JTextField afterSendField;
	public static Choice afterSendChoice;
	// 指定编号查看对象
	private Vector<Vehicle> vehicles;
	private Vector<Rack> racks;
	private Vector<Port> ports;
	// 辅助绘画
	ImageIcon tempIcon;
	// 帧率选择器
	JTextField frameRateField;
	Choice frameRateChoice;
	// 订单生成速度
	JTextField createOrderTextField;
	JTextField createOrderField;
	// 暂停/开始、重新开始按钮
	JButton pauseAndstartButton;
	JButton restartButton;
	// 信息列表框
	JTextField basicField;
	DefaultTableModel basicTableModel;
	JTable basicTable;
	JScrollPane basicJScrollPane;
	// 车辆、货架、装载点列表框
	JTextField rackField;
	DefaultTableModel rackTableModel;
	JTable rackTable;
	JScrollPane rackJScrollPane;
	
	JTextField portField;
	DefaultTableModel portTableModel;
	JTable portTable;
	JScrollPane portJScrollPane;

	JTextField vehicleField;
	DefaultTableModel vehicleTableModel;
	JTable vehicleTable;
	JScrollPane vehicleJScrollPane;
	// 刷新下拉列表框里面的信息
	Timer timer2;

	public State(Vector<Vehicle> v, Vector<Rack> r, Vector<Port> p) {
		//setBounds(0, 0, 200, Main.frameHeight - Main.barWidth);
		//setSize(200, Main.frameHeight);
		setPreferredSize(new Dimension(200, 720));
		setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
		lenPauseTime = 0;
		totalOrderCount = 0;
		deliveringOrderCount = 0;
		pauseDeliveringOrderCount = 0;
		delivededOrderCount = 0;
		
		vehicles = new Vector<Vehicle>();
		racks = new Vector<Rack>();
		ports = new Vector<Port>();
		vehicles = v;
		racks = r;
		ports = p;

		this.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 5));
		// 让JTable居中
		DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer();
		defaultTableCellRenderer.setHorizontalAlignment(JLabel.CENTER);
		// 显示信息列表
		basicField =new JTextField("基本信息");
		basicField.setPreferredSize(new Dimension(180, 20));
		basicField.setEditable(false);
		basicField.setHorizontalAlignment(JTextField.CENTER);
		basicField.setBorder(null);
		basicField.setFont(new Font(Font.SERIF, Font.BOLD, 12));
		this.add(basicField);
		
		String[] basicColumnNames = {"属性      ", "数值"};
		String[][] basicRowValues = {
				{"总用时(秒)", "0"},
				{"平均派送时间:(秒)", "0"},
				{"平均空闲时间(秒)", "0"},
				{"总运行路程", "0"},
				{"总单数", "0"},
				{"未接单数", "0"},
				{"派送中数", "0"},
				{"已送达数", "0"},
				{"货架溢出数", "0"},
				{"装载点数", ports.size() + ""},
				{"装载点容量", MyJson.PortCapacity + ""},
				{"货架数", racks.size() + ""},
				{"货架容量", MyJson.RackCapacity + ""},
				{"运输车数", vehicles.size() + ""},
				{"运输车默认容量", MyJson.VehicleCapacityDefault + ""}
			};
		basicTableModel = new DefaultTableModel(basicRowValues, basicColumnNames);
		// 设置不可编辑
		basicTable = new JTable(basicTableModel) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		// 设置列宽度
		int[] width1 = {120, 50};
		basicTable.setColumnModel(setColumnWidth(basicTable, width1));
		// 设置文字居中
		basicTable.setDefaultRenderer(Object.class, defaultTableCellRenderer);
		
		basicJScrollPane = new JScrollPane(basicTable);
		basicJScrollPane.setPreferredSize(new Dimension(180, 90));
		this.add(basicJScrollPane);
		
		// 初始化单位信息列表
		vehicleField =new JTextField("车辆信息");
		vehicleField.setPreferredSize(new Dimension(180, 20));
		vehicleField.setEditable(false);
		vehicleField.setHorizontalAlignment(JTextField.CENTER);
		vehicleField.setBorder(null);
		vehicleField.setFont(new Font(Font.SERIF, Font.BOLD, 12));
		this.add(vehicleField);
		
		String[] vehicleColumnNames = {"编号", "总容量", "当前", "目标货架"};
		String[][] vehicleRowValues = new String[vehicles.size()][4];
		for(int i = 0; i < vehicles.size(); ++i) {
			vehicleRowValues[i] = new String[]{"" + (i + 1), "" + vehicles.get(i).getCapacity(), "" + vehicles.get(i).getCurCap(), "" + 0};
		}
		vehicleTableModel = new DefaultTableModel(vehicleRowValues, vehicleColumnNames);
		vehicleTable = new JTable(vehicleTableModel) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		vehicleTable.setSize((25 * (MyJson.MaxGoodCount) + 50 * 3), 16 * vehicles.size());
		vehicleTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//		vehicleTable.setPreferredSize(new Dimension((60 * (MyJson.MaxGoodCount) + 80 * 3), 16 * vehicles.size()));
//		vehicleTable.setBounds(0, 0, (60 * (MyJson.MaxGoodCount) + 80 * 3), 16 * vehicles.size());
//		 设置列宽度
		int[] width2 = {50, 50, 50, 20 * MyJson.MaxGoodCount};
		vehicleTable.setColumnModel(setColumnWidth(vehicleTable, width2));
		// 居中显示
		vehicleTable.setDefaultRenderer(Object.class, defaultTableCellRenderer);
		
		vehicleJScrollPane = new JScrollPane(vehicleTable);
		vehicleJScrollPane.setPreferredSize(new Dimension(180, 90));
//		vehicleJScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		this.add(vehicleJScrollPane);
		
		// rack
		rackField =new JTextField("货架信息");
		rackField.setPreferredSize(new Dimension(180, 20));
		rackField.setEditable(false);
		rackField.setHorizontalAlignment(JTextField.CENTER);
		rackField.setBorder(null);
		rackField.setFont(new Font(Font.SERIF, Font.BOLD, 12));
		this.add(rackField);
		
		String[] rackColumnNames = {"编号", "当前", "总容量"};
		String[][] rackRowValues = new String[racks.size()][3];
		for(int i = 0; i < racks.size(); ++i) {
			rackRowValues[i] = new String[]{"" + (i + 1), "" + racks.get(i).getCurCap(), "" + racks.get(i).getCapacity()};
		}
		rackTableModel = new DefaultTableModel(rackRowValues, rackColumnNames);
		rackTable = new JTable(rackTableModel) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		// 居中显示
		rackTable.setDefaultRenderer(Object.class, defaultTableCellRenderer);
		rackJScrollPane = new JScrollPane(rackTable);
		rackJScrollPane.setPreferredSize(new Dimension(180, 90));
		this.add(rackJScrollPane);
		
		// port
		portField =new JTextField("装载点信息");
		portField.setPreferredSize(new Dimension(180, 20));
		portField.setEditable(false);
		portField.setHorizontalAlignment(JTextField.CENTER);
		portField.setBorder(null);
		portField.setFont(new Font(Font.SERIF, Font.BOLD, 12));
		this.add(portField);
		
		String[] portColumnNames = {"编号", "当前", "总容量"};
		String[][] portRowValues = new String[ports.size()][3];
		for(int i = 0; i < ports.size(); ++i) {
			portRowValues[i] = new String[]{"" + (i + 1), "" + ports.get(i).getCurCap(), "" + ports.get(i).getCapacity()};
		}
		portTableModel = new DefaultTableModel(portRowValues, portColumnNames);
		portTable = new JTable(portTableModel) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		// 居中显示
		portTable.setDefaultRenderer(Object.class, defaultTableCellRenderer);
		portJScrollPane = new JScrollPane(portTable);
		portJScrollPane.setPreferredSize(new Dimension(180, 90));
		this.add(portJScrollPane);
		
		
		// 初始化生成订单速率输入框
		createOrderTextField =new JTextField("生成订单(毫秒/次):");
		createOrderTextField.setPreferredSize(new Dimension(115, 23));
		createOrderTextField.setEditable(false);
		createOrderTextField.setBorder(null);
		createOrderTextField.setFont(new Font(Font.SERIF, Font.BOLD, 12));
		this.add(createOrderTextField);
		createOrderField = new JTextField("" + MyJson.createOrderTime);
		createOrderField.setPreferredSize(new Dimension(50, 20));
		// 判断输入为正整数
		createOrderField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub	
			}
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					String patternString = "^[1-9]+[0-9]*$";
					if(Pattern.matches(patternString, createOrderField.getText())) {
						int temp = Integer.parseInt(createOrderField.getText());
						if(200 <= temp)
							Main.createOrderTime = temp;
						else
							createOrderField.setText("" + Main.createOrderTime);
					}
					else 
						createOrderField.setText("" + Main.createOrderTime);
					createOrderField.setFocusable(false);
//					System.out.println(Main.createOrderTime);
				}
			}
		});
		// 重新获得焦点，使可反复使用
		createOrderField.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub	
			}
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				createOrderField.setFocusable(true);
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
			}
		});
		createOrderField.setToolTipText("按回车确认，生成订单 >= 200 毫秒/次");
		this.add(createOrderField);
		
		// 初始化帧率选项
		frameRateField =new JTextField("刷新帧率(帧/动作):");
		frameRateField.setPreferredSize(new Dimension(115, 23));
		frameRateField.setEditable(false);
		frameRateField.setBorder(null);
		frameRateField.setFont(new Font(Font.SERIF, Font.BOLD, 12));
		this.add(frameRateField);
		frameRateChoice = new Choice();
		frameRateChoice.setPreferredSize(new Dimension(50, 20));
		for(int i = 1; i < 11; ++i) {
			frameRateChoice.add("" + i);
		}
		this.add(frameRateChoice);
		frameRateChoice.setFocusable(false);
		frameRateChoice.select(Math.min(8, (int)Math.ceil((double)4000 / csvTOarray.rowCount / csvTOarray.colCount)) - 1);
		
		// 初始化策略选项框
		chooseVehicleField =new JTextField("指派策略:");
		chooseVehicleField.setPreferredSize(new Dimension(65, 23));
		chooseVehicleField.setEditable(false);
		chooseVehicleField.setBorder(null);
		chooseVehicleField.setFont(new Font(Font.SERIF, Font.BOLD, 12));
		sendGoodField =new JTextField("派送策略:");
		sendGoodField.setPreferredSize(new Dimension(65, 23));
		sendGoodField.setEditable(false);
		sendGoodField.setBorder(null);
		sendGoodField.setFont(new Font(Font.SERIF, Font.BOLD, 12));
		afterSendField =new JTextField("等待策略:");
		afterSendField.setPreferredSize(new Dimension(65, 23));
		afterSendField.setEditable(false);
		afterSendField.setBorder(null);
		afterSendField.setFont(new Font(Font.SERIF, Font.BOLD, 12));
		
		chooseVehicleChoice = new Choice();
		sendGoodChoice = new Choice();
		afterSendChoice = new Choice();
		chooseVehicleChoice.setPreferredSize(new Dimension(100, 20));
		sendGoodChoice.setPreferredSize(new Dimension(100, 20));
		afterSendChoice.setPreferredSize(new Dimension(100, 20));
		chooseVehicleChoice.add("随机指派");
		chooseVehicleChoice.add("就近指派");
		sendGoodChoice.add("顺序派送");
		sendGoodChoice.add("TSP策略");
		afterSendChoice.add("回到起点");
		afterSendChoice.add("原地待命");
		this.add(chooseVehicleField);
		this.add(chooseVehicleChoice);
		this.add(sendGoodField);
		this.add(sendGoodChoice);
		this.add(afterSendField);
		this.add(afterSendChoice);
		
		// 使得修改完参数后立刻释放鼠标、键盘的focus
		chooseVehicleChoice.setFocusable(false);
		sendGoodChoice.setFocusable(false);
		afterSendChoice.setFocusable(false);
		
		// 按钮
		pauseAndstartButton = new JButton("点击暂停");
		restartButton = new JButton("重新开始");
		pauseAndstartButton.setPreferredSize(new Dimension(180, 30));
		restartButton.setPreferredSize(new Dimension(180, 30));
		pauseAndstartButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Main.clickButton = true;
				if(Main.pause == true) {
					pauseAndstartButton.setText("点击暂停");
				} else {
					pauseAndstartButton.setText("点击开始");
				}
			}
		});
		restartButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Main.restart = true;
			}
		});
		// 去除背景颜色、边框、突起
		pauseAndstartButton.setContentAreaFilled(false);
		pauseAndstartButton.setOpaque(false);
		pauseAndstartButton.setFocusPainted(false);
		restartButton.setContentAreaFilled(false);
		restartButton.setOpaque(false);
		restartButton.setFocusPainted(false);
		this.add(pauseAndstartButton);
		this.add(restartButton);
		
		setTimer1();
		setTimer2();
	}
	// 改变列表宽度
	private TableColumnModel setColumnWidth(JTable table, int[] width) {
		TableColumnModel columns = table.getColumnModel();
		for(int i = 0; i < width.length; ++i) {
			TableColumn column = columns.getColumn(i);
			column.setPreferredWidth(width[i]);
		}
		return columns;
	}

	// 订单信息
	public void setOrders(int to, int dio, int ddo) {
		totalOrderCount = to;
		deliveringOrderCount = dio;
		delivededOrderCount = ddo;
	}

	public void setTimer1() {
		timer1 = new Timer();
		try {
			timer1.schedule(new TimerTask() {		
				@Override
				public void run() {
					setFrameRate();
					totalWalkLen = 0;
					for(int i = 0; i < vehicles.size(); ++i) {
						totalWalkLen += vehicles.get(i).getWalkLen();
					}
				}
			}, 0, Main.flashTime);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void setTimer2() {
		timer2 = new Timer();
		try {
			timer2.schedule(new TimerTask() {		
				@Override
				public void run() {
					updateBasicInfo();
					updateVehicleInfo();
					updateRackInfo();
					updatePortInfo();
				}
			}, 0, Main.flashTime);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	private void updateBasicInfo() {
		basicTableModel.setValueAt((int)(Main.totalTime / 1000) + "", 0, 1);
		if(totalOrderCount == 0)
			basicTableModel.setValueAt(0 + "", 1, 1);
		else
			basicTableModel.setValueAt((int)(Logic.totalDeliveTime / 1000 / totalOrderCount) + "", 1, 1);
		
		basicTableModel.setValueAt((int)(Logic.totalValidTime / 1000 / vehicles.size()) + "", 2, 1);
		basicTableModel.setValueAt(totalWalkLen + "", 3, 1);
		basicTableModel.setValueAt(totalOrderCount + "", 4, 1);
		basicTableModel.setValueAt((totalOrderCount - deliveringOrderCount - delivededOrderCount) + "", 5, 1);
		basicTableModel.setValueAt(deliveringOrderCount + "", 6, 1);
		basicTableModel.setValueAt(delivededOrderCount + "", 7, 1);
		basicTableModel.setValueAt(Logic.overGoodCount + "", 8, 1);
	}
	// 更新车辆信息
	private void updateVehicleInfo() {
		for(int i = 0; i < vehicles.size(); ++i) {
			String tempRackString = "0";
			if(vehicles.get(i).getCurCap() > 0) {
				tempRackString = "";
				Vector<Good> tempGoods = vehicles.get(i).getGoods();
				for(int j = 0; j < tempGoods.size(); ++j) {
					tempRackString += tempGoods.get(j).getRackId();
					if(j != tempGoods.size() - 1)
						tempRackString += ",";
				}
					
			}
			
			vehicleTableModel.setValueAt("" + vehicles.get(i).getCurCap(), i, 2);
			vehicleTableModel.setValueAt(tempRackString, i, 3);
		}
	}
	// 更新货架信息
	private void updateRackInfo() {
		for(int i = 0; i < racks.size(); ++i) {
			if(racks.get(i).getIsChange() == true) {
				rackTableModel.setValueAt("" + racks.get(i).getCurCap(), i, 1);
			}
		}
	}
	// 更新装载点信息
	private void updatePortInfo() {
		for(int i = 0; i < ports.size(); ++i) {
			if(ports.get(i).getIsChange() == true) {
				portTableModel.setValueAt("" + ports.get(i).getCurCap(), i, 1);
			}
		}
	}
	
	public void setFrameRate() {
		APanel.reduceTimes = frameRateChoice.getSelectedIndex() + 1;
	}
	// 暂停
	public void cancelTimer() {
		timer1.cancel();
		timer2.cancel();
	}
	// 重启
	public void startTimer() {
		setTimer1();
		setTimer2();
	}
}
