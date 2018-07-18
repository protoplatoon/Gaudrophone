package ulaval.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import ulaval.domain.ToneManager;
import ulaval.models.MidiInstrument;

/**
 *
 * @author elyassefradj
 */
public class MetronomeListener implements ActionListener {
    ToneManager tune = new ToneManager ();
    
     @Override
     public void actionPerformed(ActionEvent e) {
         tune.playTones("A", MidiInstrument.Agogo);
     }
}
