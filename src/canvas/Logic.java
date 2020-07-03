package canvas;

import object.*;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class Logic {
	// 导入地图
	int[][] curMap;
	boolean[][] touchRoad;
	APanel panel;
	Vector<Vehicle> vehicles;
	Vector<Rack> racks;
	Vector<Port> ports;
	// 随机生成订单
	Random random = new Random();
	double[] PortPosibilities;
	Timer timer1;
	int createOrderTime;
	// 装载点订单,记录订单所在的port
	Vector<Port> orders;
	// 总订单数
	int orderCount;
	// 刷新运输车action
	Timer timer2;
	// 货架溢出物品数
	public static int overGoodCount;
//	boolean ok=false;
	private char[] actions;
//	private char[] dfsActions;
	private char[] nextActions;
	private int[][] newMap;
	private int[] priority;
	private int[] dfn;
	private boolean[] moved;
	// 判断是否可以更新小车运动（即判断画面刷新是否完成）
	public static boolean canUpdateAction;
	// 小车更新运动后停一段时间再画
	Timer timer3;
	// 总派送时间\空闲时间
	public static double totalDeliveTime;
	public static double totalValidTime;
	// 每秒更新总派送时间\空闲时间
	Timer timer4;
	
	public Logic() {
		initLogic();
		panel = new APanel(vehicles, racks, ports);
		setTimer1();
		setTimer2();
		setTimer4();
	}
	
	public APanel getPanel() {
		return panel;
	}
	
	private void initLogic() {
		curMap = new int[MyMap.map1.length][MyMap.map1[0].length];
		// 深拷贝,初始化当前关卡地图
		for(int i = 0; i < MyMap.map1.length; ++i) {
			curMap[i] = MyMap.map1[i].clone();
		}
		
		touchRoad=new boolean[MyMap.map1.length][MyMap.map1[0].length];
		for(int i=0;i<touchRoad.length;i++) {
			for(int j=0;j<touchRoad[i].length;j++){
				touchRoad[i][j]=false;
				if (MyMap.map1[i][j]==2) {
					if (i-1>=0&&MyMap.map1[i-1][j]!=2) {
						touchRoad[i][j]=true;
						continue;
					}
					if (i+1<MyMap.map1.length&&MyMap.map1[i+1][j]!=2) {
						touchRoad[i][j]=true;
						continue;
					}
					if (j-1>=0&&MyMap.map1[i][j-1]!=2) {
						touchRoad[i][j]=true;
						continue;
					}
					if (j+1<MyMap.map1[i].length&&MyMap.map1[i][j+1]!=2) {
						touchRoad[i][j]=true;
						continue;
					}
				}
			}
		}
		
		PortPosibilities = new double[MyJson.Posibilities.length];
		PortPosibilities = MyJson.Posibilities.clone();
		
//		showCurMap();
		vehicles = new Vector<Vehicle>();
		vehicles.clear();
		racks = new Vector<Rack>();
		racks.clear();
		ports = new Vector<Port>();
		ports.clear();
		orders = new Vector<Port>();
		orders.clear();
		createOrderTime = Main.createOrderTime;
		overGoodCount = 0;
		totalDeliveTime = 0;
		totalValidTime = 0;
		
		// 初始化8辆vehicle、rack、port
		Vehicle.counter = 0;
		Rack.counter = 0;
		Port.counter = 0;
		// 指定编号
		int[] tempIds = MyJson.VehicleIds.clone();
		int[] tempCaps = MyJson.VehicleCaps.clone();
		for(int i = 0; i < curMap.length; ++i) {
			for(int j = 0; j < curMap[0].length; ++j) {
				if(curMap[i][j] == 1) {
					int k;
					for(k = 0; k < tempIds.length; ++k) {
						if(Vehicle.counter + 1== tempIds[k]) {
							Vehicle tempVehicle = new Vehicle(tempCaps[k], new Position(j * Main.sideLen, i * Main.sideLen));
							vehicles.add(tempVehicle);
							break;
						}
					}
					if(k == tempIds.length) {
						Vehicle tempVehicle = new Vehicle(MyJson.VehicleCapacityDefault, new Position(j * Main.sideLen, i * Main.sideLen));
						vehicles.add(tempVehicle);
					}
//					tempVehicle.show();
				} else if(curMap[i][j] == 2) {
//					Rack tempRack = new Rack(2, new Position(j * 30, i * 30), new Position((j - (j % 4))  * 30, i * 30));
					Rack tempRack=null;
					if (touchRoad[i][j]) tempRack=new Rack(MyJson.RackCapacity, new Position(j * Main.sideLen, i * Main.sideLen), new Position(j * Main.sideLen, i * Main.sideLen));else {
						for(int k=0;;k++) {
							if (i-k>=0&&touchRoad[i-k][j]) {
								tempRack=new Rack(MyJson.RackCapacity, new Position(j * Main.sideLen, i * Main.sideLen), new Position(j * Main.sideLen, (i-k) * Main.sideLen));
								break;
							}
							if (i+k<touchRoad.length&&touchRoad[i+k][j]) {
								tempRack=new Rack(MyJson.RackCapacity, new Position(j * Main.sideLen, i * Main.sideLen), new Position(j * Main.sideLen, (i+k) * Main.sideLen));
								break;
							}
							if (j-k>=0&&touchRoad[i][j-k]) {
								tempRack=new Rack(MyJson.RackCapacity, new Position(j * Main.sideLen, i * Main.sideLen), new Position((j-k) * Main.sideLen, i * Main.sideLen));
								break;
							}
							if (j+k<touchRoad[i].length&&touchRoad[i][j+k]) {
								tempRack=new Rack(MyJson.RackCapacity, new Position(j * Main.sideLen, i * Main.sideLen), new Position((j+k) * Main.sideLen, i * Main.sideLen));
								break;	
							}
						}
					}
					racks.add(tempRack);
//					tempRack.show();
				} else if(curMap[i][j] == 3) {
					Port tempPort = new Port(MyJson.PortCapacity, new Position(j * Main.sideLen, i * Main.sideLen));
					ports.add(tempPort);
//					tempPort.show();
				}
			}
		}
	}
	
	// 随机生成订单
	public void randomCreateOrder() {
		int tempSize=random.nextInt(MyJson.MaxGoodCount)+1;
		Good[] goods=new Good[tempSize];
		for(int i=0;i<goods.length;i++) {
			int rid=random.nextInt(racks.size()) + 1;
			Rack _rack=null;
			for(int j=0;j<racks.size();j++) if (racks.get(j).getId()==rid) {
				_rack=racks.get(j);
				break;
			}
			goods[i]=new Good(rid,_rack);
		}
		Order tempOrder = new Order(goods);
//			tempOrder.show();
		while(true) {
			double temp = random.nextDouble();
			int portId = 0;
			for(int i = 0; i < PortPosibilities.length; ++i) {
				if(temp < PortPosibilities[i]) {
					portId = i;
					break;
				}	
			}
			Port tempPort = ports.get(portId);
			if(tempPort.getIsFull() == false) {
				orders.add(tempPort);
				orderCount+=goods.length;
				tempPort.get_order(tempOrder);
				break;
			}
		}
	}
//	public void dfs(int cur) {
////		System.out.println("dfs"+now);
////		for(int i=0;i<dfsActions.length;i++) System.out.print(dfsActions[i]);
////		System.out.println();
//		if (cur==vehicles.size()) {
//			boolean nocol=true;
//			for(int i=0;i<vehicles.size();i++) {
//				int nx=vehicles.get(i).getMapX();
//				int ny=vehicles.get(i).getMapY();
//				if (newMap[nx][ny]==0) continue;
//				int j=newMap[nx][ny]-1;
//				if (i==j) continue;
//				nx=vehicles.get(j).getMapX();
//				ny=vehicles.get(j).getMapY();
//				if (newMap[nx][ny]-1==i) {
////					System.out.println(nx+","+ny+" "+mx+","+my);
//					nocol=false;
//					break;
//				}
//			}
//			if (nocol) {
//				ok=true;
//				for(int i=0;i<actions.length;i++)
//					actions[i]=dfsActions[i];
//			}else {
////				for(int i=0;i<newMap.length;i++) {
////					for(int j=0;j<newMap[i].length;j++)
////						System.out.print(newMap[i][j]);
////					System.out.println();
////				}
//			}
//			return;
//		}
//		char oriAction=nextActions[dfn[cur]];
//		int nx=Main.xGo(vehicles.get(dfn[cur]).getMapX(),oriAction);
//		int ny=Main.yGo(vehicles.get(dfn[cur]).getMapY(),oriAction);
//		if (MyMap.reachable(ny,nx)&&newMap[nx][ny]==0) {
//			newMap[nx][ny]=dfn[cur]+1;
//			dfsActions[dfn[cur]]=oriAction;
//			dfs(cur+1);
//			newMap[nx][ny]=0;
//		}
//		if (!vehicles.get(dfn[cur]).getMovable()) return;
//		if (ok) return;
//		vehicles.get(dfn[cur]).shuffleDirs();
//		char[] dirs=vehicles.get(dfn[cur]).dirs;
//		for(int i=0;i<dirs.length;i++) {
//			if (oriAction!=dirs[i]) {
//				nx=Main.xGo(vehicles.get(dfn[cur]).getMapX(),dirs[i]);
//				ny=Main.yGo(vehicles.get(dfn[cur]).getMapY(),dirs[i]);
//				if (MyMap.reachable(ny,nx)&&newMap[nx][ny]==0) {
//					newMap[nx][ny]=dfn[cur]+1;
//					dfsActions[dfn[cur]]=dirs[i];
//					dfs(cur+1);
//					newMap[nx][ny]=0;
//				}		
//				if (ok) return;
//			}			
//		}
//	}
	private boolean dfs2(int x,int y) {
//		System.out.println("dfs("+x+","+y+")");
		if (newMap[x][y]==0) return true;
		int now=newMap[x][y]-1;
		if (!vehicles.get(now).getMovable()||moved[now]) return false;
//		System.out.println("dfs("+x+","+y+") id:"+now);
		vehicles.get(now).shuffleDirs(nextActions[now]);
		char[] dirs=vehicles.get(now).dirs;
		moved[now]=true;//!!!!!!!!
		for(int i=0;i<5;i++)if (dirs[i]!='K') {
			int nx=Main.xGo(x,dirs[i]);
			int ny=Main.yGo(y,dirs[i]);			
			if (MyMap.reachable(ny,nx)) {
				if (dfs2(nx,ny)) {
					actions[now]=dirs[i];
					newMap[nx][ny]=now+1;
//					moved[now]=true;
					return true;
				}
			}
		}
//		moved[now]=false;
		return false;
	}
	public void try_dfs(int now) {
		int ox=vehicles.get(now).getMapX();
		int oy=vehicles.get(now).getMapY();
//		newMap[ox][oy]=0;
//		System.out.println("try("+ox+","+oy+") id:"+now);
		vehicles.get(now).shuffleDirs(nextActions[now]);
		char[] dirs=vehicles.get(now).dirs;
		
		moved[now]=true;
		for(int i=0;i<5;i++) {
			if (dirs[i]=='K') {
				actions[now]='K';
				newMap[ox][oy]=now+1;
				break;
			}
			int nx=Main.xGo(ox,dirs[i]);
			int ny=Main.yGo(oy,dirs[i]);		
			if (MyMap.reachable(ny,nx)) {
				if (dfs2(nx,ny)) {
					actions[now]=dirs[i];
					newMap[nx][ny]=now+1;
					newMap[ox][oy]=0;
					break;
				}
			}
		}
	}
	// 更新运输车action
	public void updateAction() {
		// 接单
		while(orders.size() > 0) {
			int minDis=1000000000;
			Vehicle cho=null;
			Vector<Vehicle> ve=new Vector<Vehicle>();
			ve.clear();
			for(int i = 0; i < vehicles.size(); ++i) {
				Vehicle tempVehicle = vehicles.get(i);
				if(tempVehicle.getDesType() == 'B') {
					//goto_getGood(tempVehicle);
					//break;
					ve.add(tempVehicle);
					int nowDis=tempVehicle.getDist(orders.get(0).getPosition().getX()/Main.sideLen,orders.get(0).getPosition().getY()/Main.sideLen);
					if (nowDis<minDis) {
						minDis=nowDis;
						cho=tempVehicle;
					}
				}
			}
//			System.out.println(cho);
//			System.out.println(minDis);
			// 所有运输车都有没空
			if (cho==null) break;
			if (Main.chooseVehicleChoice==0) {
				Vehicle tempVehicle = ve.get(random.nextInt(ve.size()));
				goto_getGood(tempVehicle);
			}else if (Main.chooseVehicleChoice==1) {
				goto_getGood(cho);
			}
		}

		for(int i = 0; i < vehicles.size(); ++i) {
			Vehicle tempVehicle = vehicles.get(i);
			goto_destination(tempVehicle);
		}
		
//		System.out.println(vehicles.size());
//		ok=false;
		nextActions=new char[vehicles.size()];
		for(int i=0;i<vehicles.size();i++) nextActions[i]=vehicles.get(i).getAction();
		for(int i=0;i<vehicles.size();i++) vehicles.get(i).setAction('K');
		actions=new char[vehicles.size()];
//		dfsActions=new char[vehicles.size()];
		newMap=new int[MyMap.map1[0].length][MyMap.map1.length];
		for(int i=0;i<newMap.length;i++)
			for(int j=0;j<newMap[i].length;j++)
				newMap[i][j]=0;
		for(int i=0;i<vehicles.size();i++) {
			newMap[vehicles.get(i).getMapX()][vehicles.get(i).getMapY()]=i+1;
//			System.out.println(vehicles.get(i).getMapX()+","+vehicles.get(i).getMapY()+"->"+(i+1));
		}
		dfn=new int[vehicles.size()];
		for(int i=0;i<vehicles.size();i++) dfn[i]=i;
		priority=new int[vehicles.size()];
		int dis;
		for(int i=0;i<vehicles.size();i++) {
			switch(vehicles.get(i).getDesType()) {
				case 'R':priority[i]=i*3;break;
				case 'P':priority[i]=i*7;break;
				case 'B':if (vehicles.get(i).atBorn()) priority[i]=2000;else priority[i]=-10000+i;break;
			}
			
			int x=vehicles.get(i).getMapX();
			int y=vehicles.get(i).getMapY();
			for(int dx=-2;dx<=2;dx++) {
				for(int dy=-2;dy<=2;dy++) {
//					if (!MyMap.reachable(y+dy,x+dx)) {
//						dis=Math.abs(dx)+Math.abs(dy);
//						if (dis==1) priority[i]+=2;
//						if (dis==2) priority[i]+=2;
//						if (dis==3) priority[i]+=2;
//					}
					if (MyMap.reachable(y+dy,x+dx)&&newMap[x+dx][y+dy]>0) {
						dis=Math.abs(dx)+Math.abs(dy);
						if (dis==1) priority[i]+=5;//100;
						if (dis==2) priority[i]+=3;//50;
						if (dis==3) priority[i]+=2;//30;
						if (dis==4) priority[i]+=1;//10;
					}
				}
			}
		}
		for(int i=0;i+1<dfn.length;i++)
			for(int j=0;j<dfn.length;j++)
				if (priority[dfn[j]]>priority[dfn[i]]) {
					dis=dfn[i];
					dfn[i]=dfn[j];
					dfn[j]=dis;
				}
//		for(int i=0;i<newMap.length;i++)
//			for(int j=0;j<newMap[i].length;j++)
//				newMap[i][j]=0;
		
//		dfs(0);
		//System.out.println(actions);
//		System.out.println("test");
//		System.out.println(vehicles);
//		System.out.println(actions.length);
//		System.out.println(ok);

		
		moved=new boolean[vehicles.size()];
		for(int i=0;i<vehicles.size();i++) moved[i]=false;
		for(int i=0;i<dfn.length;i++) if (!moved[dfn[i]]){
			int now=dfn[i];
			if (nextActions[now]=='K') {
				actions[now]='K';
//				moved[now]=true;
				continue;
			}
			try_dfs(now);
		}
//		for(int i=0;i<actions.length;i++) System.out.print(actions[i]);
//		System.out.println();
		
//		if (ok) {
			for(int i=0;i<vehicles.size();i++){
				vehicles.get(i).setAction(actions[i]);
				if (nextActions[i]!=actions[i]) vehicles.get(i).reFind++;
			}
//		}else {
////			for(int i=0;i<vehicles.size();i++) vehicles.get(i).setAction('K');
//		}
		
		// 移动
		for(int i = 0; i < vehicles.size(); ++i) {
			Vehicle tempVehicle = vehicles.get(i);
			tempVehicle.act();
		}
		changeCurMap();
//		showCurMap();
		setTimer3();
//		showVPos();
	}
	// 去接单
	public void goto_getGood(Vehicle v) {
//		System.out.println(orders.size() + " ???? ");
		int tempId = orders.remove(0).getId();
//		System.out.println(orders.size() + " " + tempId);
		Port tempPort = ports.get(tempId - 1);
		v.setDestination(tempPort.getEntrance());
		v.setDesType('P');
//		System.out.println("vehicle "+v.getId()+" ready to port");
	}
	// 派送中
	public void goto_destination(Vehicle v) {
		if(v.getAction() == 'K' && v.getStayTime() > 0) {
			v.setStayTime((v.getStayTime() - 1));
			v.setMovable(false);
			return;
		}
		v.setMovable(true);
		int ox = v.getMapX();
		int oy = v.getMapY();
		if (v.getDesType()=='B'&&Main.afterSendChoice==1) {
			v.setAction('K');
			boolean ret=true;
//			for(int i=0;i<vehicles.size();i++) {
//				int x=vehicles.get(i).getMapX();
//				int y=vehicles.get(i).getMapY();
//				if (Math.abs(x-ox)+Math.abs(y-oy)>0&&Math.abs(x-ox)+Math.abs(y-oy)<=1) {
//					ret=false;
//					break;
//				}
//			}
			if (ret) return;
		}
		int ex = v.getDestination().getX()/Main.sideLen;
		int ey = v.getDestination().getY()/Main.sideLen;
//		System.out.println("vehicle "+v.getId()+" from "+ox+","+oy+"to"+ex+","+ey);
		if ((v.getDesType()!='B'&&Math.abs(ox-ex)+Math.abs(oy-ey)<=1)||(v.getDesType()=='B'&&ox==ex&&oy==ey)) {
			finishJob(v);
		}else {
			v.nextAction();
		}
	}
	
	// 到达目的地
	public void finishJob(Vehicle v) {
		// 回到出生地
		char temp = v.getDesType();
		if(temp == 'B') {
			v.setAction('K');
		} else if(temp == 'P') {	// 去到装载点
			for(int i = 0; i < ports.size(); ++i) {
				Port tempPort = ports.get(i);
				if(tempPort.getEntrance().equal(v.getDestination())) {
					Good[] tempGoods = tempPort.send_out().getGoods();
					for(int j=0;j<tempGoods.length;j++) v.goodIn(tempGoods[j]);
					v.setAction('K');
					v.setStayTime(MyJson.LoadAndUnloadSpeed * tempGoods.length);
					v.calTSP();
					v.setDesType('R');
					for(int j = 0; j < racks.size(); ++j) {
						Rack tempRack = racks.get(j);
						if(tempRack.getId() == v.getGoods().firstElement().getRackId()) {
							v.setDestination(tempRack.getEntrance());
							break;
						}
					}
					break;
				}
			}
		} else if(temp == 'R') {	// 去到对应货架
			for(int i = 0; i < racks.size(); ++i) {
				Rack tempRack = racks.get(i);
				int tempId = (v.getGoods().firstElement()).getRackId();
				if(tempRack.getId() == tempId) {
					Good tempGood = v.goodOut();
					tempRack.put_in(tempGood);
					v.setAction('K');
					v.setStayTime(MyJson.LoadAndUnloadSpeed);
					if (v.getCurCap()>0) {
						for(int j = 0; j < racks.size(); ++j) {
							tempRack= racks.get(j);
							if(tempRack.getId() == v.getGoods().firstElement().getRackId()) {
								v.setDestination(tempRack.getEntrance());
								break;
							}
						}						
					}else {
						v.setDestination(v.getBorn());
						v.setDesType('B');
					}
					break;
				}
			}
		}
	}
	// 根据位置改变地图
	public void changeCurMap() {
		//先清空原数组（主要是因为车会动）
		clearCurMap();
		// 更新车的位置
		for(int i = 0; i < vehicles.size(); ++i) {
			Vehicle tempVehicle = vehicles.get(i);
			int x = tempVehicle.getPosition().getX() / Main.sideLen;
			int y = tempVehicle.getPosition().getY() / Main.sideLen;
			curMap[y][x] = 1;
		}
//		showCurMap();
	}
	
	public void clearCurMap() {
		for(int i = 0; i < curMap.length; ++i) {
			for(int j = 0; j < curMap[0].length; ++j) {
				curMap[i][j] = 0;
			}
		}
	}
	
	public void setTimer1() {
		timer1 = new Timer();
		// 每隔xms，随机生成订单
		try {
			timer1.schedule(new TimerTask() {
				@Override
				public void run() {
					randomCreateOrder();
				}
			}, 500, createOrderTime);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setTimer2() {
		timer2 = new Timer();
		// 每隔xms，更新小车action
		try {
			timer2.schedule(new TimerTask() {
				@Override
				public void run() {
					if(canUpdateAction == true) {
						changeOption();
						canUpdateAction = false;
						updateAction();
					}
					// 修改createOrderTime后重启timer1
					if(createOrderTime != Main.createOrderTime) {
						createOrderTime = Main.createOrderTime;
						timer1.cancel();
						setTimer1();
//						System.out.println(createOrderTime + "restart timer1");
					}
				}
			}, 0, Main.updatePanelTime / 10);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	private void setTimer3() {
		timer3 = new Timer();
		try {
			timer3.schedule(new TimerTask() {
				@Override
				public void run() {
					APanel.tempTimes = APanel.reduceTimes;
					panel.setTimer();
					timer3.cancel();
				}
			}, 0);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	private void setTimer4() {
		timer4 = new Timer();
		try {
			timer4.schedule(new TimerTask() {
				@Override
				public void run() {
					// （总数-送达数） * 1000
					totalDeliveTime += (getTotalOrderCount() - getDeliveredOrderCount()) * 1000;
					for(int i = 0; i < vehicles.size(); ++i) {
						if(vehicles.get(i).getDesType() == 'B')
							totalValidTime += 1000;
					}
				}
			}, 0, 1000);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	// 小车移动到为后改变策略
	// 改变策略
	public void changeOption() {
		Main.chooseVehicleChoice = State.chooseVehicleChoice.getSelectedIndex();
		Main.sendGoodChoice = State.sendGoodChoice.getSelectedIndex();
		Main.afterSendChoice = State.afterSendChoice.getSelectedIndex();
//		System.out.println(Main.chooseVehicleChoice + " -- " + Main.sendGoodChoice + " -- " + Main.afterSendChoice);
	}
	
	// 平滑移动
	public void moveFlash() {
		panel.cancelTimer();
		panel.startTimer();
	}
	
	// 暂停
	public void cancelTimer() {
		timer1.cancel();
		timer2.cancel();
		timer4.cancel();
		panel.cancelTimer();
	}
	// 重启
	public void startTimer() {
		setTimer1();
		setTimer2();
		setTimer4();
		panel.startTimer();
	}
	
	// 总订单数
	public int getTotalOrderCount() {
		return orderCount;
	}
	// 运送中订单数
	public int getDeliveringOrderCount() {
		int delivering = 0;
		for(int i = 0; i < vehicles.size(); ++i) {
			Vehicle tempVehicle = vehicles.get(i);
			delivering += tempVehicle.getGoods().size();
		}
		return delivering;
	}
	// 已送达订单数
	public int getDeliveredOrderCount() {
		int delivered = 0;
		for(int i = 0; i < racks.size(); ++i) {
			Rack tempRack = racks.get(i);
			delivered += tempRack.getCurCap();
		}
		return delivered;
	}
	// 未接订单
	// orderCount - delivering - delivered

	// 获得对象vector
	public Vector<Vehicle> getVehicles() {
		return vehicles;
	}
	public Vector<Rack> getRacks() {
		return racks;
	}
	public Vector<Port> getPorts() {
		return ports;
	}
	
	public void showCurMap() {
		for(int i = 0; i < curMap.length; ++i) {
			for(int j = 0; j < curMap[0].length; ++j) {
				System.out.print(curMap[i][j] + " ");
			}
			System.out.println();
		}
	}
	
	public void showVehicles() {
		for(int i = 0; i < vehicles.size(); ++i) {
			Vehicle tempVehicle = vehicles.get(i);
			tempVehicle.show();
		}
	}
}
