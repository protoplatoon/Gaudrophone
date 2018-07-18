/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulaval.domain;

import java.awt.Color;
import ulaval.models.Instrument;
import ulaval.models.Key;
import ulaval.models.MidiInstrument;
import ulaval.models.Tone;

/**
 *
 * @author pas alexis
 */
public class GuitarGenerator implements IGenerator{
    

    private Instrument guitare = new Instrument();
    
    private final String[] keysCorde1 = {"E4","F4","F#4","G4","A4b","A4","B4b","B4","C5","C#5","D5","E5b","E5",
                                         "B3","C4","C#4","D4","E4b","E4","F4","F4#","G4","A4b","A4","B4b","B4",
                                         "G3","A3b","A3","B3b","B3","C4","C#4","D4","E4b","E4","F4","F#4","G4",
                                         "D3","E3b","E3","F3","F#3","G3","A3b","A3","B3b","B3","C4","C#4","D4",
                                         "A2","B2b","B2","C3","C#3","D3","E3b","E3","F3","F#3","G3","A3b","A3",
                                         "E2","F2","F#2","G2","A2b","A2","B2b","B2","C3","C#3","D3","E3b","E3"};
   
    
    @Override
    public Instrument getInstrument() {
        
        //System.out.println("GenerateGuitar");
        guitare.name = "guitare" + guitare.id;
        int test = 3;
        int j = 0;
        int startPosX = 50;
        int startPosY = 150;
        for(int i = 0 ; i < 78 * test ; i+= test) {
            
            Tone tone = new Tone();
            tone.name = keysCorde1[j];
            tone.timbre = MidiInstrument.AcousticGuitarNylon;
            
            Key key = new Key();
            key.id = j;
            key.keyType = 5;
            key.label = keysCorde1[j];
            // plus ou moins la taille en pixel (ca s'adaptera en fonction de la fenetre du moins 20x20 fais un petit carrer quoi ^^)
            key.height = 45;
            key.width = 65;
            key.tone = tone;
            // defini la position que l'on souhaiterais sur un ecran garder cette valeur pour 13 touche sur l'ecran
            // padding
            //key.pathImage = "./images/CordesGuitare.jpg";
           // key.realImage = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/CordesGuitare.jpg"));
            //System.out.println("path image " + key.image);
            startPosX += key.width;
            key.color = new Color(255, 255, 255, 100);
            
            if(i % 13 == 0){
                startPosX = 50;
                startPosY+= key.height;
            }
            
            key.posX = startPosX;
            
            key.posY = startPosY;
            
            //System.out.println("addakey " + key);
            j++;
            guitare.listKeys.add(key);
        }
        
        return guitare;
    }
    

    @Override
    public String getName() {
        return "Guitare";
    }

    @Override
    public void setId(int id) {
        guitare = new Instrument();
        guitare.id = id;
    }
}