/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulaval.models;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author alexis
 */
public class Partition {
    
    public List<Mesure> mesures = new ArrayList<>();
    
    public int tempo = -1;
    
    public String paroles = "";
    
    public boolean hasPersistanceIdentifier = false;

    //changer le tostring sous un autre format n'est pas encore faite
    @Override
    public String toString() {
        return "Partition{" + "tones=" + mesures + ", tempo=" + tempo + '}';
    }
}
