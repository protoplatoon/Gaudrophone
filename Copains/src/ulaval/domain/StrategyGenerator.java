/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulaval.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import ulaval.models.Instrument;

/**
 * Class permettant de définir les differentes strategie d'instrument
 * Elle nous permet de recuperer un instrument selon un nom de gabarit 
 * ou de generer un instrument aléatoirement
 * 
 * @author alexis
 */
public class StrategyGenerator {
    
    // liste des instrument generable (liste des strategy)
    private final List<IGenerator> strategyList;

    // id de l'instrument generé
    static int id = 0;
    
    /**
     * Contructor Strategy generator
     */
    public StrategyGenerator() {
        this.strategyList = new ArrayList<>();
        strategyList.add(new GuitarGenerator());
        strategyList.add(new PianoGenerator());
        strategyList.add(new InstrumentGenerator());
    }
    
    /**
     * Retourn un instrument aleatoire parmit la liste des generator de la 
     * class
     * 
     * @return un Instrument
     */
    public Instrument getRandomInstrument() {
        // faire un rand sur le nombre de generator dans la liste
        // pour remplacer le 0
        Random r = new Random();
        int randomNumber = r.nextInt(strategyList.size());
        //System.out.println("getRandomInstrument() " + randomNumber);
        id++;
        strategyList.get(randomNumber).setId(id);
        
        return strategyList.get(randomNumber).getInstrument();
    }
    
    /**
     *  Retourn un instrument selon le nom de son gabarit
     * 
     * @param gabarit
     *          nom du gabarit ("Guitare", "Piano", etc ...)
     * @return un nouvel instrument correspondant au gabarit ou null 
     *          si le gabarit n'existe pas
     */
    public Instrument getInstrument(String gabarit) {
        id++;
        for (IGenerator gen : strategyList) {
            if (gen.getName().equals(gabarit)) {
                gen.setId(id);
                
                return gen.getInstrument();
            }
        }
        return null;
    }
    
    /**
     * permet d'incrementé l'id du generator
     */
    public void addSerialiseInstrument() {
        id++;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Iterable<String> getGabarit() {
        List<String> res = new ArrayList<>();
        for (IGenerator iGenerator : strategyList) {
            res.add(iGenerator.getName());
        }
        return res;
    }

    public Instrument getInstrument(int gabarit) {
        //System.out.println("try to get gabarit : " + gabarit);
        id++;
        if (gabarit < strategyList.size() && gabarit >= 0){
            strategyList.get(gabarit).setId(id);
            return strategyList.get(gabarit).getInstrument();
        }
        return null;
    }
}
