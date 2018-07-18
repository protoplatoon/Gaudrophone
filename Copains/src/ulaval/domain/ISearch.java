/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulaval.domain;

import ulaval.models.Instrument;

/**
 *
 * @author Alexis
 */
public interface ISearch {
 
    void search(String tone, Instrument instrument);
    
    String getName();
    // etc si besoin
}
