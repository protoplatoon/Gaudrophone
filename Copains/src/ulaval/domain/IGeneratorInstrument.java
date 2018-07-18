/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulaval.domain;

import ulaval.models.Instrument;

/**
 * class pour le pattern strategy pour la génération d'instrument aléatoire
 * 
 * @author alexis
 */
public interface IGeneratorInstrument {
    
    Instrument getRandomInstrument();
    
}
