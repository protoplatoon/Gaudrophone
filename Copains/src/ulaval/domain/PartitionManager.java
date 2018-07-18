/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulaval.domain;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import ulaval.models.Instrument;
import ulaval.models.Mesure;
import ulaval.models.MidiInstrument;
import ulaval.models.Partition;
import ulaval.models.Tone;

/**
 *
 * @author alexis
 */
public class PartitionManager implements Observable {

    private Partition partition = null;

    private Thread lecteur;

    private ToneManager player = new ToneManager();

    private boolean isAlive = false;

    private final List<Observer> observers = new ArrayList<>();

    public Timer timer = new Timer();

    public Boolean playSong = true;

    public int sliderMaxValue = 100;

    private int currentSliderValue;

    private boolean isInPause = false;

    private int currentNotePause = 0;

    private int prevNoteValue = 0;
    private float nbMesure;

    public void importPartition(String absolutePath, Instrument instru) {

        this.partition = new Partition();

        boolean addNewMesure = true;

        String prevLine = "";

        int mesuresTotales = -1;

        int currentMesure = -1;

        prevNoteValue = 0;

        boolean startTone = true;

        int nbBlocs = 0;

        try {
            try (BufferedReader reader = new BufferedReader(new FileReader(absolutePath))) {
                String line;
                while ((line = reader.readLine()) != null) {

                    line = line.trim();

                    if (this.partition.tempo == -1) {
                        try {
                            this.partition.tempo = Integer.parseInt(line);

                        } catch (NumberFormatException e) {

                        }
                    } else if (!line.isEmpty() && !line.contains("_") && !line.contains(",") && !line.contains(".")) {

                        if (!(line.contains("/")) && (line.contains("A") || line.contains("B") || line.contains("C")
                                || line.contains("D") || line.contains("E") || line.contains("F") || line.contains("G")
                                || line.contains("a") || line.contains("b") || line.contains("c") || line.contains("d")
                                || line.contains("e") || line.contains("f") || line.contains("g") || line.contains("X")
                                || line.contains("x"))) {
                            //on a trouvé n mesure

                            if (!line.contains("|")) {

                                if (addNewMesure == true) {
                                    //créer une nouvelle mesure
                                    addNewMesure = false;
                                    mesuresTotales++;
                                    currentMesure++;
                                    Mesure mesure = new Mesure();

                                    mesure.isEndOfLine = true;

                                    this.partition.mesures.add(mesure);

                                    //to-do faire le for pour remplis la mesure en fonction de line
                                    List<Tone> myCurrentToneList = getToneList(line, startTone);

                                    this.partition.mesures.get(currentMesure).tones.add(myCurrentToneList);

                                    if (this.partition.paroles.length() == 0) {
                                        this.partition.paroles += prevLine;
                                    } else {

                                        int salut = this.partition.mesures.get(currentMesure).tones.get(0).size();

                                        for (int i = 0; i < salut; i++) {
                                            this.partition.paroles += " ";
                                        }

                                        this.partition.paroles += prevLine;

                                    }

                                } else {

                                    // on remplit la current mesure
                                    List<Tone> myCurrentToneList = getToneList(line, startTone);

                                    this.partition.mesures.get(currentMesure).tones.add(myCurrentToneList);

                                    //to-do faire le for pour remplis la mesure en fonction de line
                                }

                                // on doit ajouter des nots à une seule mesure
                            } else {

                                int nbMesuresLine = (line.length() - line.replace("|", "").length()) - 1; // 2

                                if (addNewMesure == true) {

                                    addNewMesure = false;

                                    for (int i = 0; i < nbMesuresLine; i++) {
                                        mesuresTotales++;
                                        Mesure mesure = new Mesure();

                                        if (i == nbMesuresLine - 1) {
                                            mesure.isEndOfLine = true;
                                        }

                                        this.partition.mesures.add(mesure);

                                        List<Tone> myCurrentToneList = getMultiToneList(line, startTone, i);

                                        this.partition.mesures.get(mesuresTotales).tones.add(myCurrentToneList);

                                    }

                                    if (this.partition.paroles.length() == 0) {
                                        this.partition.paroles += prevLine;
                                    } else {

                                        int salut = (this.partition.mesures.get(mesuresTotales).tones.get(0).size() * nbMesuresLine) + nbMesuresLine;

                                        for (int j = 0; j < salut; j++) {
                                            this.partition.paroles += " ";
                                        }

                                        this.partition.paroles += prevLine;

                                    }

                                    //to-do faire le for pour remplis la mesure en fonction de line
                                } else {

                                    currentMesure = mesuresTotales + 1 - nbMesuresLine;

                                    for (int j = 0; j < nbMesuresLine; j++) {
                                        //to-do faire le for pour remplis la mesure en fonction de line

                                        List<Tone> myCurrentToneList = getMultiToneList(line, startTone, j);

                                        this.partition.mesures.get(currentMesure).tones.add(myCurrentToneList);

                                        currentMesure++;
                                    }

//                                addNewMesure = true;
                                }

                                //on doit ajouter des notes à n mesures
                            }

                        }

                    } else if (!line.contains("/") && (line.contains("_") || line.contains(",") || line.contains(".")
                            || line.contains("2") || line.contains("3") || line.contains("4") || line.contains("5")
                            || line.contains("6") || line.contains("7") || line.contains("8") || line.contains("9"))) {
                        //on termine n mesure

                        this.partition.hasPersistanceIdentifier = true;

                        if (line.contains("|")) {
                            String replace = line.replace('|', ' ');
                            // il sert a rien ce if !!!
                        }

                        nbBlocs++;

                        adjustPersistance(line, nbBlocs);

                        addNewMesure = true;

                    } else {

                        // ligne vide
                        addNewMesure = true;

                    }
                    //reste à stocker de façon plus permanente les paroles
                    prevLine = line;
                }

                if (this.partition.tempo == -1) {
                    System.err.println("ON A PAS PU TROUVER LE TEMPO");

                } else if (this.partition.mesures.isEmpty()) {
                    System.err.println("ON A PAS PU TROUVER DE MESURE");
                } else if (this.partition.mesures.get(0).tones.isEmpty()) {
                    System.err.println("ON A PAS TROUVÉ DE LISTE DE TONE DANS LA MESURE");
                } else if (this.partition.mesures.get(0).tones.get(0).isEmpty()) {
                    System.err.println("ON A PAS TROUVÉ DE NOTES DANS TA LISTE DE TONE DANS TA MESURE");
                }
            }
            sliderMaxValue = 0;
            if (partition != null && partition.mesures != null) {
                partition.mesures.forEach((m) -> {

                    //m.tones.forEach((listTone) -> {
                    sliderMaxValue += m.tones.get(longestList(m)).size();
                    System.out.println("sliderMAx " + sliderMaxValue);
                    ISearch search = new SearchPartitionStrategy();
                    m.tones.forEach((listTone) -> {
                        for (Tone tone : listTone) {
                            //System.out.println("ulaval " + instru);
                            search.search(tone.name, instru);
                            notifyObserversForInstrument();
                        }
                    });
                    //});
                });
            }
            currentSliderValue = 0;

        } catch (IOException e) {
            System.err.format("Failed reading '%s'.", absolutePath);
        }

    }

    private List<Tone> getToneList(String curLine, boolean startTone) {

        List<Tone> myCurrentToneList = new ArrayList<>();
        String curTone = "";

        for (int i = 0; i < curLine.length(); i++) {
            // process c

            if ((i + 1) < curLine.length() && curLine.charAt(i) == ' ') {

                startTone = false;

                char charac = curLine.charAt(i + 1);
                if (charac != ' ') {
                    startTone = true;
                    i++;
                }

            }

            if (startTone) {

                Tone tone = new Tone();

                while (i < curLine.length() && curLine.charAt(i) != ' ') {

                    curTone += curLine.charAt(i);

                    i++;

                }

                i--;

                startTone = false;

                tone.name = curTone;

                if ("X".equals(tone.name) || "x".equals(tone.name)) {
                    tone.persistance = -1;
                } else {
                    tone.persistance = this.partition.tempo;
                }

                myCurrentToneList.add(tone);

                curTone = "";

            }

        }

        return myCurrentToneList;
    }

    public String getPartition() {

        String partitionString = "";

        String tempo = Integer.toString(this.partition.tempo);

        String paroles = this.partition.paroles;

        partitionString += tempo + "\n" + paroles + "\n";

        int nbMesures = this.partition.mesures.size();
        int nbToneList = this.partition.mesures.get(0).tones.size();
        int nbTone = this.partition.mesures.get(0).tones.get(0).size();

        for (int i = 0; i < nbToneList; i++) {
            for (int j = 0; j < nbMesures; j++) {

                if (j % (nbMesures) == 0 && j != 0) {
                    partitionString += "||";
                } else {
                    partitionString += "|";
                }

                for (int k = 0; k < nbTone; k++) {
                    if (this.partition.mesures.get(j).tones.get(i).size() > 0) {
                        partitionString += " " + this.partition.mesures.get(j).tones.get(i).get(k).name + " ";
                    }
                }
            }
            partitionString += "|\n";
        }

        if (this.partition.hasPersistanceIdentifier) {

            partitionString += "  ";

            float diffPersistance;

            for (int l = 0; l < nbMesures; l++) {

                if (l != 0) {
                    partitionString += " ";
                }

                for (int m = 0; m < nbTone; m++) {

                    if (this.partition.mesures.get(l).tones.get(0).isEmpty()) {
                        continue;
                    } else {
                        diffPersistance = ((float) (this.partition.mesures.get(l).tones.get(0).get(m).persistance) / (float) (this.partition.tempo));
                    }

                    if (diffPersistance > 0.9 && diffPersistance < 1.1) {
                        partitionString += "_  ";
                        for (int i = 0; i < this.partition.mesures.get(l).tones.get(0).get(m).name.length() - 1; i++) {
                            partitionString += " ";
                        }

                    } else if (diffPersistance > 0.15 && diffPersistance < 0.35) {
                        partitionString += ".  ";

                        for (int i = 0; i < this.partition.mesures.get(l).tones.get(0).get(m).name.length() - 1; i++) {
                            partitionString += " ";
                        }
                    } else if (diffPersistance > 0.4 && diffPersistance < 0.6) {
                        partitionString += ",  ";

                        for (int i = 0; i < this.partition.mesures.get(l).tones.get(0).get(m).name.length() - 1; i++) {
                            partitionString += " ";
                        }
                    } else if (diffPersistance > 1.9 && diffPersistance < 2.1) {
                        partitionString += "2  ";

                        for (int i = 0; i < this.partition.mesures.get(l).tones.get(0).get(m).name.length() - 1; i++) {
                            partitionString += " ";
                        }
                    } else if (diffPersistance > 2.9 && diffPersistance < 3.1) {
                        partitionString += "3  ";

                        for (int i = 0; i < this.partition.mesures.get(l).tones.get(0).get(m).name.length() - 1; i++) {
                            partitionString += " ";
                        }
                    } else if (diffPersistance > 3.9 && diffPersistance < 4.1) {
                        partitionString += "4  ";

                        for (int i = 0; i < this.partition.mesures.get(l).tones.get(0).get(m).name.length() - 1; i++) {
                            partitionString += " ";
                        }
                    } else if (diffPersistance > 4.9 && diffPersistance < 5.1) {
                        partitionString += "5  ";

                        for (int i = 0; i < this.partition.mesures.get(l).tones.get(0).get(m).name.length() - 1; i++) {
                            partitionString += " ";
                        }
                    } else if (diffPersistance > 5.9 && diffPersistance < 6.1) {
                        partitionString += "6  ";

                        for (int i = 0; i < this.partition.mesures.get(l).tones.get(0).get(m).name.length() - 1; i++) {
                            partitionString += " ";
                        }
                    } else if (diffPersistance > 6.9 && diffPersistance < 7.1) {
                        partitionString += "7  ";

                        for (int i = 0; i < this.partition.mesures.get(l).tones.get(0).get(m).name.length() - 1; i++) {
                            partitionString += " ";
                        }
                    } else if (diffPersistance > 7.9 && diffPersistance < 8.1) {
                        partitionString += "8  ";

                        for (int i = 0; i < this.partition.mesures.get(l).tones.get(0).get(m).name.length() - 1; i++) {
                            partitionString += " ";
                        }
                    } else if (diffPersistance > 8.9 && diffPersistance < 9.1) {
                        partitionString += "9  ";

                        for (int i = 0; i < this.partition.mesures.get(l).tones.get(0).get(m).name.length() - 1; i++) {
                            partitionString += " ";
                        }
                    } else {
                        partitionString += "   ";

                        for (int i = 0; i < this.partition.mesures.get(l).tones.get(0).get(m).name.length() - 1; i++) {
                            partitionString += " ";
                        }
                    }

                }

            }

        }

        System.out.println(partitionString);

        return partitionString;

    }

    public void playPartition(Instrument instru) {

        ISearch search = new SearchGenericStrategy();

        //lancer dans un thread
//        currentSliderValue = 0;
        if (isAlive) {
            isInPause = false;
            return;
        } else {
            prevNoteValue = 0;
            currentSliderValue = 0;
            notifyObserversForInstrument();
        }

        Runnable task = () -> {
            isAlive = true;
            currentSliderValue = 0;
            String threadName = Thread.currentThread().getName();
            System.out.println("Hello " + threadName);
            if (this.partition == null) {
                return;
            }
            while (isAlive) {
                try {
                    currentNotePause = 0;
                    boolean test = false;
                    for (Mesure mesure : this.partition.mesures) {
                        nbMesure = this.partition.mesures.size();
                        //System.out.println("Play mesure");
                        // endroit ou on est dans la mesure avant de faire pause

                        if (!isAlive || isInPause) {
                            //    prevMesureValue = currentMesurePause;
                            continue;
                        }

                        int theLongestList = longestList(mesure);
                        if (!(theLongestList >= mesure.tones.size()
                                || !isAlive)) {
                            for (int i = 0; i < mesure.tones.get(theLongestList).size(); i++) {
                                currentNotePause++;
                                if (currentNotePause < prevNoteValue) {
                                    //currentSliderValue++;
                                    showTonesPartition(instru);
                                    continue;
                                }
                                List<String> tone = new ArrayList<>();
                                Integer time = 0;
                                for (List<Tone> tones : mesure.tones) {
                                    if (i < tones.size()) {
                                        tone.add(tones.get(i).name);
                                        time = tones.get(i).persistance;
                                        // recuperer ta valeur persistance
                                        // tones.get(i).persistance
                                    }
                                }
                                if (!isAlive || isInPause || getSliderValue() > 100) {
                                    prevNoteValue = 0;
                                    if (i > 0) {
                                        prevNoteValue = i;
                                    }
                                    //prevMesureValue = currentMesurePause;
                                    break;
                                }
                                time = 60000 / time;
                                try {
                                    TimeUnit.MILLISECONDS.sleep(time);
                                    // tone contient la list des note a jouer maintenant
                                    // wait(time)
                                    if (!isAlive || isInPause || getSliderValue() > 100) {
                                        prevNoteValue = 0;
                                        if (i >= 0) {
                                            prevNoteValue = i;
                                        }
                                        //prevMesureValue = currentMesurePause;
                                        break;
                                    }
                                    String res = "";
                                    instru.resetSearch();
                                    notifyObserversForInstrument();
                                    //parcous les accords de chaque ligne

                                    for (String str : tone) {
                                        res += str;
                                        //System.out.println("affiche " + str);
                                        search.search(str, instru);
                                        // refresh screen
                                        //drawer.repaint();
                                        notifyObserversForInstrument();

                                    }
                                    if (!isAlive || isInPause || getSliderValue() > 100) {
                                        prevNoteValue = 0;
                                        if (i >= 0) {
                                            prevNoteValue = i;
                                        }
                                        //prevMesureValue = currentMesurePause;
                                        continue;
                                    }
                                    currentSliderValue++;
                                    //System.out.println("play accord : " + res);
                                    if (!res.contains("x") && !res.contains("X")) {

                                        MidiInstrument defaultTone = MidiInstrument.AcousticGrandPiano;

                                        if (instru.listKeys.size() > 0) {

                                            defaultTone = instru.listKeys.get(0).tone.timbre;

                                        }

                                        if (playSong) {
                                            player.playTones(res, defaultTone);
                                        }
                                    }
                                    // play(tone[0] + tone[1] + ...)
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(PartitionManager.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    instru.resetSearch();
                                    notifyObserversForInstrument();
                                    if (!isInPause && mesure.equals(partition.mesures.get(partition.mesures.size() - 1))) {
                                        isAlive = false;
                                        instru.resetIsInPartition();
                                        notifyObserversForInstrument();
                                    }
                                }
                            }, 500);
                        }
                    }
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(PartitionManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };

        //task.run();
        this.lecteur = new Thread(task);
        this.lecteur.start();
        //this.lecteur.

    }

    private int longestList(Mesure mesure) {

        int longestList = 0;

        for (int i = 0; i < mesure.tones.size(); i++) {

            if (mesure.tones.get(i).size() > longestList) {
                longestList = i;
            }
        }
        return longestList;
    }

    public void pausePartition() {
        // voir comment faire pause au thread 
        isInPause = true;
    }

    public void stopPartition() {
        isAlive = false;
        prevNoteValue = 0;
    }

    private List<Tone> getMultiToneList(String curLine, boolean startTone, int where) {

        List<Tone> myCurrentToneList = new ArrayList<>();
        List<List<Tone>> ToneList = new ArrayList<>();
        String curTone = "";

        for (int i = 0; i < curLine.length(); i++) {
            // process c

            if ((i + 1) < curLine.length() && curLine.charAt(i) == '|') {

                startTone = false;

                char charac = curLine.charAt(i + 1);
                if (charac != ' ') {
                    startTone = true;
                    i++;
                }

            }

            if ((i + 1) < curLine.length() && curLine.charAt(i) == ' ') {

                startTone = false;

                char charac = curLine.charAt(i + 1);
                if (charac != ' ') {
                    startTone = true;
                    i++;
                }

            }

            if (startTone) {

                Tone tone = new Tone();

                while (i < curLine.length() && curLine.charAt(i) != ' ' && curLine.charAt(i) != '|') {

                    curTone += curLine.charAt(i);

                    i++;

                }

                i--;

                startTone = false;

                if (!"".equals(curTone)) {
                    tone.name = curTone;

                    if ("X".equals(tone.name) || "x".equals(tone.name)) {
                        tone.persistance = -1;
                    } else {
                        tone.persistance = this.partition.tempo;
                    }

                    myCurrentToneList.add(tone);
                }

                curTone = "";

            }

            if ((i + 1) < curLine.length() && curLine.charAt(i + 1) == '|') {

                List<Tone> salut = new ArrayList<>(myCurrentToneList);

                ToneList.add(salut);

                myCurrentToneList.clear();
            }

        }

        return ToneList.get(where);

    }

    private void adjustPersistance(String curLine, int nbBlocs) {

        boolean changePersistance = true;

        int nbMesures = this.partition.mesures.size();

        int ratio = nbMesures / nbBlocs;

        int startingMesure = ratio * (nbBlocs - 1);

        int nbTone = this.partition.mesures.get(startingMesure).tones.get(0).size();

        int idxTone = -1;

        for (int i = 0; i < curLine.length(); i++) {
            // process c

            if ((i + 1) < curLine.length() && curLine.charAt(i) == ' ') {

                changePersistance = false;

                char charac = curLine.charAt(i + 1);
                if (charac != ' ') {
                    changePersistance = true;
                    i++;
                }

            }

            if (changePersistance) {

                changePersistance = false;

                idxTone++;

                if (idxTone == nbTone) {
                    idxTone = 0;
                    startingMesure++;
                }

                if (startingMesure < this.partition.mesures.size() && this.partition.mesures.get(startingMesure).tones.get(0).isEmpty()) {
                    startingMesure++;
                }

                switch (curLine.charAt(i)) {
                    case '.':
                        for (int j = 0; j < this.partition.mesures.get(startingMesure).tones.size(); j++) {

                            if (!this.partition.mesures.get(startingMesure).tones.get(j).isEmpty()) {
                                this.partition.mesures.get(startingMesure).tones.get(j).get(idxTone).persistance /= 4;
                            }
                        }
                        break;
                    case ',':
                        for (int j = 0; j < this.partition.mesures.get(startingMesure).tones.size(); j++) {

                            if (!this.partition.mesures.get(startingMesure).tones.get(j).isEmpty()) {
                                this.partition.mesures.get(startingMesure).tones.get(j).get(idxTone).persistance /= 2;
                            }
                        }
                        break;
                    case '_':
                        continue;
                    case '2':
                        for (int j = 0; j < this.partition.mesures.get(startingMesure).tones.size(); j++) {

                            if (!this.partition.mesures.get(startingMesure).tones.get(j).isEmpty()) {
                                this.partition.mesures.get(startingMesure).tones.get(j).get(idxTone).persistance *= 2;
                            }
                        }
                        break;
                    case '3':
                        for (int j = 0; j < this.partition.mesures.get(startingMesure).tones.size(); j++) {

                            if (!this.partition.mesures.get(startingMesure).tones.get(j).isEmpty()) {
                                this.partition.mesures.get(startingMesure).tones.get(j).get(idxTone).persistance *= 3;
                            }
                        }
                        break;
                    case '4':
                        for (int j = 0; j < this.partition.mesures.get(startingMesure).tones.size(); j++) {

                            if (!this.partition.mesures.get(startingMesure).tones.get(j).isEmpty()) {
                                this.partition.mesures.get(startingMesure).tones.get(j).get(idxTone).persistance *= 4;
                            }
                        }
                        break;
                    case '5':
                        for (int j = 0; j < this.partition.mesures.get(startingMesure).tones.size(); j++) {

                            if (!this.partition.mesures.get(startingMesure).tones.get(j).isEmpty()) {
                                this.partition.mesures.get(startingMesure).tones.get(j).get(idxTone).persistance *= 5;
                            }
                        }
                        break;
                    case '6':
                        for (int j = 0; j < this.partition.mesures.get(startingMesure).tones.size(); j++) {

                            if (!this.partition.mesures.get(startingMesure).tones.get(j).isEmpty()) {
                                this.partition.mesures.get(startingMesure).tones.get(j).get(idxTone).persistance *= 6;
                            }
                        }
                        break;
                    case '7':
                        for (int j = 0; j < this.partition.mesures.get(startingMesure).tones.size(); j++) {

                            if (!this.partition.mesures.get(startingMesure).tones.get(j).isEmpty()) {
                                this.partition.mesures.get(startingMesure).tones.get(j).get(idxTone).persistance *= 7;
                            }
                        }
                        break;
                    case 8:
                        for (int j = 0; j < this.partition.mesures.get(startingMesure).tones.size(); j++) {

                            if (!this.partition.mesures.get(startingMesure).tones.get(j).isEmpty()) {
                                this.partition.mesures.get(startingMesure).tones.get(j).get(idxTone).persistance *= 8;
                            }
                        }
                        break;
                    case 9:
                        for (int j = 0; j < this.partition.mesures.get(startingMesure).tones.size(); j++) {

                            if (!this.partition.mesures.get(startingMesure).tones.get(j).isEmpty()) {
                                this.partition.mesures.get(startingMesure).tones.get(j).get(idxTone).persistance *= 9;
                            }
                        }
                        break;
                    default:
                        break;
                }

            }

        }

    }

    @Override
    public void registerObserver(Observer newListener) {
        observers.add(newListener);
    }

    @Override
    public void unregisterObserver(Observer listener) {
        observers.remove(listener);
    }

    public void notifyObserversForInstrument() { // Nouvelle méthode
        observers.forEach((observer) -> {
            observer.notifyUpdated();
        });
    }

    public void mute() {
        playSong = !playSong;
    }

    public int getSliderValue() {
        if (currentSliderValue == 0) {
            return 0;
        }
        return (int) ((float) ((float) currentSliderValue / (float) sliderMaxValue) * 100);
    }

    public void avancePartition() {
    }

    public void reculerPartiton() {
    }

    public void setCurrentTone(int value) {
        // value / 100
        // ?? / maxCurrentValue
        int test = (int) ((float) value * (float) sliderMaxValue / 100);
        System.out.println("ulaval.domain.PartitionManager.setCurrentTone() " + test);
        prevNoteValue = (int) ((float) value * (float) sliderMaxValue / 100 / nbMesure);
        currentSliderValue = (int) ((float) value * (float) sliderMaxValue / 100);
    }

    public void showTonesPartition(Instrument currentInstrument) {
        if (this.partition == null) {
            return;
        }
        ISearch search = new SearchPartitionStrategy();
        partition.mesures.forEach((m) -> {

            m.tones.forEach((listTone) -> {
                for (Tone tone : listTone) {
                    search.search(tone.name, currentInstrument);
                    notifyObserversForInstrument();
                }
            });
        });
    }

}
