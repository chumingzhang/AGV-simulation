package canvas;

public class MyMap {
	/** 
	 * 22 * 21
	 * �ֶ���ʼ����ͼ: 0-�ޡ� 1-���� 2-���ܡ� 3-װ�ص㡢 4-·
	 * 			   6-�л��ܡ�7-�л�װ�ص�
	 * 			   9-�����ܡ�10-��װ�ص�
	 * 
	 * �޸ĵ�ͼʱ�ǵ�ע�� Logic 71�����ҳ�ʼ��rackʱ��entrance�ĳ�ʼ��(���ڼ��Ϊ3����+4)
	 */

	public static int[][] map1;
	public static int[][][][] dist;
	
	public MyMap() {
		// filePath��ʽʾ��:  "E:\\360MoveData\\Users\\������\\Desktop\\desert.csv"
		csvTOarray tempCSV = new csvTOarray(MyJson.CsvPath);
		int rowCount = csvTOarray.rowCount;
		int colCount = csvTOarray.colCount;
		int[][] temp = new int[rowCount][colCount];
		temp = tempCSV.getMap();
		// ���
		map1 = new int[rowCount][colCount];
		for(int i = 0; i < rowCount; ++i) {
			map1[i] = temp[i].clone();
		}
//		showMap();
		dist=new int[map1.length][map1[0].length][map1.length][map1[0].length];
		class BFSNode{
			int x,y,step;
			BFSNode(int _x,int _y,int _step){
				x=_x;
				y=_y;
				step=_step;
			}
		}
		int nx=0,ny=0;
		for(int sx=0;sx<map1.length;sx++)
			for(int sy=0;sy<map1[0].length;sy++) {
				BFSNode[] q=new BFSNode[getSize()];
				boolean[][] vis=new boolean[map1.length][map1[0].length];
				for(int i=0;i<vis.length;i++)
					for(int j=0;j<vis[i].length;j++) {
						vis[i][j]=false;
						dist[sx][sy][i][j]=1000000000;
					}
				int head=0,tail=0;
				vis[sx][sy]=true;
				q[0]=new BFSNode(sx,sy,0);
				dist[sx][sy][sx][sy]=0;
				while (head<=tail) {
					nx=q[head].x-1;ny=q[head].y;
					if (reachable(nx,ny)) {
						if (!vis[nx][ny]) {
							vis[nx][ny]=true;
							q[++tail]=new BFSNode(nx,ny,q[head].step+1);
							dist[sx][sy][nx][ny]=q[tail].step;
						}
					}
					nx=q[head].x+1;ny=q[head].y;
					if (reachable(nx,ny)) {
						if (!vis[nx][ny]) {
							vis[nx][ny]=true;
							q[++tail]=new BFSNode(nx,ny,q[head].step+1);
							dist[sx][sy][nx][ny]=q[tail].step;
						}
					}
					nx=q[head].x;ny=q[head].y-1;
					if (reachable(nx,ny)) {
						if (!vis[nx][ny]) {
							vis[nx][ny]=true;
							q[++tail]=new BFSNode(nx,ny,q[head].step+1);
							dist[sx][sy][nx][ny]=q[tail].step;
						}
					}
					nx=q[head].x;ny=q[head].y+1;
					if (reachable(nx,ny)) {
						if (!vis[nx][ny]) {
							vis[nx][ny]=true;
							q[++tail]=new BFSNode(nx,ny,q[head].step+1);
							dist[sx][sy][nx][ny]=q[tail].step;
						}
					}
					head++;
				}	
			}
	}
	
	public static boolean reachable(int i,int j) {
		if (i<0||j<0||i>=map1.length||j>=map1[i].length) return false;
		if (map1[i][j]==4 || map1[i][j] == 1) return true;
		return false;
	}
	public static int getGrid(int i,int j) {
		return map1[i][j];
	}
	public static int getSize() {
		return map1.length*map1[0].length;
	}
	public static int getDist(int ax,int ay,int bx,int by) {
		return dist[ax][ay][bx][by];
	}
	public void showMap() {
		for(int i = 0; i < map1.length; ++i) {
			for(int j = 0; j < map1[0].length; ++j) {
				System.out.print(map1[i][j] + " ");
			}
			System.out.println();
		}
	}

}
