/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulaval.domain;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import ulaval.models.MidiInstrument;

/**
 *
 * @author alexis
 */
public class ToneManager {

    public Timer timer = new Timer();

    private Sequencer sequencer;
    
    private Sequencer newSequencer;
    
    private Boolean isOk = true;
    
    Thread t;

    public final int END_OF_TRACK = 47;
    // add these amounts to the base value A B C D E F G
    //static final int[] offsets = {9, -2, 0, 1, 3, 5, 7 };
    int offset = -2;

    final int[] offsets = {9, 11, 0, 2, 4, 5, 7};

    int notePlayed = 0;

    public ToneManager() {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            
            newSequencer = MidiSystem.getSequencer();
            newSequencer.open();
            
        } catch (MidiUnavailableException ex) {
            Logger.getLogger(ToneManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * joue une note ou un accord
     *
     * @param note ex de note : "C", "C3", "B2", "Ab", "A3b", "A#3"
     * @param instrument l'instrument jouer
     */
    public void playTones(String note, MidiInstrument instrument) {
        // 16 ticks per quarter note.

            notePlayed++;
            //System.out.println("nbNote Played : " + notePlayed);
            Runnable task = () -> {
                try {
                    isOk = false;
                    Sequence sequence;
                     if (notePlayed > 150) {
                        sequencer.close();
                        notePlayed = 0;
                        //sequencer = newSequencer;
                        sequencer = MidiSystem.getSequencer();
                        sequencer.open();
                       // System.out.println("renew sequencer");
                    }
                    
                    sequence = new Sequence(Sequence.PPQ, 1);
                    addToneToSequence(sequence, note.toCharArray(), instrument.ordinal());

                    // ouvre une sequence d'accord
                    sequencer.setSequence(sequence);
                    
                    sequencer.setTempoInBPM(0);
                    if (sequencer.isOpen() && !sequencer.isRunning())
                        sequencer.start();
                } catch (MidiUnavailableException | InvalidMidiDataException ex) {
                    Logger.getLogger(ToneManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            };
            t = new Thread(task);
            t.start();
    }

    public void playWav(String path) {
        try {
            System.out.println("Try to play : " + path);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(path));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException ex) {
            System.err.println(ex.getMessage());
        }
    }

    /**
     * Ajoute une ou des notes a une séquense sonor (Accord)
     *
     * @param s séquense midi
     * @param note list des note
     * @param instrument Instrument selectionné
     * (MidiInstrument.AcousticGrandPiano.ordinal())
     * @throws InvalidMidiDataException
     */
    private void addToneToSequence(Sequence s, char[] note, int instrument)
            throws InvalidMidiDataException {
        if (note.length == 0 || note.length == 1
                && (note[0] < 'A' && note[0] > 'G')) {
            return;
        }
        int index;
        int timeBeforeStart = 0;// temps avant le depart de la note
        //int notelength = 16; // temps de la note
        int notelength = 150; // temps de la note
        int velocity = 64; // volume (0 - 128)
        // définie la note
        int basekey = 60; // 60 is middle C. Adjusted up and down by octave
        char charIndex;
        char lastTone = 'A';
        Track track = s.createTrack(); // Begin with a new track
        // Choisir le channel de diffusion (ici 0 sortie par default)
        ShortMessage sm = new ShortMessage();
        sm.setMessage(ShortMessage.PROGRAM_CHANGE, 0, instrument, 0);
        track.add(new MidiEvent(sm, 0));

        int key = 'A';
        int nbTone = 0;
        // parcours la liste de notes pour lire l'accord
        for (index = 0; index < note.length; index++) {
            charIndex = note[index];
            if (nbTone >= 1 && charIndex >= 'A' && charIndex <= 'G') {
                addNote(track, timeBeforeStart, notelength, key, velocity);
            }
            if (charIndex >= 'A' && charIndex <= 'G') {
                key = basekey + offsets[charIndex - 'A'];
                lastTone = charIndex;
                ++nbTone;
            } else if (charIndex == 'b') { // flat
                key--;
            } else if (charIndex == '#') { // sharp
                key++;
            } else if (charIndex >= '0' && charIndex <= '8') {
                int value = charIndex - '1' + 1;
                int octaveSize = 12;
                if (value > 3) {
                    value = charIndex - '0';
                    basekey += (octaveSize * (value - 4));
                    //System.out.println("base key offset index index 12 * " + (value - 4));
                } else {
                    basekey += (-octaveSize * (4 - value));
                    //System.out.println("base key offset index 12 * " + (4 - value));
                }
                key = basekey + offsets[lastTone - 'A'];
            }
        }
        addNote(track, timeBeforeStart, notelength, key, velocity);
    }

    public void stopTone(Integer persistance) {
        if (sequencer != null) {
            isOk = true;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (sequencer.isOpen() && sequencer.isRunning()
                            && isOk) {
                        sequencer.stop();
                    }
                    //System.gc();
                }
            }, persistance);
        }

    }

    /**
     * Ajoute une note sur la sortie par défault
     *
     * @param track Accord
     * @param startTick Point de départ de l'accord dans le temps
     * @param tickLength Temps de l'accord
     * @param key Note jouée
     * @param velocity Volume (0 - 128)
     * @throws InvalidMidiDataException
     */
    private void addNote(Track track, int startTick, int tickLength, int key, int velocity)
            throws InvalidMidiDataException {
        ShortMessage on = new ShortMessage();
        on.setMessage(ShortMessage.NOTE_ON, 0, key, velocity);
        ShortMessage off = new ShortMessage();
        off.setMessage(ShortMessage.NOTE_OFF, 0, key, velocity);
        track.add(new MidiEvent(on, startTick));
        track.add(new MidiEvent(off, startTick + tickLength));
    }
}
