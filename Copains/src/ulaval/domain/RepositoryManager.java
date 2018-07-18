/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulaval.domain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import ulaval.models.Instrument;
import ulaval.models.Partition;

/**
 *
 * @author Ahmed
 */
public class RepositoryManager implements Serializable {

    //Constructor
    public RepositoryManager() {
    }

    //Get un seul instrument
    public Instrument getInstrument(String path) {
        try {
            FileInputStream fis = new FileInputStream(path);
            Instrument instrument1;
            try (ObjectInputStream ois = new ObjectInputStream(fis)) {
                instrument1 = (Instrument) ois.readObject();
            }
            return instrument1;
        } catch (ClassNotFoundException | IOException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    //Get une partion
    public Partition getPartition(String path) {
        try {
            FileInputStream fis = new FileInputStream(path);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Partition partition1 = (Partition) ois.readObject();
            ois.close();
            return (Partition) partition1;
        } catch (ClassNotFoundException | IOException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    //Sauvegarder un instrument
    public void saveInstrument(Instrument i) {
        try {
            if (i == null) {
                return;
            }
            // création d'une personne
            // System.out.println("creation de : " + i);

            // ouverture d'un flux de sortie vers le fichier "personne.serial"
            FileOutputStream fos = new FileOutputStream("instruments/" + i.name + ".serial");

            // création d'un "flux objet" avec le flux fichier
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            try {
                // sérialisation : écriture de l'objet dans le flux de sortie
                oos.writeObject(i);
                // on vide le tampon
                oos.flush();
                //   System.out.println(i + " a ete serialise");
            } finally {
                //fermeture des flux
                if (oos != null) {
                    oos.close();
                }
                if (fos != null) {
                    fos.close();
                }

            }
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
    }

    //Sauvegarder une partition
    public void savePartition(String path, Partition part) {

    }

    public void delInstrument(String instrumentName) {
        // supprimer l'instrument
        System.out.println("supprime : " + instrumentName);
        try {
            File file = new File("./instruments/" + instrumentName + ".serial");
            if (file.delete()) {
                System.out.println(file.getName() + " is deleted!");
            } else {
                System.out.println("Delete operation is failed.");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
