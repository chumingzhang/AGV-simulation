package object;

import java.awt.Image;
import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;

import javax.swing.ImageIcon;

import canvas.Main;
import canvas.MyMap;

public class Vehicle {
	public static int WIDTH = Main.sideLen, HEIGHT = Main.sideLen;
	
	private int id;
	public static int counter = 0;
//	private int type;
	private int capacity;
	private Position position;
	private Position born;	// 画图位置
	// UDLR-上下左右、K-不动(keep),注意为大写
	private char action;
	private LinkedList<Character> actionList;
	private int curCap;
	private boolean isFull;
	private Vector<Good> goods;
	private int speed;
	private Position destination;
	private char desType;	// 目的地类型: B-born; P-port; R-rack
	private int stayTime;	// 装货卸货停留的时间(可修改，初步设置为3个时间单位)
	public int reFind;
	private boolean movable;
	// 辅助判断是否要刷新状态栏信息框
	private boolean isChange;
	// 导入图片
	ImageIcon uphc_fIcon = new ImageIcon("source/image/uphc_f.png");
	ImageIcon uphc_hIcon = new ImageIcon("source/image/uphc_h.png");
	ImageIcon uphc_eIcon = new ImageIcon("source/image/uphc_e.png");
	ImageIcon downhc_fIcon = new ImageIcon("source/image/downhc_f.png");
	ImageIcon downhc_hIcon = new ImageIcon("source/image/downhc_h.png");
	ImageIcon downhc_eIcon = new ImageIcon("source/image/downhc_e.png");
	ImageIcon lefthc_fIcon = new ImageIcon("source/image/lefthc_f.png");
	ImageIcon lefthc_hIcon = new ImageIcon("source/image/lefthc_h.png");
	ImageIcon lefthc_eIcon = new ImageIcon("source/image/lefthc_e.png");
	ImageIcon righthc_fIcon = new ImageIcon("source/image/righthc_f.png");
	ImageIcon righthc_hIcon = new ImageIcon("source/image/righthc_h.png");
	ImageIcon righthc_eIcon = new ImageIcon("source/image/righthc_e.png");
	ImageIcon lastIcon = null;
	public char dirs[];
	Random random;
	// 走过的路程
	private int walkLen;
	
	public Vehicle(int c, Position p) {
		random=new Random();
		counter++;
		id = counter;
		capacity = c;
		curCap = 0;
		position = new Position(p);
		born = new Position(p);
		destination = new Position(p);
		action = 'K';
		isFull = false;
		speed = Main.sideLen;
		desType = 'B';
		goods = new Vector<Good>();
		stayTime = 0;
		actionList = new LinkedList<Character>();
		
		uphc_eIcon = change(uphc_eIcon, WIDTH, HEIGHT);
		uphc_hIcon = change(uphc_hIcon, WIDTH, HEIGHT);
		uphc_fIcon = change(uphc_fIcon, WIDTH, HEIGHT);
		downhc_eIcon = change(downhc_eIcon, WIDTH, HEIGHT);
		downhc_hIcon = change(downhc_hIcon, WIDTH, HEIGHT);
		downhc_fIcon = change(downhc_fIcon, WIDTH, HEIGHT);
		lefthc_eIcon = change(lefthc_eIcon, WIDTH, HEIGHT);
		lefthc_hIcon = change(lefthc_hIcon, WIDTH, HEIGHT);
		lefthc_fIcon = change(lefthc_fIcon, WIDTH, HEIGHT);
		righthc_eIcon = change(righthc_eIcon, WIDTH, HEIGHT);
		righthc_hIcon = change(righthc_hIcon, WIDTH, HEIGHT);
		righthc_fIcon = change(righthc_fIcon, WIDTH, HEIGHT);
		lastIcon = uphc_eIcon;
		
		walkLen = 0;
		isChange = false;
		
		dirs=new char[5];
		dirs[0]='K';dirs[1]='L';dirs[2]='R';dirs[3]='U';dirs[4]='D';
		shuffleDirs();
	}
	
	public void shuffleDirs() {
		for(int i=0;i<10;i++) {
			int x=random.nextInt(5);
			int y=random.nextInt(5);
			if (x!=y) {
				char t=dirs[x];
				dirs[x]=dirs[y];
				dirs[y]=t;
			}
		}
	}
	public void shuffleDirs(char d) {
		shuffleDirs();
		for(int i=1;i<5;i++) if (dirs[i]==d) {
			char te=dirs[i];
			dirs[i]=dirs[0];
			dirs[0]=te;
			break;
		}
	}
	
	public void calTSP() {
		if (Main.sendGoodChoice!=1) return;
		if (goods.size()<=1) return;
//		System.out.println(id+"Before TSP"+goods);
		Position temp=born.getNearRoad();
		int bx=temp.getX();
		int by=temp.getY();
		int ox=position.getX()/Main.sideLen;
		int oy=position.getY()/Main.sideLen;
		Position p[]=new Position[goods.size()];
		for(int i=0;i<goods.size();i++) {
			p[i]=goods.get(i).getRack().getEntrance().getNearRoad();
		}
		int n=goods.size();
		int m=1<<n;
		int[][] f=new int[m][n];
		int[][] pre=new int[m][n];
		for(int S=0;S<m;S++) {
			for(int i=0;i<n;i++) {
				f[S][i]=1000000000;
				if ((S&(1<<i))>0) {
					if (S==(1<<i)) {
						f[S][i]=MyMap.getDist(oy,ox,p[i].getY(),p[i].getX());
						pre[S][i]=0;
					}else {		
						for(int j=0;j<n;j++) {
							if (((S&(1<<j))>0)&&(j!=i)) {
								int te=f[S^(1<<i)][j]+MyMap.getDist(p[j].getY(),p[j].getX(),p[i].getY(),p[i].getX());
								if (te<f[S][i]) {
									f[S][i]=te;
									pre[S][i]=j;
								}
							}
						}
					}
				}
			}
		}
		int ans=1000000000;
		int now=-1;
		for(int i=0;i<n;i++) {
			int te=f[m-1][i]+MyMap.getDist(p[i].getY(),p[i].getX(),by,bx);
			if (te<ans) {
				ans=te;
				now=i;
			}
		}
		
		Good[] g=new Good[n];
		int j=0;
		m=m-1;
		while (m>0) {
			g[j++]=goods.get(now);
			int te=now;
			now=pre[m][now];
			m^=(1<<te);
		}
		
		goods=new Vector<Good>();
		for(int i=n-1;i>=0;i--) goods.add(g[i]); 
//		System.out.println(id+"After TSP"+goods);
	}
	
	public void show() {
		System.out.print("Vehicle " + id + " Position " + position.getX() + " " + position.getY());
		System.out.print(" action " + action + " des " + desType + " " + destination.getX() + " " + destination.getY());
		System.out.print("  born " + born.getX() + " " + born.getY());
		System.out.println(" CurCap " + curCap);
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
	
	public Position getPosition() {
		return position;
	}
	
	public Position getBorn() {
		return born;
	}
	
	public boolean getIsFull() {
		return isFull;
	}
	
	public void setIsFull(boolean f) {
		isFull = f;
	}
	
	public char getAction() {
		return action;
	}
	
	public void setAction(char a) {
//		System.out.println(this+"vehicle"+id+"set action"+a+" des"+desType);
		action = a;
//		System.out.println(action);
	}
	
	public int getDist(int ex,int ey) {
		int ox=position.getX()/Main.sideLen;
		int oy=position.getY()/Main.sideLen;
		int dis=MyMap.dist[oy][ox][ey][ex],te=0;
		if (ex+1<MyMap.map1[0].length) {
			te=MyMap.dist[oy][ox][ey][ex+1];
			if (te<dis) dis=te;
		}
		if (ex-1>=0) {
			te=MyMap.dist[oy][ox][ey][ex-1];
			if (te<dis) dis=te;
		}
		if (ey+1<MyMap.map1.length) {
			te=MyMap.dist[oy][ox][ey+1][ex];
			if (te<dis) dis=te;
		}
		if (ey-1>=0) {
			te=MyMap.dist[oy][ox][ey-1][ex];
			if (te<dis) dis=te;
		}		
		return dis;
	}
	
	public boolean atBorn() {
		return getDist(born.getX()/Main.sideLen,born.getY()/Main.sideLen)==0;
	}
		
	public void nextAction() {
//		System.out.println("Try to get next action"+id+actionList.isEmpty());
		if (actionList.isEmpty()||reFind>=5) {
			reFind=0;
			int ox=position.getX()/Main.sideLen;
			int oy=position.getY()/Main.sideLen;
			int ex=destination.getX()/Main.sideLen;
			int ey=destination.getY()/Main.sideLen;
			int nx=0,ny=0;
			if (desType!='B'&&Math.abs(ox-ex)+Math.abs(oy-ey)<=1){
				action='K';
				return;
			}
			if (desType=='B'&&ox==ex&&oy==ey) {
				action='K';
				return;				
			}
			
//			System.out.println("from "+ox+","+oy+"to"+ex+","+ey);
			class BFSNode{
				int x,y,pre;
				char las;
				BFSNode(int _x,int _y,int _pre,char _las){
					x=_x;
					y=_y;
					pre=_pre;
					las=_las;
				}
			}
			BFSNode[] q=new BFSNode[MyMap.getSize()];
			boolean[][] vis=new boolean[MyMap.map1[0].length][MyMap.map1.length];
			for(int i=0;i<vis.length;i++)
				for(int j=0;j<vis[i].length;j++)
					vis[i][j]=false;
			int head=0,tail=0;
			vis[ox][oy]=true;
			q[0]=new BFSNode(ox,oy,-1,' ');
			while (head<=tail) {
//				System.out.println(head+":"+q[head].x+" "+q[head].y);
//				nx=q[head].x-1;ny=q[head].y;
//				if (MyMap.reachable(ny,nx)) {
//					if (!vis[nx][ny]) {
//						vis[nx][ny]=true;
//						q[++tail]=new BFSNode(nx,ny,head,'L');
//						if (desType!='B'&&Math.abs(nx-ex)+Math.abs(ny-ey)<=1) break;
//						if (desType=='B'&&nx==ex&&ny==ey) break;
//					}
//				}
//				
//				nx=q[head].x+1;ny=q[head].y;
//				if (MyMap.reachable(ny,nx)) {
//					if (!vis[nx][ny]) {
//						vis[nx][ny]=true;
//						q[++tail]=new BFSNode(nx,ny,head,'R');
//						if (desType!='B'&&Math.abs(nx-ex)+Math.abs(ny-ey)<=1) break;
//						if (desType=='B'&&nx==ex&&ny==ey) break;
//					}
//				}
//				
//				nx=q[head].x;ny=q[head].y-1;
//				if (MyMap.reachable(ny,nx)) {
//					if (!vis[nx][ny]) {
//						vis[nx][ny]=true;
//						q[++tail]=new BFSNode(nx,ny,head,'U');
//						if (desType!='B'&&Math.abs(nx-ex)+Math.abs(ny-ey)<=1) break;
//						if (desType=='B'&&nx==ex&&ny==ey) break;
//					}
//				}
//				
//				nx=q[head].x;ny=q[head].y+1;
//				if (MyMap.reachable(ny,nx)) {
//					if (!vis[nx][ny]) {
//						vis[nx][ny]=true;
//						q[++tail]=new BFSNode(nx,ny,head,'D');
//						if (desType!='B'&&Math.abs(nx-ex)+Math.abs(ny-ey)<=1) break;
//						if (desType=='B'&&nx==ex&&ny==ey) break;
//					}
//				}			
				shuffleDirs();
//				for(int i=0;i<5;i++) System.out.print(dirs[i]);
				for(int i=0;i<dirs.length;i++) if (dirs[i]!='K'){
					nx=Main.xGo(q[head].x,dirs[i]);
					ny=Main.yGo(q[head].y,dirs[i]);
					if (MyMap.reachable(ny,nx)) {
						if (MyMap.getGrid(ny,nx)==1) {
							if (desType!='B') continue;
							if (desType=='B'&&(nx!=ex||ny!=ey)) continue;
						}
						if (!vis[nx][ny]) {
							vis[nx][ny]=true;
							q[++tail]=new BFSNode(nx,ny,head,dirs[i]);
							if (desType!='B'&&Math.abs(nx-ex)+Math.abs(ny-ey)<=1) break;
							if (desType=='B'&&nx==ex&&ny==ey) break;
						}
					}
				}
				if (desType!='B'&&Math.abs(nx-ex)+Math.abs(ny-ey)<=1) break;
				if (desType=='B'&&nx==ex&&ny==ey) break;
				head++;
			}
			if ((desType!='B'&&Math.abs(nx-ex)+Math.abs(ny-ey)>1)||(desType=='B'&&Math.abs(nx-ex)+Math.abs(ny-ey)>0)){
				System.out.println("Cannot find path.");
				action='K';
				return;
			}
			while(true){
//				System.out.println(q[tail].x+" "+q[tail].y+" "+q[tail].las+" "+q[tail].pre);
				actionList.addFirst(q[tail].las);
				tail=q[tail].pre;
				if (tail==0) break;
			}
//			System.out.println(actionList);
			action=actionList.pollFirst();//!!!!!!
		}else {
			action=actionList.pollFirst();
		}
	}
	

	
	public Vector<Good> getGoods() {
		return goods;
	}
	
	public int getSpeed() {
		return speed;
	}
	
	public Position getDestination() {
		return destination;
	}
	
	public void setDestination(Position des) {
		destination = new Position(des);
	}
	
	public char getDesType() {
		return desType;
	}
	
	public void setDesType(char dt) {
		desType = dt;
	}
	
	public int getStayTime() {
		return stayTime;
	}
	
	public void setStayTime(int st) {
		stayTime = st;
	}
	public ImageIcon getIcon() {
		if(action == 'U') {
			if(curCap == 0) {
				lastIcon = uphc_eIcon;
			} else if(isFull == true) {
				lastIcon = uphc_fIcon;
			} else {
				lastIcon = uphc_hIcon;
			}
		} else if(action == 'D') {
			if(curCap == 0) {
				lastIcon = downhc_eIcon;
			} else if(isFull == true) {
				lastIcon = downhc_fIcon;
			} else {
				lastIcon = downhc_hIcon;
			}
		} else if(action == 'L') {
			if(curCap == 0) {
				lastIcon = lefthc_eIcon;
			} else if(isFull == true) {
				lastIcon = lefthc_fIcon;
			} else {
				lastIcon = lefthc_hIcon;
			}
		} else if(action == 'R') {
			if(curCap == 0) {
				lastIcon = righthc_eIcon;
			} else if(isFull == true) {
				lastIcon = righthc_fIcon;
			} else {
				lastIcon = righthc_hIcon;
			}
		} else if(action == 'K' && curCap == 0) {	//货车送完货原地待命时变颜色
			if(lastIcon == uphc_hIcon) {
				lastIcon = uphc_eIcon;
			} else if(lastIcon == downhc_hIcon) {
				lastIcon = downhc_eIcon;
			} else if(lastIcon == lefthc_hIcon) {
				lastIcon = lefthc_eIcon;
			} else if(lastIcon == righthc_hIcon) {
				lastIcon = righthc_eIcon;
			}
		}
		return lastIcon;	
	}

	public int getRackId() {
		int tempRackId  = 0;
		if(goods.size() > 0)
			tempRackId = (getGoods().firstElement()).getRackId();
		return tempRackId;
	}
	public int getMapX() {
		return position.getX()/Main.sideLen;
	}
	public int getMapY() {
		return position.getY()/Main.sideLen;
	}
	
	public void act() {
//		System.out.println(this+"vehicle "+id+action+" des"+desType+"at ("+position.getX()/Main.sideLen+","+position.getY()/Main.sideLen+")to("+destination.getX()/Main.sideLen+","+destination.getY()/Main.sideLen+")"+actionList+goods);
		if(action == 'U') {
			moveUp();
		} else if(action == 'D') {
			moveDown();
		} else if(action == 'L') {
			moveLeft();
		} else if(action == 'R') {
			moveRight();
		} else if(action == 'K') {
			keepStay();
		}
	}
	
	private void moveUp() {
		walkLen += 1;
		int nx = position.getX();
		int ny = position.getY() - speed;
		position.setPos(nx, ny);
	}
	
	private void moveDown() {
		walkLen += 1;
		int nx = position.getX();
		int ny = position.getY() + speed;
		position.setPos(nx, ny);
	}
	
	private void moveLeft() {
		walkLen += 1;
		int nx = position.getX() - speed;
		int ny = position.getY();
		position.setPos(nx, ny);
	}
	
	private void moveRight() {
		walkLen += 1;
		int nx = position.getX() + speed;
		int ny = position.getY();
		position.setPos(nx, ny);
	}

	private void keepStay() {
		
	}
	
	
	public void goodIn(Good g) {
		goods.add(g);
		curCap++;
		isChange = true;
		if(curCap == capacity) {
			setIsFull(true);
		}
			
	}
	
	public Good goodOut() {
		Good tempGood = goods.remove(0);
		curCap--;
		isChange = true;
		if(isFull == true) {
			setIsFull(false);
		}
			
		return tempGood;
	}
	
	public int getWalkLen() {
		return walkLen;
	}
	
	public void setIsChange(boolean ic) {
		isChange = ic;
	}
	
	public boolean getIsChange() {
		return isChange;
	}
	
	public boolean getMovable() {
		return movable;
	}
	public void setMovable(boolean b) {
		movable=b;
	}
	
	// 缩放图片
	public ImageIcon change(ImageIcon image, int w, int h) {
		int width = w;
		int height = h;
		Image img = image.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT);
		ImageIcon image2 = new ImageIcon(img);
		
		return image2;
	}
	

}
