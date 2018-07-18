/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulaval.ui;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import ulaval.controller.GaudrophoneController;
import ulaval.models.Key;
import ulaval.models.Mode;
import ulaval.models.ToneType;

/**
 *
 * Class utilisé pour modifier la taille et supprimer une touche
 * dans la barre de proprété
 * 
 * @author alexi
 */
public class MyMouseKeySizeListener implements MouseListener {
    
    // touche associé a ce listener
    public Key key = null;
    
    // le gaudrophone controller ici sert a acceder a l'instrument courant
    // pour pouvoir le modifier
    public GaudrophoneController ctrl = null;
            
    // ce type correspond a la methode a appeler dans le listener en fonction
    // du bouton qui possedera ce listener 
    /**
     * 1 = augmente la largeur
     * 2 = diminue la largeur
     * 3 = augmente la hauteur
     * 4 = diminue la hauteur
     * 41 = change l'etat d'affichage du label
     * 42 = supprime la touche
     */
    public int type = 1;
    
    // l'instrument display sera initialiser a la création de du listener, 
    // il sert a evité de faire grandir trop la touche
    InstrumentPanel InstrumentDisplay;
    
    List<Component> components = null;
    
    MainJFrameView jFrame;
    
    // checkbox pour le type 41
    JCheckBox checkBok;
    
    // pour le type 36
    JCheckBox MoveCheck;
    
    JCheckBox checkBokLeft;
    
    JCheckBox checkBokUp;
    
    JCheckBox checkBokRight;
    
    JCheckBox checkBokDown;
    
    JTextField text;
    
    // ToneValue
    JComboBox<String> tone;
    // OctaveValue
    JComboBox<String> octave;
    // OtherValue
    JComboBox<String> bemole;
    
    // ColorChooser pour le type 40
    //JColorChooser colorChooser = null;
    
    @Override
    public void mousePressed(MouseEvent e){
        
        if (key == null && type != 36)
             return;
        // plus a droite
        if (type == 1 && key.width + 10 < InstrumentDisplay.getWidth() - 100) {
            key.width += 10;
            key.relativeWidth = key.width * 100 / InstrumentDisplay.getWidth();
            jFrame.registerPropertyButton(key);
            jFrame.ctrl.recalculKeyCoord();
        }else if (type == 0 && key.width - 15 > 0) {
            // moins a droite
            key.width -= 10;
            key.relativeWidth = key.width * 100 / InstrumentDisplay.getWidth();
            jFrame.registerPropertyButton(key);
            jFrame.ctrl.recalculKeyCoord();
        }else if (type == 2 && key.height + 5 < InstrumentDisplay.getHeight()){
            // plus en haut
            //key.posY -= 1;
            //key.relativePosY = key.posY * 100 / InstrumentDisplay.getHeight();
            key.height += 5;
            key.relativeHeight = key.height * 100 / InstrumentDisplay.getHeight();
            jFrame.registerPropertyButton(key);
            jFrame.ctrl.recalculKeyCoord();
        } else if (type == 3 && key.height - 10 > 0) {
            // moins en haut
            //key.posY += 1;
            //key.relativePosY = key.posY * 100 / InstrumentDisplay.getHeight();
            key.height -= 5;
            key.relativeHeight = key.height * 100 / InstrumentDisplay.getHeight();
            jFrame.registerPropertyButton(key);
            jFrame.ctrl.recalculKeyCoord();
        } else if (type == 41) {
            // 41 pour le show label ou non
            System.out.println("change print Label ");
            if (key.printLabel == true)
                key.printLabel = false;
            else
                key.printLabel = true;
            checkBok.setSelected(!key.printLabel);
        } else if (type == 40) {
            // 40 pour les couleurs de la bordure par exemple
            // variable utiliser pour differencier qui utilise le colorChooser
            // (la touche ou la bordure 40 => touche 39 => bordure)
            jFrame.isNotBorderColor = false;
        } else if (type == 39) {
            jFrame.isNotBorderColor = true;
        } else if (type == 38) {
            // type 38 pour gerer le bouton modifier l'image d'une touche
            JFileChooser FileChooser = jFrame.getJFileChooser();
            int returnVal = FileChooser.showOpenDialog(jFrame);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = FileChooser.getSelectedFile();
                System.out.println("file = " + file.getAbsolutePath());
                if (file.getAbsolutePath().endsWith(".jpg") || 
                        file.getAbsolutePath().endsWith(".png")) {
                    key.image = file.getAbsoluteFile().getAbsolutePath();
                        
                }
            } else {
                System.out.println("File access cancelled by user.");
            }
        } else if (type == 37) {
            // type 37 pour gerer le bouton modifier le son d'une touche par un fichier wav
            JFileChooser FileChooser = jFrame.getJFileChooser();
            int returnVal = FileChooser.showOpenDialog(jFrame);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = FileChooser.getSelectedFile();
                System.out.println("file = " + file.getAbsolutePath());
                if (file.getAbsolutePath().endsWith(".wav")) {
                    key.tone.type = ToneType.Sample;
                    key.tone.samplePath = file.getAbsolutePath();
                }
            } else {
                System.out.println("File access cancelled by user.");
            }
        } else if (type == 36) {
            // 36 pour rendre les touche deplacable ou non
            if (jFrame.isMovable == true) {
                jFrame.isMovable = false;
                MoveCheck.setSelected(false);
            } else {
                jFrame.isMovable = true;
                MoveCheck.setSelected(true);
            }
            return;
        } else if (type == 35) {
            // 35 down bordure
            if (key.showDownBordure == true) {
                key.showDownBordure = false;
            }
            else {
                key.showLeftBordure = false;
                key.showUpBordure = false;
                key.showRightBordure = false;
                key.showDownBordure = true;
                text.setText(key.bordureSizeDown);
            }
            checkBokDown.setSelected(key.showDownBordure);
            checkBokLeft.setSelected(key.showLeftBordure);
            checkBokRight.setSelected(key.showRightBordure);
            checkBokUp.setSelected(key.showUpBordure);
            
        }else if (type == 34) {
            // right bordure
            if (key.showRightBordure == true)
                key.showRightBordure = false;
            else{
                key.showLeftBordure = false;
                key.showUpBordure = false;
                key.showRightBordure = true;
                key.showDownBordure = false;
                text.setText(key.bordureSizeRight);
            }
            checkBokDown.setSelected(key.showDownBordure);
            checkBokLeft.setSelected(key.showLeftBordure);
            checkBokRight.setSelected(key.showRightBordure);
            checkBokUp.setSelected(key.showUpBordure);
        }else if (type == 33) {
            // up bordure
            if (key.showUpBordure == true)
                key.showUpBordure = false;
            else {
                key.showLeftBordure = false;
                key.showUpBordure = true;
                key.showRightBordure = false;
                key.showDownBordure = false;
                text.setText(key.bordureSizeUp);
            }
            checkBokDown.setSelected(key.showDownBordure);
            checkBokLeft.setSelected(key.showLeftBordure);
            checkBokRight.setSelected(key.showRightBordure);
            checkBokUp.setSelected(key.showUpBordure);
        }else if (type == 32) {
            // left bordure
            if (key.showLeftBordure == true)
                key.showLeftBordure = false;
            else {
                key.showLeftBordure = true;
                key.showUpBordure = false;
                key.showRightBordure = false;
                key.showDownBordure = false;
                text.setText(key.bordureSizeLeft);
            }
            checkBokDown.setSelected(key.showDownBordure);
            checkBokLeft.setSelected(key.showLeftBordure);
            checkBokRight.setSelected(key.showRightBordure);
            checkBokUp.setSelected(key.showUpBordure);
        } else if (type == 31) {
            // comboBox Note 
            
        } else if (type == 30) {
            // octave Note
            
        } else if (type == 29) {
            // bemole Note 
            
        }
        
        // suppression button
        if (type == 42) {
            // alors la on doit supprimer les listener de toute les touche ^^ 
            // c'est pas cool peut etre inutile mtn a voir ^^
            ctrl.editInstrument(key, Mode.DEL.ordinal());
            jFrame.curSelectedKey = null;
            removeAllListener();
            removeAllPropValue();
        }
        else 
            ctrl.editInstrument(key, Mode.EDIT.ordinal());
        InstrumentDisplay.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent me) {
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }

     private void removeAllListener() {
      int i = 0;

        // parcour les composant et supprime les mouse listener pour remettre les 
        // correspondant et les keyListener
        for (Component component : components) {
              
            if (null != component.getName()) switch (component.getName()) {
                case "HeightTextField":
                case "WidthTextField":
                case "DisplayNameTextField":
                case "PersistanceTextField":
                case "SizeBorderTextField":
                case "SetToneTextFiled":
                    for (KeyListener listener : component.getKeyListeners()) {
                        component.removeKeyListener(listener);
                    }
                    // listener pour modifier le label de la touche
                    break;
                case "ToneList":
                    //System.out.println("set ToneList Listener");
                    JComboBox<?> combo = null;
                    if (component instanceof JComboBox<?>)
                        combo = (JComboBox<?>)component;
                    if (combo == null)
                        break;
                    for (ActionListener actionListener : combo.getActionListeners()) {
                        combo.removeActionListener(actionListener);
                    }
                    break;
                case "TonePlusRightButton":
                //case "BorderColorButton":
                case "ToneMinusRightButton":
                case "TonePlusTopButton":
                case "ToneMinusTopButton":
                case "ColorChooser":
                case "PrintLabelCheckBox":
                case "OpenSampleFileChooser":
                case "BorderColorButton":
                case "OpenFileChooser":
                case "ShowBordureDown":
                case "ShowBordureUp":
                case "ShowBordureRight":
                case "ShowBordureLeft":
                    for (MouseListener mouseListener : component.getMouseListeners()) {
                        if (mouseListener instanceof MyMouseKeySizeListener)
                            component.removeMouseListener(mouseListener);
                        //System.out.println("supprimer listener " + component.getName());
                    }

                    break;
                
                default:
                    
                    break;
            }
            i++;
        }
    }
    /**
     * Doit supprimer toute les valeur dans la 
     * barre de propriete 
     * remettre les champs par default
     */
    private void removeAllPropValue() {
      int i = 0;

        // parcour les composant et supprime les mouse listener pour remettre les 
        // correspondant et les keyListener
        for (Component component : components) {
              
            if (null != component.getName()) switch (component.getName()) {
                case "SetToneTextField":
                case "DisplayNameTextField":
                case "PersistanceTextField":
                case "SizeBorderTextField":
                case "HeightTextField":
                case "WidthTextField":
                    JTextField text = (JTextField) component;
                    text.setText("");
                    break;
                default:
                    
                    break;
            }
            i++;
        }
    }
   
}
