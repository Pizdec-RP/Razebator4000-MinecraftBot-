package pizdecrp.MCAI.autotool;

public enum GetMaterial {
	stone(1);
	
	private int id;
	
	GetMaterial(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
}
