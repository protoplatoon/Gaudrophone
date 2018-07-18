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
public class Mesure implements Serializable {
    
    public List<List<Tone>> tones = new ArrayList<>();
    
    public boolean isEndOfLine;

    @Override
    public String toString() {
        return "Mesure{" + "tones=" + tones + ", isEndOfLine=" + isEndOfLine + '}';
    }
}
