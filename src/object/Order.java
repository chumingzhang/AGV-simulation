package object;

public class Order {
//	private Good good;
	private Good[] goods;
	
	public Order(Good[] g) {
		goods=new Good[g.length];
		for(int i=0;i<g.length;i++) goods[i]=g[i];
	}
	
	public Good[] getGoods() {
		return goods;
	}
	
	public void show() {
		for(int i=0;i<goods.length;i++)
			goods[i].show();
	}
}
