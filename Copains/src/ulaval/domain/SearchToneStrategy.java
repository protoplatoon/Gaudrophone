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
public class SearchToneStrategy implements ISearch{

    @Override
    public void search(String tone, Instrument instrument) {
        if (tone != null)
            tone = tone.trim().toUpperCase();
        //System.out.println("search : " + tone);
        if (instrument.listKeys.size() > 0)
            for (Key key : instrument.listKeys) {
                if (key.tone.name == null ? tone == null : key.tone.name.equals(tone)) {
                   //System.out.println("Find Tone");
                   key.isSearch = true;
                }
            }
    }

    @Override
    public String getName() {
        return "Tone";
    }
    
}
