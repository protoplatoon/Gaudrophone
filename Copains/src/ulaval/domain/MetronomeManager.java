package ulaval.domain;

import javax.swing.Timer;
import ulaval.ui.MetronomeListener;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.

/**
 *
 * @author elyassefradj
 */
 
 public class MetronomeManager{
      Timer time = null;
  
      public void start(int tempo){    
          int delay = 60000/tempo;
          MetronomeListener monlestener = new MetronomeListener(); 
          time = (new Timer(delay, monlestener));
          time.start();   
      }
      public void stop(){
          if (time != null)
              time.stop();
          
    }
}
