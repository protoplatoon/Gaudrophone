/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulaval.domain;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import ulaval.models.Instrument;
import ulaval.models.Key;
import ulaval.models.MidiInstrument;

/**
 * Class qui gere l'instrument afficher a l'ecran ainsi que sa serialisation
 *
 * @author alexis
 */
public class InstrumentManager implements Observable {

    private Instrument curInstrument;

    private final List<Observer> observers = new ArrayList<>(); ;
    
    public Dimension instrumentPanelInitialSize;

    private Dimension instrumentPanelNewSize;

    public void setInstrumentPanelNewSize(Dimension instrumentPanelNewSize) {
        this.instrumentPanelNewSize = instrumentPanelNewSize;
    }

    public void setCurInstrument(Instrument curInstrument) {
        this.curInstrument = curInstrument;

        //for (Key key : curInstrument.listKeys) {
        // si la hitbox est un rectangle
        //        if (isResizable()) {
        //key.posX = (int) (key.relativePosX * instrumentPanelSize.width / 100);
        //key.posY = (int) (key.relativePosY * instrumentPanelSize.height / 100);
        //key.relativeWidth = curSelectedKey.width * 100 / InstrumentDisplay.getWidth();
        //key.relativeHeight = curSelectedKey.height * 100 / InstrumentDisplay.getHeight();
        //key.height = (int) (key.relativeHeight * instrumentPanelSize.height / 100);
        //key.width = (int) (key.relativeWidth * instrumentPanelSize.width / 100);
        //        }
        //System.out.println("pos x " + key.posX + " y " + key.posY);
        //    }
    }

    public Instrument getInstrument() {
        return this.curInstrument;
    }

    public void addKey(Instrument instrument, Key touche) {
        if (instrument != null) {
            curInstrument.name = instrument.name;
        }
        if (touche != null && null != instrument) {
            instrument.listKeys.add(touche);
        }
    }

    public void delKey(Instrument instrument, Key key) {
        if (instrument == null) {
            return;
        }
        curInstrument.name = instrument.name;
        Key idelete = instrument.listKeys.stream()
                .filter((tmpKey) -> tmpKey.id == key.id)
                .findFirst().orElse(null);
        if (idelete != null) {
            instrument.listKeys.remove(idelete);
        }
    }

    public boolean editKey(Instrument instrument, Key key) {
        if (instrument == null || key == null) {
            return false;
        }
        boolean res = false;
        curInstrument.name = instrument.name;
        Key touch = null;
        for (Key tmpKey : curInstrument.listKeys) {
            if (tmpKey.id == key.id) {
                touch = tmpKey;
            }
        }
        if (touch == null) {
            // new touch
            touch = new Key();
            touch.id = key.id;
            instrument.listKeys.add(touch);
        }
        // update data
        touch.tone = key.tone;
        touch.label = key.label;
        touch.width = key.width;
        touch.height = key.height;
        touch.relativeHeight = key.relativeHeight;
        touch.relativeWidth = key.relativeWidth;
        touch.relativePosX = key.relativePosX;
        touch.relativePosY = key.relativePosY;
        touch.bordureColor = key.bordureColor;
        touch.bordureSize = key.bordureSize;
        touch.color = key.color;
        touch.isSearch = key.isSearch;
        if (key.posX > 0 && key.posY > 0) {
            
            touch.posX = key.posX;
            touch.posY = key.posY;
            res = true;
        } else
            System.err.println("InstrumentManager : Pos X ou Pos Y de la clé et < 0 !!!!!!!!!");
        touch.keyType = key.keyType;
        notifyObserversForUpdatedInstrument();
        // etc etc
        return res;
    }

    public void searchTone(String str) {
        if (str != null && !"".equals(str) && curInstrument != null) {
            this.curInstrument.listKeys.stream().filter((k) -> (k != null && k.tone.name.equals(str))).forEachOrdered((k) -> {
                k.isSearch = true;
            });
        }
    }

    public void saveInstrument() {
        // appeler la dal et sauvegarder l'instrument
    }

    /**
     * Permet de savoir si les touches rentre dans le panel de l'ecran ou pas
     *
     * @return
     */
    public boolean isResizable() {
        if (instrumentPanelInitialSize != null && curInstrument != null) {
            for (Key key : curInstrument.listKeys) {
                if (key.posX + key.width / 2 > instrumentPanelNewSize.width
                        || key.posY + key.height / 2 > instrumentPanelNewSize.height) {

                    //System.out.println("key out of screen !");
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * recupere l'instance de la clé de l'instrument correspondant si x et y
     * corresponde a l'emplacement relatif de la touche
     *
     * @param x
     * @param y
     * @return
     */
    public Key getKey(int x, int y) {
        Key curKey = null;

        if (curInstrument != null) {
            for (Key key : curInstrument.listKeys) {

                float tmpx = key.relativePosX;
                float tmpy = key.relativePosY;
                float tmpwidth = key.relativeWidth;
                float tmpheight = key.relativeHeight;

                if (x >= tmpx - tmpwidth / 2 && x <= tmpx + tmpwidth / 2
                        && y >= tmpy - tmpheight / 2 && y <= tmpy + tmpheight / 2) {
                    curKey = key;
                }
                // sinon c'est plus compliqué ^^
            }
        }
        return curKey;
    }

    public void recalcul() {
        //System.out.println("ulaval.domain.InstrumentManager.recalcul()");
        Key maxX = getMaxX();
        Key maxY = getMaxY();

        if (curInstrument != null && maxX != null && instrumentPanelInitialSize != null && maxY != null) {
            for (Key key : curInstrument.listKeys) {

                float heightRatio = (float) key.height / (float) instrumentPanelInitialSize.height;
                float widthRatio = (float) key.width / (float) instrumentPanelInitialSize.width;

                float posXRatio = (float) key.posX / (float) instrumentPanelInitialSize.width;
                float posYRatio = (float) key.posY / (float) instrumentPanelInitialSize.height;

                float doubleRatio = ((float) key.width / (float) key.height);
                float posRatio = ((float) key.posX / (float) key.posY);

                //System.out.println("new Height" + instrumentPanelNewSize.height + " init height "
                //+ instrumentPanelInitialSize.height);
                //System.out.println("max Y " + maxY.label + " relative Y " + maxY.relativePosY);
                if (instrumentPanelNewSize.height <= instrumentPanelInitialSize.height 
                        || instrumentPanelNewSize.width >= (maxX.relativePosX + 200)) {
                    
                    key.relativeHeight = heightRatio * instrumentPanelNewSize.height;
                    key.relativeWidth = key.relativeHeight * doubleRatio;
                    key.relativePosY = posYRatio * instrumentPanelNewSize.height;
                    key.relativePosX = key.relativePosY * posRatio; // lui
                    //System.out.println("1");
                }

                if (instrumentPanelNewSize.width <= instrumentPanelInitialSize.width 
                        || instrumentPanelNewSize.height >= (maxY.relativePosY + 200)) {

                    key.relativeWidth = widthRatio * instrumentPanelNewSize.width;
                    key.relativeHeight = key.relativeWidth / doubleRatio;
                    key.relativePosX = posXRatio * instrumentPanelNewSize.width;
                    key.relativePosY = key.relativePosX / posRatio; // lui
                    //System.out.println("2");
                }

              
            }
        }
    }

    public Key getMaxX() {
        Key maxX = null;
        if (curInstrument != null) {
            for (Key key : curInstrument.listKeys) {
                if (maxX == null) {
                    maxX = key;
                }
                if (key.posX > maxX.posX) {
                    maxX = key;
                }
            }
        }
        if (maxX == null) {
            return null;
        }
        try {
            return (Key) maxX.clone();
        } catch (CloneNotSupportedException
                | NullPointerException ex) {
            Logger.getLogger(InstrumentManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Key getMaxY() {
        Key maxY = null;
        if (curInstrument != null) {
            for (Key key : curInstrument.listKeys) {
                if (maxY == null) {

                    maxY = key;
                }
                if (key.posY > maxY.posY) {
                    maxY = key;
                }
            }
        }
        if (maxY == null) {
            return null;
        }
        try {
            return (Key) maxY.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(InstrumentManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void reaffectTimbre(int timbre) {
        if (this.curInstrument != null && this.curInstrument.listKeys != null) {
            this.curInstrument.listKeys.forEach((key) -> {
                key.tone.timbre = MidiInstrument.values()[timbre];
            });
        }
    }

    @Override
    public void registerObserver(Observer newListener) {
        observers.add(newListener);
    }

    @Override
    public void unregisterObserver(Observer listener) {
        observers.remove(listener);
    }
    
    public void notifyObserversForUpdatedInstrument() { // Nouvelle méthode
        observers.forEach((observer) -> {
            observer.notifyUpdated();
        });
    }
}
