package app;

import javax.sound.midi.*;

public class MidiPlayer {
	
	private static MidiPlayer instance;
	private static MidiChannel[] mChannels;
	
	public static MidiPlayer getInstance() {
		if (instance == null) {
			instance = new MidiPlayer();
		}
		return instance;
	}
	
	private MidiPlayer() {
		Synthesizer midiSynth;
		try {
			midiSynth = MidiSystem.getSynthesizer();
			midiSynth.open();
			
			Instrument[] instr = midiSynth.getDefaultSoundbank().getInstruments();
			ThreadedPlayer.mChannels = midiSynth.getChannels();	// Why is this not setting as intended?
			
			midiSynth.loadInstrument(instr[0]);
		} catch (MidiUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void play(Note note) {
		Thread t = new Thread(new ThreadedPlayer(note));
		t.start();
	}


	
	private static class ThreadedPlayer implements Runnable {
		
		private static int liveThreads = 0;
		
		protected Note note;
		protected static MidiChannel[] mChannels;		
		
		private ThreadedPlayer(Note note) {
			this.note = note;
		}
		
		@Override
		public void run() {
			mChannels[0].noteOn(note.getNote(), 70);
			System.out.println("Live threads = " + liveThreads);
			liveThreads++;
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {}
			mChannels[0].noteOff(note.getNote());
			liveThreads--;
			System.out.println("Live threads = " + liveThreads);
		}
	}

}
