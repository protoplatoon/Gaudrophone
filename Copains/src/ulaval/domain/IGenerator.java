/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulaval.domain;

import ulaval.models.Instrument;

/**
 * Interface de generator
 * défini un generator d'instrument peut importe le type
 * l'instrument devra implementer cette interface
 * 
 * @author alexis
 */
interface IGenerator {
    
    /**
     * permet de recuperer un instrument de type aleatoire
     * 
     * @return
     *      un instrument
     */     
    Instrument getInstrument();
    
    /**
     * permet de recuperer le nom de l'instrument
     * 
     * @return 
     *         Le nom de l'instrument
     */
    String getName();
    
    /**
     * Set l'id de l'instrument que le generateur utilisera
     * 
     * @param id 
     *          id de l'instrument generé
     */
    void setId(int id);
}
