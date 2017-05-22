package app;

public enum Note {
	A (57),
	B (59),
	C (60),
	D (62);
	
	private int midiValue;

	Note(int key) {
		midiValue = key;
	}
	
	public int getNote() {
		return midiValue;
	}
}
