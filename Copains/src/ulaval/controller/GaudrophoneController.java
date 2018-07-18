/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulaval.controller;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import ulaval.domain.ISearch;
import ulaval.domain.InstrumentManager;
import ulaval.domain.LiveLoopingManager;
import ulaval.domain.MetronomeManager;
import ulaval.domain.PartitionManager;
import ulaval.domain.RepositoryManager;
import ulaval.domain.SearchGenericStrategy;
import ulaval.domain.SearchToneStrategy;
import ulaval.domain.ToneManager;
import ulaval.domain.StrategyGenerator;
import ulaval.models.Coord;
import ulaval.models.Instrument;
import ulaval.models.Key;
import ulaval.models.MidiInstrument;
import ulaval.models.Mode;
import ulaval.ui.InstrumentPanel;
import ulaval.domain.Observer;
import ulaval.ui.PlayBar;

/**
 * Gaudrophone Controller parce que il faut que un seul controller qui donne
 * acces au business de l'application
 *
 * Point d'entrée de la vue
 *
 * @author alexis
 */
public class GaudrophoneController {

    public Timer timer = new Timer();

    public Boolean isEditMode = false;

    private final PartitionManager partitionManager = new PartitionManager();

    private final InstrumentManager instrumentManager;

    private final StrategyGenerator generator;

    private final LiveLoopingManager liveLoopingManager = new LiveLoopingManager();

    private List<Instrument> instruments = new ArrayList<>();

    private final RepositoryManager repo = new RepositoryManager();

    private final ToneManager curManager = new ToneManager();

    private boolean mute = false;

    private final MetronomeManager metro = new MetronomeManager();

    Long pressedTime;

    public void changeMute() {
        this.mute = !mute;
    }

    private final List<ISearch> listSearch;

    public GaudrophoneController() {
        generator = new StrategyGenerator();
        instrumentManager = new InstrumentManager();
        listSearch = new ArrayList<>();
        listSearch.add(new SearchGenericStrategy());
        listSearch.add(new SearchToneStrategy());
    }

    /**
     * Joue une note de piano
     *
     * @param notes String ex : "A3" || "G7" || "C#" || "Cb7"
     */
    public void playPianoTone(String notes) {

        Runnable task = () -> {
            String threadName = Thread.currentThread().getName();
            //System.out.println("Hello " + threadName);
            ToneManager t = new ToneManager();
            t.playTones(notes, MidiInstrument.AcousticGrandPiano);
        };
        task.run();
    }

    /**
     * Récupere les instrument enregistrer dans le dossier par default
     *
     * @return List d'instruments enregistrés
     */
    public List<Instrument> getSavedInstrument() {
        // appeler la dal et recuperer la liste des instrument enregistrer
        // pour les envoyer à la vue
        if (instruments.isEmpty()) {
            boolean success = (new File("./instruments")).mkdirs();
            if (!success) {
                // Directory creation failed
                //System.out.println("./instruments already exist");
            }
            openInstrumentFolder().forEach((path) -> {
                Instrument intrumentSave = repo.getInstrument(path);
                System.out.println("Path " + path);
                int id = 0;
                if (intrumentSave != null) {
                    //repo.saveInstrument(iSaveTest);
                    instruments.add(intrumentSave);
                    if (intrumentSave.id > id) {
                        id = intrumentSave.id + 1;
                    }
                    // permet d'incrementé l'id du generator pour repartir 
                    // du plus gros id en fonction des fichier
                    generator.setId(id);
                }
            }); // ajoute deux instrument a chaque fois peut etre enlever 
            //instruments.add(generator.getRandomInstrument());
            //instruments.add(generator.getRandomInstrument());
        }
        return instruments;
    }

    private List<String> openInstrumentFolder() {
        List<String> res = new ArrayList<>();

        File folder = new File("./instruments/");
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                System.out.println(file.getName());
                if (file.getName().endsWith(".serial")) {
                    res.add(file.getAbsolutePath());
                    //System.out.println("ulaval.controller.GaudrophoneController.openInstrumentFolder()");
                }
            }
        }
        return res;
    }

    /**
     * nom du gabarit d'instrument
     *
     * @param instrumentName Exemple : guitar, piano
     * @return Instrument generer aléatoirement
     */
    public Instrument getInstrument(String instrumentName) {
        if (instrumentName.length() > 0) {
            return generator.getInstrument(instrumentName);
        }
        return generator.getRandomInstrument();
    }

    /**
     * nom du gabarit d'instrument
     *
     * @param id
     *
     * @return Instrument generer aléatoirement
     */
    public Instrument getInstrument(int id) {
        int index = 0;
        for (Instrument instr : getSavedInstrument()) {
            if (index == id) {
                return instr;
            }
            index++;
        }
        return generator.getRandomInstrument();
    }

    /**
     * Défini l'instrument courant en fonction du nom de celui-ci
     *
     * @param instrumentName
     */
    public void setInstrument(String instrumentName) {
        Instrument instrument = getInstrumentByName(instrumentName);
        if (instrument != null) {
            instrumentManager.setCurInstrument(instrument);
            //this.recalculKeyCoord();
        }
    }

    /**
     * Modifie l'instrument courrant - Ajoute une touche - Edit une touche -
     * Delete une touche - rename instrument
     *
     * @param key
     * @param mode 1 => add 2 => edit Key 3 => del Key tu peux aussi utiliser
     * l'enumeration mode
     * @return
     */
    public boolean editInstrument(Key key, int mode) {
        Instrument selectedInstrument = instrumentManager.getInstrument();
        //System.out.println("Instru : " + selectedInstrument);
        if (selectedInstrument == null || key == null || selectedInstrument.id == -1) {
            System.out.println("Select Instrument !!");
            return false;
        }
        //System.out.println("size : " + this.instrumentManager.instrumentPanelSize.width
        //    + ", " + this.instrumentManager.instrumentPanelSize.height);

        //if (key.posX >= this.instrumentManager.instrumentPanelSize.width
        //        || key.posY >= this.instrumentManager.instrumentPanelSize.height) {
        //    System.err.println("Key out of screen !!!!!");
        // faut voir ^^
        //    return false;
        //}
        boolean res = false;
        //if (key != null)
        //    System.out.println("Instrument : " + selectedInstrument.name
        //        + " Key : " + key.id + " coord : (x,y) : ("
        //        + key.posX + "," + key.posY + ")");
        instrumentManager.setCurInstrument(selectedInstrument);
        switch (mode) {
            case 0:
                instrumentManager.addKey(selectedInstrument, key);
                repo.saveInstrument(getCurrentInstrument());
                break;
            case 1:
                res = instrumentManager.editKey(selectedInstrument, key);
                // serialise l'instrument courant
                if (res == true) {
                    repo.saveInstrument(getCurrentInstrument());
                }
                break;
            case 2:
                instrumentManager.delKey(selectedInstrument, key);
                repo.saveInstrument(getCurrentInstrument());
                break;
            default:
                break;
        }
        return res;
    }

    public Instrument getCurrentInstrument() {
        //this.instrumentManager.recalcul();
        return this.instrumentManager.getInstrument();
    }

    /**
     * Sauvegarde l'instrument en cour de modification
     */
    public void saveInstrument() {
        this.instrumentManager.saveInstrument();
    }

    public void addInstrument() {
        //generator.addSerialiseInstrument();

        Instrument instrument = generator.getInstrument("Instrument");
        repo.saveInstrument(instrument);
        instruments.add(instrument);
    }

    /**
     * Gabarit d'instrument (Guitare, Piano, etc ...)
     *
     * @param gabarit
     */
    public void addInstrument(String gabarit) {
        //generator.addSerialiseInstrument();
        Instrument instrument = generator.getInstrument(gabarit);
        repo.saveInstrument(instrument);
        instruments.add(instrument);
    }

    /**
     * Gabarit d'instrument (Guitare, Piano, etc ...)
     *
     * @param gabarit
     */
    public void addInstrument(int gabarit) {
        //generator.addSerialiseInstrument();
        Instrument instrument = generator.getInstrument(gabarit);
        System.out.println("Add : " + instrument);
        repo.saveInstrument(instrument);
        instruments.add(instrument);
    }

    /**
     *
     * @param x
     * @param y
     * @return
     */
    public Coord tranformCoordPixelToRelative(int x, int y) {
        Coord coord = new Coord() {
            {
                x = 1;
                y = 1;
            }
        };
        return coord;
    }

    /**
     * Permet de notifier la vue des changement fais dans la class de type
     * InstrumentPrinterObserver cf : pattern observer
     */
    /*
    public void registerObserverInstrument(InstrumentPrinterObserver instrumentPrinterObserver) {
        if (instrumentPrinterObserver != null)
            this.instrumentManager.registerObserver(instrumentPrinterObserver);
    }
     */
    public Instrument getInstrumentByName(String name) {
        for (Instrument instrument : getSavedInstrument()) {
            if (name != null && instrument != null
                    && name.equals(instrument.name)) {
                return instrument;
            }
        }
        return new Instrument() {
            {
                name = "FailInstrument";
                id = -1;
            }
        };
    }

    public Key getKey(int x, int y) {
        return instrumentManager.getKey(x, y);
    }

    public void playKey(Key curSelectedKey) {
        if (mute) {
            return;
        }
        if (curSelectedKey != null) {
            
            if (curSelectedKey.tone.samplePath != null
                    && curSelectedKey.tone.samplePath.endsWith(".wav")) {
                curManager.playWav(curSelectedKey.tone.samplePath);
            } else if (curSelectedKey.tone != null && !"".equals(curSelectedKey.tone.name)) {
                curManager.playTones(curSelectedKey.tone.name, curSelectedKey.tone.timbre);
            }
        }
    }

    public void setKeyPressed(int x, int y) {
        Key currentKey = getKey(x, y);

        if (currentKey != null) {

            pressedTime = System.currentTimeMillis();

//            curManager = new ToneManager();
            playKey(currentKey);
            currentKey.isSelected = true;
            editInstrument(currentKey, 1);

        }
    }

    public void setKeyReleased(int x, int y) {

        Key currentKey = getKey(x, y);
        if (currentKey != null) {
            currentKey.isSelected = false;
            editInstrument(currentKey, Mode.EDIT.ordinal());

        }

        //System.out.println("Key Released");
        if (curManager != null && currentKey != null) {
            curManager.stopTone(currentKey.persistance);

            try {
                Key key = currentKey.clone();

                if ((int) (pressedTime - System.currentTimeMillis()) > key.persistance) {

                    key.persistance = (int) (pressedTime - System.currentTimeMillis());
                }

                liveLoopingManager.addSong(key);

            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(GaudrophoneController.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        //System.out.println("-------------");
    }

    // change property isSearch a true avec l'instrument manager
    // et notify Vue pour redésiner les touche de couleur
    public void searchTone(String searchField, int type) {
        //ISearch search = new SearchToneStrategy();
        System.out.println("search : " + listSearch.get(type).getName());
        if (instrumentManager != null
                && this.instrumentManager.getInstrument() != null) {
            for (ISearch strategy : listSearch) {
                if (strategy.getName().equals(listSearch.get(type).getName())) {
                    strategy.search(searchField, this.instrumentManager.getInstrument());
                }
            }
        }

    }

    public List<String> getListSearch() {
        List<String> res = new ArrayList<>();
        for (ISearch r : listSearch) {
            res.add(r.getName());
        }
        return res;
    }

    public void setInitialInstrumentPanelSize(Dimension size) {

        this.instrumentManager.instrumentPanelInitialSize = size;
        this.instrumentManager.setInstrumentPanelNewSize(size);
    }

    public void delInstrument(int selectedIndex) {
        if (selectedIndex >= 0 && selectedIndex < instruments.size()) {
            this.repo.delInstrument(instruments.get(selectedIndex).name);
        }
        instruments.clear();
        instruments = getSavedInstrument();
    }

    public void delInstrument(String instrumentName) {
        this.repo.delInstrument(instrumentName);
        instruments.clear();
        instruments = getSavedInstrument();
    }

    public void recalculKeyCoord() {
        this.instrumentManager.recalcul();
    }

    public boolean checkDisplaySize() {
        return this.instrumentManager.isResizable();
    }

    public Key getMaxKeyX() {
        return this.instrumentManager.getMaxX();
    }

    public Key getMaxKeyY() {
        return this.instrumentManager.getMaxY();
    }

    public void removeSearch() {
        if (instrumentManager != null
                && this.instrumentManager.getInstrument() != null) {
            this.instrumentManager.getInstrument().resetSearch();
        }
    }

    public void setNewInstrumentPanelSize(Dimension dimension) {
        this.instrumentManager.setInstrumentPanelNewSize(dimension);
    }

    public int getNewKeyId() {
        if (instrumentManager.getInstrument() == null) {
            return 0;
        }
        int idGenerator = 0;
        for (int i = 0; i < instrumentManager.getInstrument().listKeys.size(); i++) {
            if (instrumentManager.getInstrument().listKeys.get(i).id > idGenerator) {
                idGenerator = instrumentManager.getInstrument().listKeys.get(i).id;
            }
        }
        System.out.println("ulaval.controller.GaudrophoneController.getNewKeyId() " + idGenerator + 1);
        return idGenerator + 1;
    }

    public void startMetro(int tempo) {
        metro.start(tempo);
    }

    public void stopMetro() {
        metro.stop();
    }

    public void importPartition(String absolutePath) {

        partitionManager.importPartition(absolutePath, this.getCurrentInstrument());

    }

    public String getPartition() {

        return partitionManager.getPartition();
    }

    public void playPartition() {

        partitionManager.playPartition(this.getCurrentInstrument());

    }

    public void pausePartition() {

        partitionManager.pausePartition();

    }

    public void stopPartition() {

        partitionManager.stopPartition();

    }

//    public int getPlayingPosition(){
//        
//        return partitionManager.getPlayingPosition();
//    }
    public void reaffectTimbre(int timbre) {
        this.instrumentManager.reaffectTimbre(timbre);
    }

    public Iterable<String> getListGabarit() {
        return generator.getGabarit();
    }

    public void registerDrawer(Observer InstrumentDisplay) {
        this.instrumentManager.registerObserver(InstrumentDisplay);
        this.partitionManager.registerObserver(InstrumentDisplay);
    }

    public void liveLoop(int keyId) {

        liveLoopingManager.manageState(keyId);

    }

    public int getState(int keyId) {

        return liveLoopingManager.getState(keyId);
    }

    public void AvancePartition() {
        partitionManager.avancePartition();
    }

    public void ReculePartition() {
        partitionManager.reculerPartiton();
    }

    public void mutePartition() {
        partitionManager.mute();
    }

    public void registerScrollerObserver(Observer observerScrollbar) {
        partitionManager.registerObserver(observerScrollbar);
    }

    public int getSliderValue() {
        return partitionManager.getSliderValue();
    }

    public void setTimePartition(int value) {
        partitionManager.setCurrentTone(value);
    }

    public void showTonesInPartition() {
        partitionManager.showTonesPartition(this.getCurrentInstrument());
    }

}
