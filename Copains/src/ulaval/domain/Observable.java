/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulaval.domain;

/**
 *
 * Objet observer par un Observateur
 * 
 * @author alexi
 */
public interface Observable {
    
    public void registerObserver(Observer newListener);

    public void unregisterObserver(Observer listener);
    
}
