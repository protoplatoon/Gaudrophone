/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulaval.domain;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import ulaval.models.Instrument;
import ulaval.models.Key;
import ulaval.models.MidiInstrument;
import ulaval.models.Tone;

/**
 *
 * @author alexis
 */
public class PianoGenerator implements IGenerator{


    Instrument piano = new Instrument();

    private final String[] octavePiano2 = {"C2", "C2#", "D2", "D2#", "E2", "F2", "F2#", "G2", "G2#", "A2", "A2#", "B2"};
    private final String[] octavePiano3 = {"C3", "C3#", "D3", "D3#", "E3", "F3", "F3#", "G3", "G3#", "A3", "A3#", "B3"};
    private final String[] octavePiano4 = {"C4", "C4#", "D4", "D#4", "E4", "F4", "F4#", "G4", "G4#", "A4", "A4#", "B4"};
    private final String[] octavePiano5 = {"C5", "C5#", "D5", "D5#", "E5", "F5", "F5#", "G5", "G5#", "A5", "A5#", "B5"};
    private final String[] octavePiano6 = {"C6", "C6#", "D6", "D6#", "E6", "F6", "F6#", "G6", "G6#", "A6", "A6#", "B6"};

    private final String[][] pianoFull = {octavePiano2, octavePiano3, octavePiano4, octavePiano5, octavePiano6};

    @Override
    public Instrument getInstrument() {

        piano.name = "piano" + piano.id;

        List<Key> listBlackKey = new ArrayList<Key>();

        int startPosX = 50;
        int startPosY = 100;

        for (int j = 0; j < 5; j++) {

            for (int i = 0; i < 12; i++) {

                boolean isWhite = true;

//                System.out.println(pianoFull[j][i]);

                Tone tone = new Tone();
                tone.name = pianoFull[j][i];
                tone.timbre = MidiInstrument.AcousticGrandPiano;

                Key key = new Key();
                key.id = i + (12 * j);
                key.keyType = 1;
                key.label = pianoFull[j][i];
                // plus ou moins la taille en pixel (ca s'adaptera en fonction de la fenetre du moins 20x20 fais un petit carrer quoi ^^)
                key.height = 150;
                key.width = 20;

                key.tone = tone;
                // defini la position que l'on souhaiterais sur un ecran garder cette valeur pour 13 touche sur l'ecran
                // padding
                startPosX += key.width + 2;
                
                key.printLabel = false;

                // key.printLabel = false;
                if (i == 1) {
                    
                    isWhite = false;

                    key.color = Color.BLACK;

                    key.height = 100;

                    key.posX = startPosX - 10 - (96*j);
                    key.posY = startPosY - 25;
                    
                    listBlackKey.add(key);

                } else if(i == 3){
                    
                    isWhite = false;

                    key.color = Color.BLACK;

                    key.height = 100;

                    key.posX = startPosX - 30 - (96*j);
                    key.posY = startPosY - 25;
                    
                    listBlackKey.add(key);
                    
                }else if(i == 6){
                    
                    isWhite = false;

                    key.color = Color.BLACK;

                    key.height = 100;

                    key.posX = startPosX - 48 - (96*j);
                    key.posY = startPosY - 25;
                    
                    listBlackKey.add(key);
                    
                }else if(i == 8){
                    
                    isWhite = false;

                    key.color = Color.BLACK;

                    key.height = 100;

                    key.posX = startPosX - 68 - (96*j);
                    key.posY = startPosY - 25;
                    
                    listBlackKey.add(key);
                    
                }else if(i == 10){
                    
                    isWhite = false;

                    key.color = Color.BLACK;

                    key.height = 100;

                    key.posX = startPosX - 88 - (96*j);
                    key.posY = startPosY - 25;
                    
                    listBlackKey.add(key);
                    
                }else {

                    key.posX = startPosX - (96 * j); // - (10*i) - (110*j);
                    key.posY = startPosY;

                    switch (i) {
                        case 2:
                        case 4:
                            key.posX = startPosX - (10 * i) - (96 * j);
                            break;
                        case 5:
                            key.posX = startPosX - 38 - (96 * j);
                            break;
                        case 7:
                            key.posX = startPosX - 58 - (96 * j);
                            break;
                        case 9:
                            key.posX = startPosX - 78 - (96 * j);
                            break;
                        case 11:
                            key.posX = startPosX - 98 - (96 * j);
                            break;
                        default:
                            break;
                    }

                }

                // calcul de la position relative
                key.relativeHeight = key.height * 15 / 100;
                key.relativeWidth = key.width * 15 / 100;
                // bon la faut voir ^^ la taille relative aussi va changer surment
                // mais c'est pas encore fait
                key.relativePosX = key.posX * 10 / 100;
                key.relativePosY = key.posY * 10 / 100;
                //System.out.println("addakey " + key);

                if (isWhite) {
                    piano.listKeys.add(key);
                }
            }

        }

        piano.listKeys.addAll(listBlackKey);
        
        return piano;
    }

    @Override
    public String getName() {
        return "Piano";
    }

    @Override
    public void setId(int id) {
        piano = new Instrument();
        piano.id = id;
    }
}
