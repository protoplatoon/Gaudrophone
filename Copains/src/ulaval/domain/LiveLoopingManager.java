/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulaval.domain;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import ulaval.models.Key;
import ulaval.models.MidiInstrument;

/**
 *
 * @author alexis
 */
public class LiveLoopingManager {

    List<Entry<Long, Key>> loop0 = new ArrayList<>();
    List<Entry<Long, Key>> loop1 = new ArrayList<>();
    List<Entry<Long, Key>> loop2 = new ArrayList<>();
    List<Entry<Long, Key>> loop3 = new ArrayList<>();
    List<Entry<Long, Key>> loop4 = new ArrayList<>();
    List<Entry<Long, Key>> loop5 = new ArrayList<>();
    List<Entry<Long, Key>> loop6 = new ArrayList<>();
    List<Entry<Long, Key>> loop7 = new ArrayList<>();
    List<Entry<Long, Key>> loop8 = new ArrayList<>();
    List<Entry<Long, Key>> loop9 = new ArrayList<>();

    private Thread lecteur;

    private ToneManager player;

    Long startTime0;
    Long startTime1;
    Long startTime2;
    Long startTime3;
    Long startTime4;
    Long startTime5;
    Long startTime6;
    Long startTime7;
    Long startTime8;
    Long startTime9;

    public boolean recordMode0 = false;
    public boolean recordMode1 = false;
    public boolean recordMode2 = false;
    public boolean recordMode3 = false;
    public boolean recordMode4 = false;
    public boolean recordMode5 = false;
    public boolean recordMode6 = false;
    public boolean recordMode7 = false;
    public boolean recordMode8 = false;
    public boolean recordMode9 = false;

    public boolean toReset0 = false;
    public boolean toReset1 = false;
    public boolean toReset2 = false;
    public boolean toReset3 = false;
    public boolean toReset4 = false;
    public boolean toReset5 = false;
    public boolean toReset6 = false;
    public boolean toReset7 = false;
    public boolean toReset8 = false;
    public boolean toReset9 = false;

    // 9 cle d'enregistrement
    // pour chaque clé
    // ajouter une Key en fonction de la key et de la durer depuis le derniere appel
    /**
     * Methode appeler par le controller quand on est entrain d'enregistrer un
     * live ajoute le sons a la piste du livelooping en fonction du time
     *
     * @param touch touche pressé pour récuperer le son ou la piste audio a
     * jouer dans la futur loop
     * @param time temps depuis le dernier appel permet de reproduire le tempo
     * gaudrophoniste
     */
    public void addSong(Key touch) {

        if (recordMode0) {
            Long timeBeforeNote0 = System.currentTimeMillis() - startTime0;
            startTime0 = System.currentTimeMillis();
            loop0.add(new AbstractMap.SimpleEntry<>(timeBeforeNote0, touch));
        }

        if (recordMode1) {
            Long timeBeforeNote1 = System.currentTimeMillis() - startTime1;
            startTime1 = System.currentTimeMillis();
            loop1.add(new AbstractMap.SimpleEntry<>(timeBeforeNote1, touch));
        }

        if (recordMode2) {
            Long timeBeforeNote2 = System.currentTimeMillis() - startTime2;
            startTime2 = System.currentTimeMillis();
            loop2.add(new AbstractMap.SimpleEntry<>(timeBeforeNote2, touch));
        }

        if (recordMode3) {
            Long timeBeforeNote3 = System.currentTimeMillis() - startTime3;
            startTime3 = System.currentTimeMillis();
            loop3.add(new AbstractMap.SimpleEntry<>(timeBeforeNote3, touch));

        }

        if (recordMode4) {
            Long timeBeforeNote4 = System.currentTimeMillis() - startTime4;
            startTime4 = System.currentTimeMillis();
            loop4.add(new AbstractMap.SimpleEntry<>(timeBeforeNote4, touch));
        }

        if (recordMode5) {
            Long timeBeforeNote5 = System.currentTimeMillis() - startTime5;
            startTime5 = System.currentTimeMillis();
            loop5.add(new AbstractMap.SimpleEntry<>(timeBeforeNote5, touch));
        }

        if (recordMode6) {
            Long timeBeforeNote6 = System.currentTimeMillis() - startTime6;
            startTime6 = System.currentTimeMillis();
            loop6.add(new AbstractMap.SimpleEntry<>(timeBeforeNote6, touch));
        }

        if (recordMode7) {

            Long timeBeforeNote7 = System.currentTimeMillis() - startTime7;
            startTime7 = System.currentTimeMillis();
            loop7.add(new AbstractMap.SimpleEntry<>(timeBeforeNote7, touch));

        }

        if (recordMode8) {
            Long timeBeforeNote8 = System.currentTimeMillis() - startTime8;
            startTime8 = System.currentTimeMillis();
            loop8.add(new AbstractMap.SimpleEntry<>(timeBeforeNote8, touch));
        }

        if (recordMode9) {
            Long timeBeforeNote9 = System.currentTimeMillis() - startTime9;
            startTime9 = System.currentTimeMillis();
            loop9.add(new AbstractMap.SimpleEntry<>(timeBeforeNote9, touch));
        }

    }

    /**
     *
     * @param keyId id de la loop a lancer
     */
    public void startLoop(int keyId) {

        switch (keyId) {
            case 10:

                recordMode0 = true;
                startTime0 = System.currentTimeMillis();
                break;
            case 1:

                recordMode1 = true;
                startTime1 = System.currentTimeMillis();
                break;
            case 2:

                recordMode2 = true;
                startTime2 = System.currentTimeMillis();
                break;
            case 3:

                recordMode3 = true;
                startTime3 = System.currentTimeMillis();
                break;
            case 4:

                recordMode4 = true;
                startTime4 = System.currentTimeMillis();
                break;
            case 5:

                recordMode5 = true;
                startTime5 = System.currentTimeMillis();
                break;
            case 6:

                recordMode6 = true;
                startTime6 = System.currentTimeMillis();
                break;
            case 7:

                recordMode7 = true;
                startTime7 = System.currentTimeMillis();
                break;
            case 8:

                recordMode8 = true;
                startTime8 = System.currentTimeMillis();
                break;
            case 9:

                recordMode9 = true;
                startTime9 = System.currentTimeMillis();
                break;

        }

    }

    public void playLoop(int keyId) {

        switch (keyId) {
            case 10:

                if (loop0.size() > 0) {

                    listPlayer(loop0);

                    recordMode0 = false;

//                    toReset0 = true;
                }

                break;
            case 1:

                if (loop1.size() > 0) {

                    listPlayer(loop1);

                    recordMode1 = false;

//                    toReset1 = true;
                }
                break;
            case 2:

                if (loop2.size() > 0) {
                    listPlayer(loop2);

                    recordMode2 = false;
//                    toReset2 = true;
                }
                break;
            case 3:

                if (loop3.size() > 0) {

                    listPlayer(loop3);

                    recordMode3 = false;
//                    toReset3 = true;
                }
                break;
            case 4:

                if (loop4.size() > 0) {

                    listPlayer(loop4);

                    recordMode4 = false;
//                    toReset4 = true;

                }
                break;
            case 5:

                if (loop5.size() > 0) {
                    listPlayer(loop5);

                    recordMode5 = false;
                }
                break;
            case 6:

                if (loop6.size() > 0) {
                    listPlayer(loop6);

                    recordMode6 = false;
//                    toReset6 = true;
                }
                break;
            case 7:

                if (loop7.size() > 0) {
                    listPlayer(loop7);

                    recordMode7 = false;
//                    toReset7 = true;
                }
                break;
            case 8:

                if (loop8.size() > 0) {
                    listPlayer(loop8);

                    recordMode8 = false;
//                    toReset8 = true;
                }
                break;
            case 9:

                if (loop9.size() > 0) {
                    listPlayer(loop9);

                    recordMode9 = false;
//                    toReset9 = true;
                }
                break;

        }

    }

    private void listPlayer(List<Entry<Long, Key>> loop) {

        Runnable task = () -> {
            String threadName = Thread.currentThread().getName();

            player = new ToneManager();

            MidiInstrument timbre = MidiInstrument.AcousticGrandPiano;

            while (true) {

                for (int i = 0; i < loop.size(); i++) {

                    Long time = loop.get(i).getKey();

                    try {
                        TimeUnit.MILLISECONDS.sleep(loop.get(i).getKey());

                    } catch (InterruptedException ex) {
                        Logger.getLogger(LiveLoopingManager.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }

                    if (loop.size() > 0) {
                        timbre = loop.get(i).getValue().tone.timbre;

                        player.playTones(loop.get(i).getValue().tone.name, timbre);

                        player.stopTone(loop.get(i).getValue().tone.persistance);

                    }
                }

            }

        };

        this.lecteur = new Thread(task);
        this.lecteur.start();

    }

    public void manageState(int keyId) {

        switch (keyId) {

            case 10:
                if (recordMode0 && !toReset0) {

                    this.playLoop(keyId);

                } else if (toReset0) {

                    loop0.clear();
//                    toReset0 = false;
//                    recordMode0 = true;

                } else {
                    this.startLoop(keyId);

                }
                break;

            case 1:
                if (recordMode1) {

                    this.playLoop(keyId);

                } else if (toReset1) {

                    loop1.clear();
//                    toReset1 = false;
//                    this.startLoop(keyId);

                } else {
                    this.startLoop(keyId);
                }
                break;

            case 2:
                if (recordMode2) {

                    this.playLoop(keyId);

                } else if (toReset2) {

                    loop2.clear();
//                    toReset2 = false;
//                    recordMode2 = true;

                } else {
                    this.startLoop(keyId);
                }
                break;

            case 3:
                if (recordMode3 && !toReset3) {

                    this.playLoop(keyId);

                } else if (toReset3) {

                    loop3.clear();
//                    toReset3 = false;
//                    recordMode3 = true;

                } else {
                    this.startLoop(keyId);
                }
                break;

            case 4:
                if (recordMode4 && !toReset4) {

                    this.playLoop(keyId);

                } else if (toReset4) {

                    loop4.clear();
//                    toReset4 = false;
//                    recordMode4 = true;

                } else {
                    this.startLoop(keyId);
                }
                break;

            case 5:
                if (recordMode5 && !toReset5) {

                    this.playLoop(keyId);

                } else if (toReset5) {

                    loop5.clear();
//                    toReset5 = false;
//                    recordMode5 = true;

                } else {
                    this.startLoop(keyId);
                }
                break;

            case 6:
                if (recordMode6 && !toReset6) {

                    this.playLoop(keyId);

                } else if (toReset6) {

                    loop6.clear();
//                    toReset6 = false;
//                    recordMode6 = true;

                } else {
                    this.startLoop(keyId);
                }
                break;

            case 7:
                if (recordMode7 && !toReset7) {

                    this.playLoop(keyId);

                } else if (toReset7) {

                    loop7.clear();
//                    toReset7 = false;
//                    recordMode7 = true;

                } else {
                    this.startLoop(keyId);
                }
                break;

            case 8:
                if (recordMode8 && !toReset8) {

                 
                    this.playLoop(keyId);

                } else if (toReset8) {

                    loop8.clear();
//                    toReset8 = false;
//                    recordMode8 = true;

                } else {
                    this.startLoop(keyId);
                }
                break;

            case 9:
                if (recordMode9 && !toReset9) {

                    
                    this.playLoop(keyId);

                } else if (toReset9) {

                    loop9.clear();
//                    toReset9 = false;
//                    recordMode9 = true;

                } else {
                    this.startLoop(keyId);
                }
                break;

        }

    }

    public int getState(int keyId) {

        int state = 0;

        switch (keyId) {

            case 10:

                if (recordMode0) {

                    //rouge
                    state = 2;

                } else if (toReset0) {

                    //jaune
                    state = 0;
                    toReset0 = false;

                } else {

                    //vert
                    state = 1;
                    toReset0 = true;
                }
                break;

            case 1:

                if (recordMode1) {

                    //rouge
                    state = 2;

                } else if (toReset1) {

                    //jaune
                    state = 0;
                    toReset1 = false;

                } else {

                    //vert
                    state = 1;
                    toReset1 = true;
                }
                break;

            case 2:

                if (recordMode2) {

                    //rouge
                    state = 2;

                } else if (toReset2) {

                    //jaune
                    state = 0;
                    toReset2 = false;

                } else {

                    //vert
                    state = 1;
                    toReset2 = true;
                }
                break;

            case 3:

                if (recordMode3) {

                    //rouge
                    state = 2;

                } else if (toReset3) {

                    //jaune
                    state = 0;
                    toReset3 = false;

                } else {

                    //vert
                    state = 1;
                    toReset3 = true;
                }
                break;
            case 4:

                if (recordMode4) {

                    //rouge
                    state = 2;

                } else if (toReset4) {

                    //jaune
                    state = 0;
                    toReset4 = false;

                } else {

                    //vert
                    state = 1;
                    toReset4 = true;
                }
                break;
            case 5:

                if (recordMode5) {

                    //rouge
                    state = 2;

                } else if (toReset5) {

                    //jaune
                    state = 0;
                    toReset5 = false;

                } else {

                    //vert
                    state = 1;
                    toReset5 = true;
                }
                break;
            case 6:

                if (recordMode6) {

                    //rouge
                    state = 2;

                } else if (toReset6) {

                    //jaune
                    state = 0;
                    toReset6 = false;

                } else {

                    //vert
                    state = 1;
                    toReset6 = true;
                }
                break;

            case 7:

                if (recordMode7) {

                    //rouge
                    state = 2;

                } else if (toReset7) {

                    //jaune
                    state = 0;
                    toReset7 = false;

                } else {

                    //vert
                    state = 1;
                    toReset7 = true;
                }
                break;

            case 8:

                if (recordMode8) {

                    //rouge
                    state = 2;

                } else if (toReset8) {

                    //jaune
                    state = 0;
                    toReset8 = false;

                } else {

                    //vert
                    state = 1;
                    toReset8 = true;
                }
                break;

            case 9:

                if (recordMode9) {

                    //rouge
                    state = 2;

                } else if (toReset9) {

                    //jaune
                    state = 0;
                    toReset9 = false;

                } else {

                    //vert
                    state = 1;
                    toReset9 = true;
                }
                break;
        }

        return state;

    }
}
