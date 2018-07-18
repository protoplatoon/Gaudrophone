/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulaval.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import ulaval.domain.drawing.InstrumentDrawer;
import ulaval.domain.Observer;

/**
 *
 * @author alexis
 */
public class InstrumentPanel extends JPanel implements Observer {
    
    public Dimension initialDimension;
    
    public MainJFrameView mainWindow;
    
    private InstrumentDrawer mainDrawer;
   
    public InstrumentPanel() {
        
    }
    
    public InstrumentPanel(MainJFrameView mainWindow) {
        this.mainWindow = mainWindow;
        setBorder(new javax.swing.border.BevelBorder(BevelBorder.LOWERED));
        int width = (int) (java.awt.Toolkit.getDefaultToolkit().getScreenSize().width);
        setPreferredSize(new Dimension(width,1));
        setVisible(true);
        int height = (int)(width*0.5);
        initialDimension = new Dimension(width,height);
    }
    @Override
    protected void paintComponent(Graphics g)
    {
        if (mainWindow != null){
            super.paintComponent(g); 
            if (mainDrawer != null)
                mainDrawer.draw(g);
        }
    }
    
    public MainJFrameView getMainWindow(){
        return mainWindow;
    }
    
    public void setMainWindow(MainJFrameView mainWindow){
        this.mainWindow = mainWindow;
        System.out.println("ulaval.ui.InstrumentPanel.setMainWindow()");
        mainDrawer = new InstrumentDrawer(mainWindow.ctrl, initialDimension);

    }
    
    public Dimension getInitialDimension(){
        return initialDimension;
    }
    
    public void setInitialDimension(){
        
    }  

    @Override
    public void notifyUpdated() {
        this.repaint();
        //System.out.println("ulaval.ui.InstrumentPanel.notifyUpdatedInstrument()");
    }
}
