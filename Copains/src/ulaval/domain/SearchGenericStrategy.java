/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulaval.domain;

import ulaval.models.Instrument;
import ulaval.models.Key;

/**
 *
 * @author Alexis
 */
public class SearchGenericStrategy implements ISearch {

    @Override
    public void search(String str, Instrument instrument) {
        //if (octave != null)
            //octave = octave.trim().toUpperCase();
        //System.out.println("search : " + tone);
        if (instrument.listKeys.size() > 0)
            for (Key key : instrument.listKeys) {
                if (!str.contains("1") &&!str.contains("2") && !str.contains("3")
                        && !str.contains("4") && !str.contains("5") && !str.contains("6") 
                        && !str.contains("7")  && !str.contains("8"))
                    str += "4";
                if (key.tone.name == null ? str == null : key.tone.name.toLowerCase().equals(str.toLowerCase())) {
                   //System.out.println("Find Tone");
                   key.isSearch = true;
                }
            }
    }

    @Override
    public String getName() {
        return "All";
    }
    
}
