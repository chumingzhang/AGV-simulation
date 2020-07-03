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
	// ˢ�»���
	Timer timer1;
	// ������ͣʱ����ÿһ�ηֿ��㣩
	public static double lenPauseTime;
	// �ܶ������������ж����������ʹﶩ��������ͣʱ�������͵ĵ���
	int totalOrderCount;
	int deliveringOrderCount;
	int pauseDeliveringOrderCount;
	int delivededOrderCount;
	int totalWalkLen;
	// ��������ѡ��
	JTextField chooseVehicleField;
	public static Choice chooseVehicleChoice;
	JTextField sendGoodField;
	public static Choice sendGoodChoice;
	JTextField afterSendField;
	public static Choice afterSendChoice;
	// ָ����Ų鿴����
	private Vector<Vehicle> vehicles;
	private Vector<Rack> racks;
	private Vector<Port> ports;
	// �����滭
	ImageIcon tempIcon;
	// ֡��ѡ����
	JTextField frameRateField;
	Choice frameRateChoice;
	// ���������ٶ�
	JTextField createOrderTextField;
	JTextField createOrderField;
	// ��ͣ/��ʼ�����¿�ʼ��ť
	JButton pauseAndstartButton;
	JButton restartButton;
	// ��Ϣ�б��
	JTextField basicField;
	DefaultTableModel basicTableModel;
	JTable basicTable;
	JScrollPane basicJScrollPane;
	// ���������ܡ�װ�ص��б��
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
	// ˢ�������б���������Ϣ
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
		// ��JTable����
		DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer();
		defaultTableCellRenderer.setHorizontalAlignment(JLabel.CENTER);
		// ��ʾ��Ϣ�б�
		basicField =new JTextField("������Ϣ");
		basicField.setPreferredSize(new Dimension(180, 20));
		basicField.setEditable(false);
		basicField.setHorizontalAlignment(JTextField.CENTER);
		basicField.setBorder(null);
		basicField.setFont(new Font(Font.SERIF, Font.BOLD, 12));
		this.add(basicField);
		
		String[] basicColumnNames = {"����      ", "��ֵ"};
		String[][] basicRowValues = {
				{"����ʱ(��)", "0"},
				{"ƽ������ʱ��:(��)", "0"},
				{"ƽ������ʱ��(��)", "0"},
				{"������·��", "0"},
				{"�ܵ���", "0"},
				{"δ�ӵ���", "0"},
				{"��������", "0"},
				{"���ʹ���", "0"},
				{"���������", "0"},
				{"װ�ص���", ports.size() + ""},
				{"װ�ص�����", MyJson.PortCapacity + ""},
				{"������", racks.size() + ""},
				{"��������", MyJson.RackCapacity + ""},
				{"���䳵��", vehicles.size() + ""},
				{"���䳵Ĭ������", MyJson.VehicleCapacityDefault + ""}
			};
		basicTableModel = new DefaultTableModel(basicRowValues, basicColumnNames);
		// ���ò��ɱ༭
		basicTable = new JTable(basicTableModel) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		// �����п��
		int[] width1 = {120, 50};
		basicTable.setColumnModel(setColumnWidth(basicTable, width1));
		// �������־���
		basicTable.setDefaultRenderer(Object.class, defaultTableCellRenderer);
		
		basicJScrollPane = new JScrollPane(basicTable);
		basicJScrollPane.setPreferredSize(new Dimension(180, 90));
		this.add(basicJScrollPane);
		
		// ��ʼ����λ��Ϣ�б�
		vehicleField =new JTextField("������Ϣ");
		vehicleField.setPreferredSize(new Dimension(180, 20));
		vehicleField.setEditable(false);
		vehicleField.setHorizontalAlignment(JTextField.CENTER);
		vehicleField.setBorder(null);
		vehicleField.setFont(new Font(Font.SERIF, Font.BOLD, 12));
		this.add(vehicleField);
		
		String[] vehicleColumnNames = {"���", "������", "��ǰ", "Ŀ�����"};
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
//		 �����п��
		int[] width2 = {50, 50, 50, 20 * MyJson.MaxGoodCount};
		vehicleTable.setColumnModel(setColumnWidth(vehicleTable, width2));
		// ������ʾ
		vehicleTable.setDefaultRenderer(Object.class, defaultTableCellRenderer);
		
		vehicleJScrollPane = new JScrollPane(vehicleTable);
		vehicleJScrollPane.setPreferredSize(new Dimension(180, 90));
//		vehicleJScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		this.add(vehicleJScrollPane);
		
		// rack
		rackField =new JTextField("������Ϣ");
		rackField.setPreferredSize(new Dimension(180, 20));
		rackField.setEditable(false);
		rackField.setHorizontalAlignment(JTextField.CENTER);
		rackField.setBorder(null);
		rackField.setFont(new Font(Font.SERIF, Font.BOLD, 12));
		this.add(rackField);
		
		String[] rackColumnNames = {"���", "��ǰ", "������"};
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
		// ������ʾ
		rackTable.setDefaultRenderer(Object.class, defaultTableCellRenderer);
		rackJScrollPane = new JScrollPane(rackTable);
		rackJScrollPane.setPreferredSize(new Dimension(180, 90));
		this.add(rackJScrollPane);
		
		// port
		portField =new JTextField("װ�ص���Ϣ");
		portField.setPreferredSize(new Dimension(180, 20));
		portField.setEditable(false);
		portField.setHorizontalAlignment(JTextField.CENTER);
		portField.setBorder(null);
		portField.setFont(new Font(Font.SERIF, Font.BOLD, 12));
		this.add(portField);
		
		String[] portColumnNames = {"���", "��ǰ", "������"};
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
		// ������ʾ
		portTable.setDefaultRenderer(Object.class, defaultTableCellRenderer);
		portJScrollPane = new JScrollPane(portTable);
		portJScrollPane.setPreferredSize(new Dimension(180, 90));
		this.add(portJScrollPane);
		
		
		// ��ʼ�����ɶ������������
		createOrderTextField =new JTextField("���ɶ���(����/��):");
		createOrderTextField.setPreferredSize(new Dimension(115, 23));
		createOrderTextField.setEditable(false);
		createOrderTextField.setBorder(null);
		createOrderTextField.setFont(new Font(Font.SERIF, Font.BOLD, 12));
		this.add(createOrderTextField);
		createOrderField = new JTextField("" + MyJson.createOrderTime);
		createOrderField.setPreferredSize(new Dimension(50, 20));
		// �ж�����Ϊ������
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
		// ���»�ý��㣬ʹ�ɷ���ʹ��
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
		createOrderField.setToolTipText("���س�ȷ�ϣ����ɶ��� >= 200 ����/��");
		this.add(createOrderField);
		
		// ��ʼ��֡��ѡ��
		frameRateField =new JTextField("ˢ��֡��(֡/����):");
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
		
		// ��ʼ������ѡ���
		chooseVehicleField =new JTextField("ָ�ɲ���:");
		chooseVehicleField.setPreferredSize(new Dimension(65, 23));
		chooseVehicleField.setEditable(false);
		chooseVehicleField.setBorder(null);
		chooseVehicleField.setFont(new Font(Font.SERIF, Font.BOLD, 12));
		sendGoodField =new JTextField("���Ͳ���:");
		sendGoodField.setPreferredSize(new Dimension(65, 23));
		sendGoodField.setEditable(false);
		sendGoodField.setBorder(null);
		sendGoodField.setFont(new Font(Font.SERIF, Font.BOLD, 12));
		afterSendField =new JTextField("�ȴ�����:");
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
		chooseVehicleChoice.add("���ָ��");
		chooseVehicleChoice.add("�ͽ�ָ��");
		sendGoodChoice.add("˳������");
		sendGoodChoice.add("TSP����");
		afterSendChoice.add("�ص����");
		afterSendChoice.add("ԭ�ش���");
		this.add(chooseVehicleField);
		this.add(chooseVehicleChoice);
		this.add(sendGoodField);
		this.add(sendGoodChoice);
		this.add(afterSendField);
		this.add(afterSendChoice);
		
		// ʹ���޸�������������ͷ���ꡢ���̵�focus
		chooseVehicleChoice.setFocusable(false);
		sendGoodChoice.setFocusable(false);
		afterSendChoice.setFocusable(false);
		
		// ��ť
		pauseAndstartButton = new JButton("�����ͣ");
		restartButton = new JButton("���¿�ʼ");
		pauseAndstartButton.setPreferredSize(new Dimension(180, 30));
		restartButton.setPreferredSize(new Dimension(180, 30));
		pauseAndstartButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Main.clickButton = true;
				if(Main.pause == true) {
					pauseAndstartButton.setText("�����ͣ");
				} else {
					pauseAndstartButton.setText("�����ʼ");
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
		// ȥ��������ɫ���߿�ͻ��
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
	// �ı��б���
	private TableColumnModel setColumnWidth(JTable table, int[] width) {
		TableColumnModel columns = table.getColumnModel();
		for(int i = 0; i < width.length; ++i) {
			TableColumn column = columns.getColumn(i);
			column.setPreferredWidth(width[i]);
		}
		return columns;
	}

	// ������Ϣ
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
	// ���³�����Ϣ
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
	// ���»�����Ϣ
	private void updateRackInfo() {
		for(int i = 0; i < racks.size(); ++i) {
			if(racks.get(i).getIsChange() == true) {
				rackTableModel.setValueAt("" + racks.get(i).getCurCap(), i, 1);
			}
		}
	}
	// ����װ�ص���Ϣ
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
	// ��ͣ
	public void cancelTimer() {
		timer1.cancel();
		timer2.cancel();
	}
	// ����
	public void startTimer() {
		setTimer1();
		setTimer2();
	}
}
