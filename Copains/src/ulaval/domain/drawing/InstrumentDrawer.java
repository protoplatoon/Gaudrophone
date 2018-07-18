/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulaval.domain.drawing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Toolkit;
import javax.swing.JComponent;
import ulaval.controller.GaudrophoneController;
import ulaval.models.Instrument;
import ulaval.models.Key;

/**
 *
 * @author alexis
 */
public class InstrumentDrawer extends JComponent {

    private final GaudrophoneController ctrl;

    private final Dimension initialDimension;

    float initRatio = 0;

    private Dimension newSize;

    public InstrumentDrawer(GaudrophoneController controller, Dimension initialDimension) {
        this.ctrl = controller;
        this.initialDimension = initialDimension;
        this.initRatio = (float) initialDimension.width / (float) initialDimension.height;
    }

    public void draw(Graphics g) {
        drawInstrument(g);
    }

    private void drawInstrument(Graphics g) {
        if (ctrl == null) {
            return;
        }
        Instrument i = ctrl.getCurrentInstrument();
        Color searchColor = Color.CYAN;

        if (i != null) {
            //System.out.println("nbKey : " + i.listKeys.size());
            //Random rand = new Random();
            //float r = rand.nextFloat();
            //float d = rand.nextFloat();
            //float b = rand.nextFloat();
            //Color randomColor = new Color(r, d, b);
            //randomColor.brighter();

            boolean resizeKey = false;

            for (Key key : i.listKeys) {
                if (key.posX + key.width / 2 > initialDimension.width
                        || key.posY + key.height / 2 > initialDimension.height) {

                    //System.out.println("key out of screen !!!!");
                    resizeKey = true;
                    continue;
                }
            }

//                System.out.println("redraw with w / h :  " 
//                        + initialDimension.width + " / " + initialDimension.height);
            // parcour la liste des touche pour les afficher a l'ecran
            for (Key key : i.listKeys) {
                Color color = key.hoverColor;
                //if (resizeKey) {
                // key.relativePosX = key.posX * initialDimension.width / 100;
                // key.relativePosY = key.posY * initialDimension.height / 100;

//                   int tmpKeyPosX = (int) key.relativePosX;
//                   int tmpKeyPosY = (int) key.relativePosY;
//                   int tmpKeyWidth = (int) key.relativeHeight;
//                   int tmpKeyHeight = (int) key.relativeWidth;
                int tmpKeyPosX = (int) (key.relativePosX);
                int tmpKeyPosY = (int) (key.relativePosY);
                int tmpKeyWidth = (int) (key.relativeWidth);
                int tmpKeyHeight = (int) (key.relativeHeight);

                //}
                //System.out.println("test : " + key.relativePosX);
                //int tmpKeyrelativeWidth = (int) (key.relativeWidth * initialDimension.width / 100);
                //int tmpKeyrelaveHeight = (int) (key.relativeHeight * initialDimension.width / 100);
                //couleur de la touche
                g.setColor(key.color);
                // surbrillance pour recherche ou clic
                if (key.isInPartition) {
                    g.setColor(Color.GREEN);
                    //System.out.println("change color !!!!!.drawInstrument()");
                }

                if (key.isSearch) {

                    // défini la couleur de la recherche
                    g.setColor(searchColor);
                }
                if (key.isSelected) {
                    // défini la couleur de la surbrillance
                    g.setColor(color);
                }

                // switch sur le type pour savoir quoi dessiner
                // TODO Ajouter le dessin des bordure pour chacune des touche
                // la taille en pixel de la bordure est stocker dans key.bordureSize
                switch (key.keyType) {
                    case 1:
                        // dessine une touche rectangle
                        drawKeyRect(key, g, tmpKeyPosX, tmpKeyPosY, tmpKeyWidth, tmpKeyHeight);
                        drawBordure(key, g, tmpKeyPosX, tmpKeyPosY, tmpKeyWidth, tmpKeyHeight);
                        break;
                    case 2:
                        // dessine une touche ronde ovale
                        g.fillOval(tmpKeyPosX - (tmpKeyWidth / 2),
                                 tmpKeyPosY - (tmpKeyHeight / 2),
                                 tmpKeyWidth, tmpKeyHeight);
                        drawBordure(key, g, tmpKeyPosX, tmpKeyPosY, tmpKeyWidth, tmpKeyHeight);
                        break;
                    case 3:
                        // dessine une touche polygon

                        Polygon p = new Polygon();
                        for (int j = 0; j < 6; j++) {
                            p.addPoint((int) (tmpKeyPosX + (tmpKeyWidth / 2) * Math.cos(j * 2 * Math.PI / 6)),
                                    (int) (tmpKeyPosY + (tmpKeyHeight / 2) * Math.sin(j * 2 * Math.PI / 6)));
                        }
                        // fill border of polygon
                        g.drawPolygon(p);
                        drawBordure(key, g, tmpKeyPosX, tmpKeyPosY, tmpKeyWidth, tmpKeyHeight);
                        //g.fillPolygon(ints, ints1, tmpKeyWidth);
                        break;
                    case 4:
                        // dessine une touche rect 3d
                        g.draw3DRect(tmpKeyPosX - (tmpKeyWidth / 2),
                                 tmpKeyPosY - (tmpKeyHeight / 2),
                                 tmpKeyWidth, tmpKeyHeight, true);
                        drawBordure(key, g, tmpKeyPosX, tmpKeyPosY, tmpKeyWidth, tmpKeyHeight);
                        break;
                    case 5:
                        // dessine une touche je sais pas 
                        //rect en attendant 
                        drawKeyRect(key, g, tmpKeyPosX, tmpKeyPosY, tmpKeyWidth, tmpKeyHeight);
                        drawBordure(key, g, tmpKeyPosX, tmpKeyPosY, tmpKeyWidth, tmpKeyHeight);
                        //g.drawL
                        //key.
                        break;
                }
                // draw label
                //defini la couleur du label
                g.setColor(Color.BLACK);
                String printLabel = key.label;
                if (tmpKeyWidth > 0 && printLabel.length() > 0
                        && tmpKeyWidth / 10 < printLabel.length()) {
                    //System.out.println("tronc printLabel " + tmpKeyWidth / printLabel.length());
                    printLabel = printLabel.substring(0, tmpKeyWidth / 10);
                    if (printLabel.length() > 3) {
                        printLabel += "...";
                    }
                }
                if (key.printLabel) {
                    g.drawString(printLabel, tmpKeyPosX - 3 * printLabel.length(), tmpKeyPosY + 5);
                }

            }
        }

    }

    private void drawBordure(Key key, Graphics g, int tmpKeyPosX, int tmpKeyPosY, int tmpKeyWidth, int tmpKeyHeight) {
        int bordureSizeLeft = 0;
        int bordureSizeUp = 0;
        int bordureSizeDown = 0;
        int bordureSizeRight = 0;
        //couleur de la surbrillance
        Color color = key.hoverColor;
        if (key.bordureSize != null && key.bordureSize.length() > 0) {
            try {
                bordureSizeLeft = Integer.parseInt(key.bordureSizeLeft);
            } catch (NumberFormatException e) {
                //System.err.println(e.getMessage());
            }
            try {
                bordureSizeUp = Integer.parseInt(key.bordureSizeUp);
            } catch (NumberFormatException e) {
                //System.err.println(e.getMessage());
            }
            try {
                bordureSizeRight = Integer.parseInt(key.bordureSizeRight);
            } catch (NumberFormatException e) {
                //System.err.println(e.getMessage());
            }
            try {
                bordureSizeDown = Integer.parseInt(key.bordureSizeDown);
            } catch (NumberFormatException e) {
                //System.err.println(e.getMessage());
            }
        }

        if (bordureSizeLeft > 0) {
            // draw la bordure
            g.setColor(key.bordureColor);
            for (int j = 0; j < bordureSizeLeft; j++) {
                g.drawLine(tmpKeyPosX - (tmpKeyWidth / 2) + j, tmpKeyPosY + (tmpKeyHeight / 2),
                        tmpKeyPosX - (tmpKeyWidth / 2) + j, tmpKeyPosY - (tmpKeyHeight / 2));
            }

        }

        if (bordureSizeRight > 0) {
            // draw la bordure
            g.setColor(key.bordureColor);
            for (int j = 0; j < bordureSizeRight; j++) {
                g.drawLine(tmpKeyPosX + (tmpKeyWidth / 2) - j, tmpKeyPosY + (tmpKeyHeight / 2),
                        tmpKeyPosX + (tmpKeyWidth / 2) - j, tmpKeyPosY - (tmpKeyHeight / 2));
            }

        }
        if (bordureSizeUp > 0) {
            // draw la bordure
            g.setColor(key.bordureColor);
            for (int j = 0; j < bordureSizeUp; j++) {
                g.drawLine(tmpKeyPosX - (tmpKeyWidth / 2), tmpKeyPosY - (tmpKeyHeight / 2) + j,
                        tmpKeyPosX + (tmpKeyWidth / 2), tmpKeyPosY - (tmpKeyHeight / 2) + j);
            }

        }
        if (bordureSizeDown > 0) {
            // draw la bordure
            g.setColor(key.bordureColor);
            for (int j = 0; j < bordureSizeDown; j++) {
                g.drawLine(tmpKeyPosX + (tmpKeyWidth / 2), tmpKeyPosY + (tmpKeyHeight / 2) - j,
                        tmpKeyPosX - (tmpKeyWidth / 2), tmpKeyPosY + (tmpKeyHeight / 2) - j);
            }

        }

    }

    private void drawKeyRect(Key key, Graphics g, int tmpKeyPosX, int tmpKeyPosY, int tmpKeyWidth, int tmpKeyHeight) {
        //couleur de la surbrillance
        Color color = key.hoverColor;
        Color searchColor = Color.CYAN;

        // draw l'image de fond
        if (key.image != null || key.keyType == 5) {
            try {
                //System.out.println("draw image : " + key.pathImage);
                Image img1 = null;
                if (key.image != null) {
                    img1 = Toolkit.getDefaultToolkit().getImage(key.image);
                } else if (key.keyType == 5) {
                    img1 = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/CordesGuitare.jpg"));
                }

                g.drawImage(img1, tmpKeyPosX - (tmpKeyWidth / 2),
                         tmpKeyPosY - (tmpKeyHeight / 2),
                         tmpKeyWidth, tmpKeyHeight, this);
            } catch (Exception e) {
                //System.err.println(e.getMessage());
            }
        } else if (key.realImage != null) {
            try {
                //System.out.println("draw image : " + key.pathImage);
                Image img1 = key.realImage;

                g.drawImage(img1, tmpKeyPosX - (tmpKeyWidth / 2),
                         tmpKeyPosY - (tmpKeyHeight / 2),
                         tmpKeyWidth, tmpKeyHeight, this);
            } catch (Exception e) {
                //System.err.println(e.getMessage());
            }
        }

        g.setColor(key.color);
        if (key.isInPartition) {
            g.setColor(Color.GREEN);
            //System.out.println("change color !!!!!.drawInstrument()");
        }
        if (key.isSearch) {
            // défini la couleur de la recherche
            g.setColor(searchColor);
        }
        if (key.isSelected) {
            // défini la couleur de la surbrillance
            g.setColor(color);
        }

        // exemple de bordure faut faire pareil sur les 
        // autres touches en fonction de leur forme
        g.fillRect(tmpKeyPosX - (tmpKeyWidth / 2),
                 tmpKeyPosY - (tmpKeyHeight / 2),
                 tmpKeyWidth, tmpKeyHeight);

    }
}
