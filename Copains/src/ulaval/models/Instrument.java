/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulaval.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author alexis
 */
public class Instrument implements Serializable {

    // list des touches de l'instrument
    public List<Key> listKeys = new ArrayList<>();

    // nom de l'instrument
    public String name = "";

    // id de l'instrument
    public int id;

    // le prof conseil le timbre ici mais moi je le mettrais dans 
    // key qui correspond a la touche de l'instrument car chaque notes peuvent
    // etre constrituée d'un timbre different
    // correspond au son de l'instrument 
    // ex : timbre = MidiInstrument.AcousticGrandPiano.ordinal()
    //public int timbre;
    @Override
    public String toString() {
        return "Instrument{" + "listKeys=" + listKeys + ", name=" + name + ", id=" + id + '}';
    }

    public void resetSearch() {
        if (listKeys.size() > 0) {
            for (Key key : listKeys) {
                key.isSearch = false;
            }
        }
    }

    public void resetIsInPartition() {
        if (listKeys.size() > 0) {
            for (Key key : listKeys) {
                key.isInPartition = false;
            }
        }
    }
}
