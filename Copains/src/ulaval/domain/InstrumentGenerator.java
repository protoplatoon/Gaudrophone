/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulaval.domain;

import ulaval.models.Instrument;

/**
 *
 * @author alexi
 */
public class InstrumentGenerator implements IGenerator{
    

    private Instrument instrument = new Instrument();
    
    @Override
    public Instrument getInstrument() {
        
        System.out.println("new Instru");
        instrument.name = "instrument" + instrument.id;
        
        return instrument;
    }
    

    @Override
    public String getName() {
        return "Instrument";
    }

    @Override
    public void setId(int id) {
        instrument = new Instrument();
        instrument.id = id;
    }
}
