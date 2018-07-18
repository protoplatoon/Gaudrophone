/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulaval.models;

import java.awt.Color;
import java.awt.Image;
import java.io.Serializable;

/**
 *
 * @author alexis
 */
public class Key implements Serializable , Cloneable {
    
    // id de la touche
    public int id;
    
    // indique le type de touche 
    public int keyType;
    
    // label ecrit sur la touche
    public String label = "";
    
    // larger
    public Integer width = -1;
    
    public transient boolean isInPartition = false;
    
    // hauteur
    public Integer height = -1;

    // note associé 
    public Tone tone = new Tone();
    
    // défini la forme de la touche si elle sont toute carrer deja
    // posX et posY etant les possitions relative dans le panel du bouton
    public int posX = -1, posY = -1;
    
    public float relativePosX = -1, relativePosY = -1, relativeWidth = -1, relativeHeight = -1;
    
    // permet de savoir si la touche est allumé ou pas
    public transient boolean isSearch = false;
    
    public Color color = new Color(255, 255, 255, 200);
    
    public String image = null;
    
    public Color bordureColor = Color.WHITE;
        
    public String bordureSize = "0";
    
    public boolean printLabel = true;
    
    public Color hoverColor = Color.PINK;
    
    public Integer persistance = 500;
    
    public transient boolean isSelected = false;
    
    public boolean showRightBordure;
    
    public boolean showLeftBordure;
    
    public boolean showUpBordure;
    
    public boolean showDownBordure;
    
    public String bordureSizeDown;
    
    public String bordureSizeLeft;
    
    public String bordureSizeUp;
    
    public String bordureSizeRight;
    
    public transient Image realImage = null;

    @Override
    public String toString() {
        return "Key{" + "id=" + id + ", keyType=" + keyType + ", label=" 
                + label + ", width=" + width + ", height=" + height 
                + ", tone=" + tone + ", posX=" + posX + ", posY=" 
                + posY + ", color=" + color 
                + ", pathImage=" + image + ", bordureColor=" + bordureColor
                + ", printLabel=" + printLabel + ", bordureSize=" + bordureSize
                + ", hoverColor=" + hoverColor + ", persistance=" + persistance + '}';
    }
    
    @Override
    public Key clone() throws CloneNotSupportedException {
		Object o = null;
		try {
			// On récupère l'instance à renvoyer par l'appel de la 
			// méthode super.clone()
			o = super.clone();
		} catch(CloneNotSupportedException cnse) {
			// Ne devrait jamais arriver car nous implémentons 
			// l'interface Cloneable
			cnse.printStackTrace(System.err);
		}
		// on renvoie le clone
		return (Key) o;
	}
    
    // autre chose pour d'autre forme, une enum pour le type de forme par ex
}
