package ulaval.ui;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import ulaval.controller.GaudrophoneController;
import ulaval.models.Key;
import ulaval.models.Mode;

/**
 *  Listener utiliser pour gerer la saisi du son de la touche dans la barre de 
 *  propriété
 * 
 * @author alexis
 */
public class MyKeyListener implements KeyListener {
       
            
    public JTextField textField = null;
            
    public Key key = null;
    
    public GaudrophoneController ctrl = null;
    
    /**
     * type de keyListener 0 pour le son de la touche et 1 pour le label
     */
    public int type = 0;
    
    InstrumentPanel InstrumentDisplay = null;
    
    List<Component> components;
    
    JCheckBox checkBokLeft;
    
    JCheckBox checkBokUp;
    
    JCheckBox checkBokRight;
    
    JCheckBox checkBokDown;
            
    @Override
    public void keyPressed(KeyEvent ke) {
    }

    @Override
    public void keyTyped(KeyEvent ke) {
    }

    /**
     * Modifie la valeur de la touche selectionné
     * 
     * @param ke 
     *          KeyEvent
     */
    @Override
    public void keyReleased(KeyEvent ke) {
        //System.out.println("ulaval.ui.MyKeyListener.keyReleased() 1 " + type);
        if (textField == null || textField.getText() == null
                 || ke == null || ctrl == null || key == null
                 || textField == null)
             return;
        String str = textField.getText();
        switch (type) {
            case 0:
                key.tone.name = str;
                ctrl.editInstrument(key, Mode.EDIT.ordinal());
                break;
            case 1:
                key.label = str;
                ctrl.editInstrument(key, Mode.EDIT.ordinal());
                if (InstrumentDisplay != null)
                    InstrumentDisplay.repaint();
                
                break;
            case 2:
                if (key.showDownBordure)
                    key.bordureSizeDown = str;
                else if (key.showLeftBordure)
                    key.bordureSizeLeft = str;
                else if (key.showUpBordure)
                    key.bordureSizeUp = str;
                else if (key.showRightBordure) {
                    key.bordureSizeRight = str;   
                } else
                {
                    key.bordureSizeRight = str;
                    key.bordureSizeUp = str;
                    key.bordureSizeLeft = str;
                    key.bordureSizeDown = str;
                }
                ctrl.editInstrument(key, Mode.EDIT.ordinal());
                if (InstrumentDisplay != null)
                    InstrumentDisplay.repaint();
                
                break;
            case 3:
                // pour la persistance
                try {
                    key.persistance = Integer.parseInt(str);
                    ctrl.editInstrument(key, Mode.EDIT.ordinal());
                //    if (InstrumentDisplay != null)
                //        InstrumentDisplay.repaint();
                } catch (NumberFormatException e) {
                    
                }
                break;
            case 4:
                // edit width
                try {
                    key.width = Integer.parseInt(str);
                    ctrl.editInstrument(key, Mode.EDIT.ordinal());
                    ctrl.recalculKeyCoord();
                    if (InstrumentDisplay != null)
                        InstrumentDisplay.repaint();
                } catch (NumberFormatException e) {
                    
                }
                break;
            case 5:
                // edit height
                try {
                    key.height = Integer.parseInt(str);
                    ctrl.editInstrument(key, Mode.EDIT.ordinal());
                    ctrl.recalculKeyCoord();
                    if (InstrumentDisplay != null)
                        InstrumentDisplay.repaint();
                } catch (NumberFormatException e) {
                    
                }
                break;   
            default:
                break;
        }
        
        
        //System.out.println("ulaval.ui.MyKeyListener.keyReleased() 2 " + key.label);
    }
        public void removeListener() {
        int i = 0;
        if (components == null)
            return;
        // parcour les composant et supprime les mouse listener pour remettre les 
        // correspondant et les keyListener
        for (Component component : components) {
              
            if (null != component.getName()) switch (component.getName()) {

                case "DisplayNameTextField":
                case "PersistanceTextField":
                case "SizeBorderTextField":
                case "SetToneTextFiled":
                case "WidthTextField":
                case "HeightTextField":
                    for (KeyListener listener : component.getKeyListeners()) {
                        if (listener instanceof MyKeyListener)
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
                    for (MouseListener mouseListener : component.getMouseListeners()) {
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
    
}
