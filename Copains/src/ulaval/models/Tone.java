/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulaval.models;

import java.io.Serializable;

/**
 *
 * @author alexis
 */
public class Tone implements Serializable {
    
    /**
     * Nom de la touche
     *  Ex : A3, B7b, G#, Cb, E, etc ...
     */
    public String name = "G";
    
    /**
     * Défini si le son est celui d'un instrument 
     * ou d'un fichier audio
     */
    public ToneType type = ToneType.Tone;
    public Integer persistance = 500;
    /**
     * Instrument Choisi
     * si type == ToneType.Tone
     */
    public MidiInstrument timbre = MidiInstrument.AcousticGuitarNylon;
    
   
    
    /**
     * Path du sample audio a jouer quand on joue cette note
     * si type == ToneType.Sample
     */
    public String samplePath;
    
    @Override
    public String toString() {
        return "Tone{" + "name=" + name + ", type=" + type + ", persistance=" + persistance + ", timbre=" + timbre + ", samplePath=" + samplePath + '}';
    }
}
