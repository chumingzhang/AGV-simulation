package object;

public class Good {
	private int id;
	private static int counter = 0;
//	private int type;
	private int volume;
	private int rackId;
	private Rack rack;
	
	public Good(int r,Rack _rack) {
		counter++;
		id = counter;
		volume = 1;
		rackId = r;
		rack=_rack;
	}
	
	public void show() {
		System.out.println("Good " + id + " Volume " + volume + " rackid " + rackId);
	}
	
	public int getId() {
		return id;
	}
	
	public int getVolume() {
		return volume;
	}
	
	public int getRackId() {
		return rackId;
	}
	
	public Rack getRack() {
		return rack;
	}
}
