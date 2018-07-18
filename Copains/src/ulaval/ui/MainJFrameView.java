/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulaval.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListDataListener;
import ulaval.controller.GaudrophoneController;
import ulaval.models.Instrument;
import ulaval.models.Key;
import ulaval.models.MidiInstrument;
import ulaval.models.Mode;

/**
 *
 * @author alexis
 */
public class MainJFrameView extends javax.swing.JFrame {

    private int idGenerator = 0;

    /**
     * Panel utiliser pour l'en-tete de l'application
     */
    private final JPanel TitleBar = new TitleBar();

    /**
     * Panel utiliser pour définir les proprieté d'un instrument (Peut etre
     * passer le type en PropertyBar et ajouter les methodes nécéssaire dans
     * PrepertyBar)
     */
    private final PropertyBar propBar = new PropertyBar();

    /**
     * Controller de Larman permettant d'acceder a la couche businness
     */
    public GaudrophoneController ctrl;

    /**
     * Instrument actuellement désiné à l'écran
     */
    Instrument selectedInstrument;

    /**
     * Touche en cours de deplacement ou de lecture
     */
    Key curSelectedKey = new Key();

    private Boolean isInEditMode = false;

    private final int widthWindow = 1250;

    private final int heightWindow = 800;

    private JCheckBox checkBoxShowKeyLabel = null;

    boolean isNotBorderColor = true;

    private boolean isResizable = false;

    MyMouseKeySizeListener myCurrentMouseListener = null;

    // taille initial sur x de la touche la plus loins
    Key maxX = null;
    // pareil sur Y pour savoir si on doit reajuster l'ecran ou pas
    Key maxY = null;

    boolean isMovable = true;
    private Dimension initialPanel;

    private PlayBar PlayBar = new PlayBar();

    private boolean PlayBarIsVisible = true;

    private boolean isStart = false;

    /**
     * Creates new form MainJFrameView
     */
    public MainJFrameView() {
        initComponents();
        initWindow();
        //addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
    }

    /**
     * Initialise la fenetre et la mise en page
     */
    private void initWindow() {
        this.ctrl = new GaudrophoneController();

        // affichage des differents élément
        TitleBar.setVisible(true);
        KeysPanel.setVisible(false);
        ButtonPannel.setVisible(true);
        propBar.setVisible(false);

        // défini le titre de la fenetre
        setTitle("Gaudrophone");
        // windows size

        // défini la taille minimum de la fenetre
//        setMinimumSize(new Dimension(970, 650));
        setMinimumSize(new Dimension(770, 780));
        setSize(widthWindow, heightWindow);

        //setSize(1250, 800);
        // défini le layout de la fenetre pour la mise en page
        setLayout(new BorderLayout());
        // place les diférents panels sur la fenetre
        add(InstrumentDisplay, BorderLayout.CENTER);
        // panel qui contient la liste des instruments placé a l'ouest
        //this.add(NavPanel, BorderLayout.WEST);
        // panel contenant les boutons pour passer au mode création et les 
        // bouton pour lire la partition
        this.add(ButtonPannel, BorderLayout.SOUTH);
        // propbar et la bar de proprieté elle contient les differents element
        // permettant de définir les touches ou l'instrument en mode création
        this.add(propBar, BorderLayout.EAST);
        // en-tete de la fenetre 
        this.add(NavPanel, BorderLayout.NORTH);

        this.add(PlayBar, BorderLayout.EAST);
        PlayBar.setVisible(true);

        PlayBar.mainJFrame = this;
        PlayBar.init();

        addSavedInstrument();
        //ajoute le panel colorChooser a la barre de proprieté
        this.propBar.ColorChooser = ColorChooser;
        this.propBar.SaveColorButton = SaveColorButton;
        this.propBar.CancelColorButton = CancelColorButton;
        this.propBar.jFileChooserPanel = FileChooserPanel;

        this.propBar.FileChooser = FileChooser;

        ColorChooser.setVisible(false);
        SaveColorButton.setVisible(false);
        CancelColorButton.setVisible(false);

        // initialise maxX et maxY si un instrument et selectionné par default
        maxX = ctrl.getMaxKeyX();

        maxY = ctrl.getMaxKeyY();

        FileChooserPanel.setVisible(false);
        //FileChooser.getSelectedFile();

        DefaultComboBoxModel<String> combo = new DefaultComboBoxModel<>();
        for (String searchType : ctrl.getListSearch()) {
            combo.addElement(searchType);
        }
        SearchList.setModel(combo);

        combo = new DefaultComboBoxModel<>();
        for (String searchType : ctrl.getListGabarit()) {
            combo.addElement(searchType);
        }
        GabaritList.setModel(combo);

        // demande au controller la liste des instruments et les convertirs
        // en liste de string lisible par swing
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (Instrument elem : ctrl.getSavedInstrument()) {
            if (elem != null) {
                listModel.addElement(elem.name);
            }
        }

        combo = new DefaultComboBoxModel<>();
        for (MidiInstrument midi : MidiInstrument.values()) {
            combo.addElement(midi.name());
        }
        TimbreList.setModel(combo);

        TimbreLabel.setVisible(false);
        TimbreList.setVisible(false);
        DelInstrumentButton.setVisible(false);

        this.PlayBar.InstrumentDisplay = InstrumentDisplay;

        ctrl.registerDrawer(InstrumentDisplay);

        InstrumentDisplay.setMainWindow(this);
        // donne les coordonnée de la fenetre au drawer
        //InstrumentDisplay.initialDimension.height = 800;
        //InstrumentDisplay.initialDimension.width = 1200;
        //System.out.println("test " + InstrumentDisplay.getHeight() + ", " + InstrumentDisplay.getWidth());
        // JLabel backgroundPicture = new JLabel(new ImageIcon("./toto.jpg"));
        // this.InstrumentDisplay.add(backgroundPicture);
        // backgroundPicture.setBounds(0, 0, InstrumentDisplay.getWidth(), InstrumentDisplay.getHeight());

        // register l'observer d'instrument dans le controller
        // pour refresh l'instrument dans la vue à chaque modification
        // de l'instrument
        //this.ctrl.registerObserverInstrument(this);
        // set look & feel défini le style de l'application        
        //try {
        //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        //UIManager.setLookAndFeel("javax.swing.plaf.metal");
        //for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
        //    if ("Nimbus".equals(info.getName())) {
        //        javax.swing.UIManager.setLookAndFeel(info.getClassName());
        //        break;
        //    }
        //}
        //} catch (ClassNotFoundException | InstantiationException 
        //        | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
        //    java.util.logging.Logger.getLogger(MainJFrameView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        //}
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        NavPanel = new javax.swing.JPanel();
        InstrumentsComboBox = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        TimbreList = new javax.swing.JComboBox<>();
        TimbreLabel = new javax.swing.JLabel();
        DelInstrumentButton = new javax.swing.JButton();
        ButtonPannel = new javax.swing.JPanel();
        CreateModeButton = new javax.swing.JButton();
        SearchLabel = new javax.swing.JLabel();
        SearchTextField = new javax.swing.JTextField();
        MetroLabel = new javax.swing.JLabel();
        MetroTextField = new javax.swing.JTextField();
        MetroActionButton = new javax.swing.JButton();
        SearchList = new javax.swing.JComboBox<>();
        muteButton2 = new javax.swing.JButton();
        HidePanel = new javax.swing.JButton();
        KeysPanel = new javax.swing.JPanel();
        Key1 = new javax.swing.JButton();
        Key2 = new javax.swing.JButton();
        Key3 = new javax.swing.JButton();
        Key5 = new javax.swing.JButton();
        Key4 = new javax.swing.JButton();
        PlayModeButton = new javax.swing.JButton();
        NextGabaritButton = new javax.swing.JButton();
        PrevGabaritButton = new javax.swing.JButton();
        AddInstrumentButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        muteButton = new javax.swing.JButton();
        GabaritList = new javax.swing.JComboBox<>();
        InstrumentDisplay = new ulaval.ui.InstrumentPanel(this);
        ColorChooser = new javax.swing.JColorChooser();
        SaveColorButton = new javax.swing.JButton();
        CancelColorButton = new javax.swing.JButton();
        FileChooserPanel = new javax.swing.JPanel();
        FileChooser = new javax.swing.JFileChooser();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        NavPanel.setBackground(new java.awt.Color(255, 255, 255));

        InstrumentsComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        InstrumentsComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InstrumentsComboBoxActionPerformed(evt);
            }
        });
        InstrumentsComboBox.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                InstrumentsComboBoxPropertyChange(evt);
            }
        });

        jLabel7.setText("Instrument : ");

        TimbreList.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        TimbreList.setName("TimbreList"); // NOI18N
        TimbreList.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                TimbreListItemStateChanged(evt);
            }
        });

        TimbreLabel.setText("timbre :");

        DelInstrumentButton.setFont(new java.awt.Font("Tahoma", 0, 3)); // NOI18N
        DelInstrumentButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/garbage.png"))); // NOI18N
        DelInstrumentButton.setText("-");
        DelInstrumentButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        DelInstrumentButton.setMinimumSize(new java.awt.Dimension(549, 521));
        DelInstrumentButton.setPreferredSize(new java.awt.Dimension(549, 521));
        DelInstrumentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DelInstrumentButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout NavPanelLayout = new javax.swing.GroupLayout(NavPanel);
        NavPanel.setLayout(NavPanelLayout);
        NavPanelLayout.setHorizontalGroup(
            NavPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NavPanelLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(InstrumentsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(99, 99, 99)
                .addComponent(DelInstrumentButton, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(TimbreLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(TimbreList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        NavPanelLayout.setVerticalGroup(
            NavPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NavPanelLayout.createSequentialGroup()
                .addGroup(NavPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(NavPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(NavPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(InstrumentsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7)
                            .addComponent(TimbreList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(TimbreLabel)))
                    .addGroup(NavPanelLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(DelInstrumentButton, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        ButtonPannel.setBackground(new java.awt.Color(255, 255, 255));

        CreateModeButton.setText("Create Mode");
        CreateModeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                CreateModeButtonMouseReleased(evt);
            }
        });

        SearchLabel.setText("search : ");

        SearchTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                SearchTextFieldKeyReleased(evt);
            }
        });

        MetroLabel.setText("metronome :");

        MetroTextField.setText("80");
        MetroTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                MetroTextFieldKeyReleased(evt);
            }
        });

        MetroActionButton.setText("Start");
        MetroActionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MetroActionButtonActionPerformed(evt);
            }
        });

        SearchList.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        SearchList.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                SearchListItemStateChanged(evt);
            }
        });

        muteButton2.setText("mute");
        muteButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                muteButton2ActionPerformed(evt);
            }
        });

        HidePanel.setText("Hide Panel");
        HidePanel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HidePanelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ButtonPannelLayout = new javax.swing.GroupLayout(ButtonPannel);
        ButtonPannel.setLayout(ButtonPannelLayout);
        ButtonPannelLayout.setHorizontalGroup(
            ButtonPannelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ButtonPannelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(CreateModeButton)
                .addGap(125, 125, 125)
                .addGroup(ButtonPannelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(ButtonPannelLayout.createSequentialGroup()
                        .addComponent(SearchLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(SearchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(SearchList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(61, 61, 61)
                        .addComponent(muteButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(ButtonPannelLayout.createSequentialGroup()
                        .addComponent(MetroLabel)
                        .addGap(18, 18, 18)
                        .addComponent(MetroTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(MetroActionButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(HidePanel)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        ButtonPannelLayout.setVerticalGroup(
            ButtonPannelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ButtonPannelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ButtonPannelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(CreateModeButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(SearchLabel)
                    .addComponent(SearchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SearchList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(muteButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ButtonPannelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(MetroLabel)
                    .addGroup(ButtonPannelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(MetroTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(MetroActionButton)
                        .addComponent(HidePanel)))
                .addContainerGap())
        );

        KeysPanel.setBackground(new java.awt.Color(255, 255, 255));

        Key1.setText("rect");
        Key1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                Key1MouseDragged(evt);
            }
        });
        Key1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Key1MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                Key1MouseReleased(evt);
            }
        });

        Key2.setText("rond");
        Key2.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                Key2MouseDragged(evt);
            }
        });
        Key2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Key2MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                Key2MouseReleased(evt);
            }
        });

        Key3.setText("hexa");
        Key3.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                Key3MouseDragged(evt);
            }
        });
        Key3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Key3MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                Key3MouseReleased(evt);
            }
        });

        Key5.setText("...");
        Key5.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                Key5MouseDragged(evt);
            }
        });
        Key5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Key5MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                Key5MouseReleased(evt);
            }
        });

        Key4.setText("rect3d");
        Key4.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                Key4MouseDragged(evt);
            }
        });
        Key4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Key4MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                Key4MouseReleased(evt);
            }
        });

        PlayModeButton.setText("Play Mode");
        PlayModeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                PlayModeButtonMouseReleased(evt);
            }
        });

        NextGabaritButton.setText(">");
        NextGabaritButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NextGabaritButtonActionPerformed(evt);
            }
        });

        PrevGabaritButton.setText("<");
        PrevGabaritButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PrevGabaritButtonActionPerformed(evt);
            }
        });

        AddInstrumentButton.setText("+");
        AddInstrumentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddInstrumentButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Drag & Drop a key :");

        jLabel2.setText("Choose background :");

        jLabel5.setText("Add Instrument");

        muteButton.setText("mute");
        muteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                muteButtonActionPerformed(evt);
            }
        });

        GabaritList.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        GabaritList.setName("GabaritList"); // NOI18N

        javax.swing.GroupLayout KeysPanelLayout = new javax.swing.GroupLayout(KeysPanel);
        KeysPanel.setLayout(KeysPanelLayout);
        KeysPanelLayout.setHorizontalGroup(
            KeysPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, KeysPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(KeysPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(PlayModeButton, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
                    .addComponent(GabaritList, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(6, 6, 6)
                .addComponent(AddInstrumentButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(KeysPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2))
                .addGap(15, 15, 15)
                .addGroup(KeysPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(PrevGabaritButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Key1, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(KeysPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(NextGabaritButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Key2))
                .addGroup(KeysPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(KeysPanelLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(Key3)
                        .addGap(18, 18, 18)
                        .addComponent(Key4)
                        .addGap(18, 18, 18)
                        .addComponent(Key5))
                    .addGroup(KeysPanelLayout.createSequentialGroup()
                        .addGap(186, 186, 186)
                        .addComponent(muteButton)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        KeysPanelLayout.setVerticalGroup(
            KeysPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, KeysPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PlayModeButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(KeysPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(GabaritList)
                    .addComponent(jLabel5)
                    .addComponent(AddInstrumentButton))
                .addContainerGap())
            .addGroup(KeysPanelLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(KeysPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Key1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Key2)
                    .addComponent(Key3)
                    .addComponent(Key4)
                    .addComponent(Key5)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(KeysPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(KeysPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(NextGabaritButton)
                        .addComponent(PrevGabaritButton)
                        .addComponent(muteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        InstrumentDisplay.setBackground(new java.awt.Color(153, 153, 153));
        InstrumentDisplay.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                InstrumentDisplayMouseDragged(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                InstrumentDisplayMouseMoved(evt);
            }
        });
        InstrumentDisplay.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                InstrumentDisplayMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                InstrumentDisplayMouseReleased(evt);
            }
        });
        InstrumentDisplay.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                InstrumentDisplayComponentResized(evt);
            }
        });

        SaveColorButton.setText("Save");
        SaveColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveColorButtonActionPerformed(evt);
            }
        });

        CancelColorButton.setText("Cancel");
        CancelColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelColorButtonActionPerformed(evt);
            }
        });

        FileChooser.setDialogTitle("");
        FileChooser.setInheritsPopupMenu(true);
        FileChooser.setName("MyFileChooser"); // NOI18N
        FileChooser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FileChooserActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout FileChooserPanelLayout = new javax.swing.GroupLayout(FileChooserPanel);
        FileChooserPanel.setLayout(FileChooserPanelLayout);
        FileChooserPanelLayout.setHorizontalGroup(
            FileChooserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(FileChooserPanelLayout.createSequentialGroup()
                .addComponent(FileChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 642, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        FileChooserPanelLayout.setVerticalGroup(
            FileChooserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, FileChooserPanelLayout.createSequentialGroup()
                .addGap(0, 44, Short.MAX_VALUE)
                .addComponent(FileChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 389, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout InstrumentDisplayLayout = new javax.swing.GroupLayout(InstrumentDisplay);
        InstrumentDisplay.setLayout(InstrumentDisplayLayout);
        InstrumentDisplayLayout.setHorizontalGroup(
            InstrumentDisplayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(InstrumentDisplayLayout.createSequentialGroup()
                .addGap(0, 708, Short.MAX_VALUE)
                .addGroup(InstrumentDisplayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, InstrumentDisplayLayout.createSequentialGroup()
                        .addComponent(CancelColorButton, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(SaveColorButton, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(FileChooserPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ColorChooser, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        InstrumentDisplayLayout.setVerticalGroup(
            InstrumentDisplayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(InstrumentDisplayLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(FileChooserPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(ColorChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(InstrumentDisplayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SaveColorButton, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CancelColorButton, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ButtonPannel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(KeysPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(InstrumentDisplay, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(NavPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(NavPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(InstrumentDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, 882, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(ButtonPannel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(KeysPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void refreshSize() {
        InstrumentDisplay.initialDimension.height = InstrumentDisplay.getHeight();
        InstrumentDisplay.initialDimension.width = InstrumentDisplay.getWidth();

        if (InstrumentDisplay != null && ctrl != null) {
            ctrl.setNewInstrumentPanelSize(new Dimension(InstrumentDisplay.getWidth(), InstrumentDisplay.getHeight()));
        }

    }

    /**
     * Permet de recuperer les cordonnée dans le panel afin de pouvoir demander
     * au controller de stocker ou de modifier l'instrument
     *
     * @param evt contient les coordonnée de la touche en temps réel pendant le
     * drag
     */
    private void Key1MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Key1MouseDragged
        // TODO add your handling code here:
        // x et y par rapport au InstrumentDisplay en haut a gauche du panel 0,0
        int xNewKey = evt.getX() + Key1.getX();
        int yNewKey = evt.getY() + 5 + InstrumentDisplay.getHeight();
        //TODO pour la sequence du livrable 2 sur comment transformer les coords
        // elles sont la donc il faut les stocker et les réutiliser sur le 
        // drop pour chaque type de touche appeler le controller pour qu'il
        // sauvegarde la touche dans l'instrument si elle est valide
        curSelectedKey = new Key();
        curSelectedKey.height = 30;
        curSelectedKey.width = 30;
        curSelectedKey.posX = xNewKey;
        curSelectedKey.posY = yNewKey;
        System.out.println("x, y " + xNewKey + ", " + yNewKey);

        //if (isResizable) {
        /*curSelectedKey.relativeWidth = curSelectedKey.width * 100 / InstrumentDisplay.getWidth();
            curSelectedKey.relativeHeight = curSelectedKey.height * 100 / InstrumentDisplay.getHeight();
         */
        if (xNewKey > 0 && yNewKey > 0) {
            //System.out.println("!!!!!!!!!!!!!!! redefine posX et posY relatif");
            float posRatio = (float) xNewKey / (float) yNewKey;

            int xKeyOnPanel = (int) (((float) xNewKey / (float) InstrumentDisplay.getWidth()) * initialPanel.width);

            int yKeyOnPanel = (int) ((float) xKeyOnPanel / posRatio);
            System.out.println("coord key drag : " + xKeyOnPanel + "/" + yKeyOnPanel);

            curSelectedKey.posX = xKeyOnPanel;
            curSelectedKey.posY = yKeyOnPanel;
            curSelectedKey.relativePosX = xNewKey;
            curSelectedKey.relativePosY = yNewKey;
        }

        //}
        //System.out.println("x " + curSelectedKey.relativePosX + " y " + curSelectedKey.relativePosY);
        curSelectedKey.keyType = 1;
        curSelectedKey.id = idGenerator;

//        ctrl.editInstrument(curSelectedKey, 1);
        //ctrl.recalculKeyCoord();
        //InstrumentDisplay.repaint();
        //System.out.println("relative boutton 1 x : " + 
        //        (curSelectedKey.relativePosX) + " / y : " +
        //        (curSelectedKey.relativePosY) + " id : " + curSelectedKey.id
        //        + " test " +  NavPanel.getWidth());
    }//GEN-LAST:event_Key1MouseDragged

    private void Key2MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Key2MouseDragged
        // TODO add your handling code here:
        int xNewKey = evt.getX() + Key2.getX();
        int yNewKey = evt.getY() + 5 + InstrumentDisplay.getHeight();

        //curSelectedKey = new Key();
        curSelectedKey.posX = xNewKey;
        curSelectedKey.posY = yNewKey;
        curSelectedKey.height = 40;
        curSelectedKey.width = 40;
        //if (isResizable) {
        /*curSelectedKey.relativeWidth = curSelectedKey.width * 100 / InstrumentDisplay.getWidth();
            curSelectedKey.relativeHeight = curSelectedKey.height * 100 / InstrumentDisplay.getHeight();
         */
        if (xNewKey > 0 && yNewKey > 0) {
            //System.out.println("!!!!!!!!!!!!!!! redefine posX et posY relatif");
            float posRatio = (float) xNewKey / (float) yNewKey;

            int xKeyOnPanel = (int) (((float) xNewKey / (float) InstrumentDisplay.getWidth()) * initialPanel.width);

            int yKeyOnPanel = (int) ((float) xKeyOnPanel / posRatio);
            //System.out.println("coord key press : " + xKeyOnPanel + "/" + yKeyOnPanel);

            curSelectedKey.posX = xKeyOnPanel;
            curSelectedKey.posY = yKeyOnPanel;

            curSelectedKey.relativePosX = xNewKey;
            curSelectedKey.relativePosY = yNewKey;
        }

        //// }
        curSelectedKey.keyType = 2;
        curSelectedKey.id = idGenerator;
    }//GEN-LAST:event_Key2MouseDragged

    private void Key3MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Key3MouseDragged
        int xNewKey = evt.getX() + Key3.getX();
        int yNewKey = evt.getY() + 5 + InstrumentDisplay.getHeight();

        curSelectedKey = new Key();
        curSelectedKey.posX = xNewKey;
        curSelectedKey.posY = yNewKey;
        curSelectedKey.height = 50;
        curSelectedKey.width = 50;
        curSelectedKey.keyType = 3;
        //if (isResizable) {
        /*curSelectedKey.relativeWidth = curSelectedKey.width * 100 / InstrumentDisplay.getWidth();
            curSelectedKey.relativeHeight = curSelectedKey.height * 100 / InstrumentDisplay.getHeight();
         */
        if (xNewKey > 0 && yNewKey > 0) {
            //System.out.println("!!!!!!!!!!!!!!! redefine posX et posY relatif");
            float posRatio = (float) xNewKey / (float) yNewKey;

            int xKeyOnPanel = (int) (((float) xNewKey / (float) InstrumentDisplay.getWidth()) * initialPanel.width);

            int yKeyOnPanel = (int) ((float) xKeyOnPanel / posRatio);
            //System.out.println("coord key press : " + xKeyOnPanel + "/" + yKeyOnPanel);

            curSelectedKey.posX = xKeyOnPanel;
            curSelectedKey.posY = yKeyOnPanel;
            curSelectedKey.relativePosX = xNewKey;
            curSelectedKey.relativePosY = yNewKey;
        }

        //}
        curSelectedKey.id = idGenerator;
    }//GEN-LAST:event_Key3MouseDragged

    private void Key4MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Key4MouseDragged
        int xNewKey = evt.getX() + Key4.getX();
        int yNewKey = evt.getY() + 5 + InstrumentDisplay.getHeight();

        curSelectedKey = new Key();
        curSelectedKey.posX = xNewKey;
        curSelectedKey.posY = yNewKey;
        curSelectedKey.height = 100;
        curSelectedKey.width = 50;
        //if (isResizable) {
        /*curSelectedKey.relativeWidth = curSelectedKey.width * 100 / InstrumentDisplay.getWidth();
            curSelectedKey.relativeHeight = curSelectedKey.height * 100 / InstrumentDisplay.getHeight();
         */
        if (xNewKey > 0 && yNewKey > 0) {
            //System.out.println("!!!!!!!!!!!!!!! redefine posX et posY relatif");
            float posRatio = (float) xNewKey / (float) yNewKey;

            int xKeyOnPanel = (int) (((float) xNewKey / (float) InstrumentDisplay.getWidth()) * initialPanel.width);

            int yKeyOnPanel = (int) ((float) xKeyOnPanel / posRatio);
            //System.out.println("coord key press : " + xKeyOnPanel + "/" + yKeyOnPanel);

            curSelectedKey.posX = xKeyOnPanel;
            curSelectedKey.posY = yKeyOnPanel;
            curSelectedKey.relativePosX = xNewKey;
            curSelectedKey.relativePosY = yNewKey;
        }

        //}
        curSelectedKey.keyType = 4;
        curSelectedKey.id = idGenerator;
    }//GEN-LAST:event_Key4MouseDragged

    private void Key5MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Key5MouseDragged
        int xNewKey = evt.getX() + Key5.getX();
        int yNewKey = evt.getY() + 5 + InstrumentDisplay.getHeight();

        curSelectedKey = new Key();
        curSelectedKey.posX = xNewKey;
        curSelectedKey.posY = yNewKey;
        curSelectedKey.height = 50;
        curSelectedKey.width = 100;
        curSelectedKey.keyType = 5;
        //if (isResizable) {
        /*curSelectedKey.relativeWidth = curSelectedKey.width * 100 / InstrumentDisplay.getWidth();
            curSelectedKey.relativeHeight = curSelectedKey.height * 100 / InstrumentDisplay.getHeight();
         */
        if (xNewKey > 0 && yNewKey > 0) {
            //System.out.println("!!!!!!!!!!!!!!! redefine posX et posY relatif");
            float posRatio = (float) xNewKey / (float) yNewKey;

            int xKeyOnPanel = (int) (((float) xNewKey / (float) InstrumentDisplay.getWidth()) * initialPanel.width);

            int yKeyOnPanel = (int) ((float) xKeyOnPanel / posRatio);
            //System.out.println("coord key press : " + xKeyOnPanel + "/" + yKeyOnPanel);

            curSelectedKey.posX = xKeyOnPanel;
            curSelectedKey.posY = yKeyOnPanel;
            curSelectedKey.relativePosX = xNewKey;
            curSelectedKey.relativePosY = yNewKey;
        }

        //}
        curSelectedKey.id = idGenerator;
    }//GEN-LAST:event_Key5MouseDragged

    private void Key5MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Key5MouseReleased
        // fonction utiliser pour le drop de la touche 5        
        //essayer de sauvegarder la touche dans l'instrument
        editKeyMouseRelease();
    }//GEN-LAST:event_Key5MouseReleased

    private void Key4MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Key4MouseReleased
        editKeyMouseRelease();
    }//GEN-LAST:event_Key4MouseReleased

    private void Key3MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Key3MouseReleased
        editKeyMouseRelease();
    }//GEN-LAST:event_Key3MouseReleased

    private void Key2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Key2MouseReleased
        editKeyMouseRelease();
    }//GEN-LAST:event_Key2MouseReleased

    private void Key1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Key1MouseReleased

        editKeyMouseRelease();
    }//GEN-LAST:event_Key1MouseReleased

    private void editKeyMouseRelease() {
        curSelectedKey.id = ctrl.getNewKeyId();
        //System.out.println("ulaval.ui.MainJFrameView.editKeyMouseRelease() " + curSelectedKey);
        if ((curSelectedKey.relativePosX / InstrumentDisplay.getWidth() > 1 || curSelectedKey.relativePosY / InstrumentDisplay.getHeight() > 1)) {
            System.out.println("Key out of screen !!!");
            return;
        }
        if (curSelectedKey != null
                && ctrl.editInstrument(curSelectedKey, Mode.EDIT.ordinal())) {
            changeToTheCreateMode();
            System.out.println("new Touche with id : " + idGenerator + " curKey relativePosX " + curSelectedKey.relativePosX);
            ++idGenerator;

            //refreshSize();
            //ctrl.recalculKeyCoord();
            //InstrumentDisplay.repaint();
            curSelectedKey = null;

            //curSelectedKey.id = -1;
        } else {
            curSelectedKey = null;
        }
        //curSelectedKey = new Key();
        //curSelectedKey.id = -1;
    }

    private void PrevGabaritButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PrevGabaritButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PrevGabaritButtonActionPerformed

    private void NextGabaritButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NextGabaritButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NextGabaritButtonActionPerformed

    private void InstrumentDisplayMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_InstrumentDisplayMousePressed
        // TODO add your handling code here:
        //isResizable = ctrl.checkDisplaySize();
        if (evt.getClickCount() > 1) {
            //System.out.println("deux clic en meme temps ");
        }
        Point mousePoint = evt.getPoint();
        // appeler le controller et essayer de jouer la note ou de la deplacer
        int xKeyOnPanel = (int) mousePoint.getX();
        int yKeyOnPanel = (int) mousePoint.getY();
        //System.out.println("coord key press : " + xKeyOnPanel + "/" + yKeyOnPanel);
        // transformer getKey en setKey
        if ((curSelectedKey = ctrl.getKey(xKeyOnPanel, yKeyOnPanel)) != null) {
            curSelectedKey.isSelected = true;
        }

        // mettre la lecture de la touche dans le controller
        ctrl.setKeyPressed(xKeyOnPanel, yKeyOnPanel);
        //InstrumentDisplay.repaint();
        if (isInEditMode) {
            // pour ca il faudrait mieux faire un setToneValue(string tone)
            // qui modifira la touche courante presente dans le controller ou ailleur
            //System.out.println("register prop button");
            registerPropertyButton(curSelectedKey);
            if (checkBoxShowKeyLabel != null && curSelectedKey != null) {
                checkBoxShowKeyLabel.setSelected(curSelectedKey.printLabel);
            }
        }
    }//GEN-LAST:event_InstrumentDisplayMousePressed


    private void MetroTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_MetroTextFieldKeyReleased
        // TODO add your handling code here:
        // metronome value : (JTextField)evt.getSource()).getText()
        System.out.println(((JTextField) evt.getSource()).getText());
    }//GEN-LAST:event_MetroTextFieldKeyReleased

    private void MetroActionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MetroActionButtonActionPerformed
        if (!isStart) {
            int res = 0;
            try {
                res = Integer.parseInt(MetroTextField.getText());
            } catch (NumberFormatException e) {
                res = -1;
            }
            if (res > 0) {
                ctrl.startMetro(res);
                MetroActionButton.setText("Stop");
                isStart = true;
            }
        } else {
            ctrl.stopMetro();
            isStart = false;
            MetroActionButton.setText("Start");
        }
    }//GEN-LAST:event_MetroActionButtonActionPerformed

    private void InstrumentDisplayMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_InstrumentDisplayMouseReleased
        Point mousePoint = evt.getPoint();

        int xKeyOnPanel = (int) mousePoint.getX();
        int yKeyOnPanel = (int) mousePoint.getY();
        if (curSelectedKey != null) {
            curSelectedKey.isSelected = false;
        }
        ctrl.setKeyReleased(xKeyOnPanel, yKeyOnPanel);
        //ctrl.recalculKeyCoord();
        InstrumentDisplay.repaint();
    }//GEN-LAST:event_InstrumentDisplayMouseReleased

    private void InstrumentDisplayMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_InstrumentDisplayMouseDragged
        // TODO add your handling code here:
        if (curSelectedKey != null && curSelectedKey.isSelected == true
                && isInEditMode && isMovable) {
            Point mousePoint = evt.getPoint();
            //System.out.println("move key");

            float posRatio = (float) mousePoint.getX() / (float) mousePoint.getY();

            int xKeyOnPanel = (int) (((float) mousePoint.getX() / (float) ((float) InstrumentDisplay.getWidth()) * (float) initialPanel.width));

            int yKeyOnPanel = (int) ((float) xKeyOnPanel / posRatio);
            //System.out.println("coord key press : " + xKeyOnPanel + "/" + yKeyOnPanel);

            curSelectedKey.posX = xKeyOnPanel;
            curSelectedKey.posY = yKeyOnPanel;
            curSelectedKey.relativePosX = mousePoint.x;
            curSelectedKey.relativePosY = mousePoint.y;

            if (curSelectedKey.posX > 0 && curSelectedKey.posY > 0) {
                //ctrl.recalculKeyCoord();
                ctrl.editInstrument(curSelectedKey, 1);
                //InstrumentDisplay.repaint();
            }

            //informe le controller de la modification de la touche
            // redessine l'instrument
            //refresh();
            InstrumentDisplay.repaint();
        } else if (!isInEditMode) {
            //System.out.println("Try to play like a guitare");
            Point mousePoint = evt.getPoint();
            //System.out.println("coord key press : " + xKeyOnPanel + "/" + yKeyOnPanel);
            // permet de jouer comme une vrai guitare ;)
            Key tmpKey = ctrl.getKey(mousePoint.x, mousePoint.y);
            // si une touche est deja selectionné c'est qu'on est entrain
            // de la jouer alors on la relace
            if (curSelectedKey != null && tmpKey != null && curSelectedKey.id != tmpKey.id) {
                ctrl.setKeyReleased((int) curSelectedKey.relativePosX, (int) curSelectedKey.relativePosY);
                curSelectedKey = tmpKey;
                ctrl.setKeyPressed((int) tmpKey.relativePosX, (int) tmpKey.relativePosY);
                InstrumentDisplay.repaint();
            } else if (curSelectedKey == null && tmpKey != null) {
                curSelectedKey = tmpKey;
                ctrl.setKeyPressed((int) tmpKey.relativePosX, (int) tmpKey.relativePosY);
                InstrumentDisplay.repaint();
            }
        }

    }//GEN-LAST:event_InstrumentDisplayMouseDragged

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        // TODO add your handling code here:
        //widthWindow = InstrumentDisplay.getWidth();
        //heightWindow = InstrumentDisplay.getHeight();
        InstrumentDisplay.initialDimension.height = InstrumentDisplay.getHeight();
        InstrumentDisplay.initialDimension.width = InstrumentDisplay.getWidth();
        //System.out.println("Window resize " + widthWindow + ", " + heightWindow);
        //System.out.println("InstrumentDisplay : h " + InstrumentDisplay.getHeight()
        //                + " w " + InstrumentDisplay.getWidth());
        if (InstrumentDisplay != null && ctrl != null) {
            //ctrl.setInitialInstrumentPanelSize(new Dimension(InstrumentDisplay.getWidth(), InstrumentDisplay.getHeight()));

            InstrumentDisplay.repaint();

            //isResizable = ctrl.checkDisplaySize();
        }
    }//GEN-LAST:event_formComponentResized

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        System.out.println("ulaval.ui.MainJFrameView.formWindowOpened()");
        InstrumentDisplay.initialDimension.height = InstrumentDisplay.getHeight();
        InstrumentDisplay.initialDimension.width = InstrumentDisplay.getWidth();
        initialPanel = new Dimension(InstrumentDisplay.getWidth(), InstrumentDisplay.getHeight());
        ctrl.setInitialInstrumentPanelSize(new Dimension(InstrumentDisplay.getWidth(), InstrumentDisplay.getHeight()));
        refreshSize();
        ctrl.recalculKeyCoord();
        InstrumentDisplay.repaint();
    }//GEN-LAST:event_formWindowOpened

    private void PlayModeButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PlayModeButtonMouseReleased
        // TODO add your handling code here:
        this.KeysPanel.setVisible(false);
        this.propBar.setVisible(false);
        this.PlayBar.setVisible(true);
        this.isInEditMode = false;
        this.getContentPane().add(ButtonPannel, BorderLayout.SOUTH);
        this.add(PlayBar, BorderLayout.EAST);
        TimbreLabel.setVisible(false);
        TimbreList.setVisible(false);
        DelInstrumentButton.setVisible(false);
        this.ButtonPannel.setVisible(true);

    }//GEN-LAST:event_PlayModeButtonMouseReleased

    private void CreateModeButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_CreateModeButtonMouseReleased
        // TODO add your handling code here:
        changeToTheCreateMode();
    }//GEN-LAST:event_CreateModeButtonMouseReleased

    private void InstrumentDisplayComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_InstrumentDisplayComponentResized
        // TODO add your handling code here:
        InstrumentDisplay.initialDimension.height = InstrumentDisplay.getHeight();
        InstrumentDisplay.initialDimension.width = InstrumentDisplay.getWidth();

        refreshSize();
        if (ctrl != null && InstrumentDisplay != null) {
            ctrl.recalculKeyCoord();
            InstrumentDisplay.repaint();
        }
    }//GEN-LAST:event_InstrumentDisplayComponentResized

    private void SaveColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveColorButtonActionPerformed

        // TODO add your handling code here:
        // savegarde la nouvelle couleur de la touche curKey
        if (curSelectedKey != null && isNotBorderColor) {
            curSelectedKey.color = ColorChooser.getColor();
            ctrl.editInstrument(curSelectedKey, Mode.EDIT.ordinal());
        } else if (curSelectedKey != null && !isNotBorderColor) {
            isNotBorderColor = true;
            curSelectedKey.bordureColor = ColorChooser.getColor();
            ctrl.editInstrument(curSelectedKey, Mode.EDIT.ordinal());
        }
        InstrumentDisplay.repaint();
        SaveColorButton.setVisible(false);
        ColorChooser.setVisible(false);
        CancelColorButton.setVisible(false);
    }//GEN-LAST:event_SaveColorButtonActionPerformed

    private void CancelColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelColorButtonActionPerformed
        this.ColorChooser.setVisible(false);
        this.CancelColorButton.setVisible(false);
        this.SaveColorButton.setVisible(false);
        isInEditMode = true;
    }//GEN-LAST:event_CancelColorButtonActionPerformed

    private void AddInstrumentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddInstrumentButtonActionPerformed
        // TODO add your handling code here:

        ctrl.addInstrument(GabaritList.getSelectedIndex());
        addSavedInstrument();
        ctrl.recalculKeyCoord();

        // TODO reussir a faire marcher la putain de scroll bar sur la List d'instrument
        //ScrollPanelList.repaint();
    }//GEN-LAST:event_AddInstrumentButtonActionPerformed

    private void Key5MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Key5MousePressed
        // TODO add your handling code here:
        curSelectedKey = new Key();
        isResizable = ctrl.checkDisplaySize();
    }//GEN-LAST:event_Key5MousePressed

    private void Key1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Key1MousePressed
        // TODO add your handling code here:
//        curSelectedKey = new Key();
        isResizable = ctrl.checkDisplaySize();
    }//GEN-LAST:event_Key1MousePressed

    private void Key2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Key2MousePressed
        // TODO add your handling code here:
        curSelectedKey = new Key();
        isResizable = ctrl.checkDisplaySize();
    }//GEN-LAST:event_Key2MousePressed

    private void Key3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Key3MousePressed
        // TODO add your handling code here:
        curSelectedKey = new Key();
        isResizable = ctrl.checkDisplaySize();

    }//GEN-LAST:event_Key3MousePressed

    private void Key4MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Key4MousePressed
        // TODO add your handling code here:
        curSelectedKey = new Key();
        isResizable = ctrl.checkDisplaySize();

    }//GEN-LAST:event_Key4MousePressed

    private void SearchTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SearchTextFieldKeyReleased
        // TODO add your handling code here:
        String str = ((JTextField) evt.getSource()).getText();
        ctrl.removeSearch();
        if (str.length() > 0 && ctrl != null) {
            ctrl.searchTone(str, SearchList.getSelectedIndex());
        }
        InstrumentDisplay.repaint();
    }//GEN-LAST:event_SearchTextFieldKeyReleased

    private void SearchListItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_SearchListItemStateChanged
        // TODO add your handling code here:
        String str = (SearchTextField).getText();
        ctrl.removeSearch();
        if (str.length() > 0 && ctrl != null) {
            ctrl.searchTone(str, SearchList.getSelectedIndex());
        }
        InstrumentDisplay.repaint();
    }//GEN-LAST:event_SearchListItemStateChanged

    private void FileChooserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FileChooserActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_FileChooserActionPerformed

    private void muteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_muteButtonActionPerformed
        // TODO add your handling code here:
        //mute audio
        if ("mute".equals(muteButton.getText())) {
            muteButton.setText("unmute");
        } else {
            muteButton.setText("mute");
        }
        if ("mute".equals(muteButton2.getText())) {
            muteButton2.setText("unmute");
        } else {
            muteButton2.setText("mute");
        }
        ctrl.changeMute();
    }//GEN-LAST:event_muteButtonActionPerformed

    private void muteButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_muteButton2ActionPerformed
        // TODO add your handling code here:
        if ("mute".equals(muteButton.getText())) {
            muteButton.setText("unmute");
        } else {
            muteButton.setText("mute");
        }
        if ("mute".equals(muteButton2.getText())) {
            muteButton2.setText("unmute");
        } else {
            muteButton2.setText("mute");
        }
        ctrl.changeMute();
    }//GEN-LAST:event_muteButton2ActionPerformed

    private void InstrumentDisplayMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_InstrumentDisplayMouseMoved
        // TODO add your handling code here:
        if (!isInEditMode) {
            //System.out.println("Try to play like a guitar");
        }
    }//GEN-LAST:event_InstrumentDisplayMouseMoved

    private void HidePanelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HidePanelActionPerformed
        // TODO add your handling code here:
        if (PlayBarIsVisible) {
            this.PlayBar.setVisible(false);
            PlayBarIsVisible = false;
            HidePanel.setText("Show Panel");
        } else {
            this.PlayBar.setVisible(true);
            PlayBarIsVisible = true;
            HidePanel.setText("Hide Panel");
        }
    }//GEN-LAST:event_HidePanelActionPerformed

    private void DelInstrumentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DelInstrumentButtonActionPerformed
        // TODO add your handling code here:
        if (InstrumentsComboBox.getSelectedItem() instanceof String) {
            String index = (String) InstrumentsComboBox.getSelectedItem();
            ctrl.delInstrument(index);
            System.out.println("Del : " + index);

            // utiliser pour refaire le binding entre la comboBox et la liste
            // d'instrument apres suppression
            addSavedInstrument();

            ctrl.recalculKeyCoord();

        }
    }//GEN-LAST:event_DelInstrumentButtonActionPerformed

    private void TimbreListItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_TimbreListItemStateChanged
        // TODO add your handling code here:
        System.out.println("changeTimbre !!!");
        if (ctrl != null) {
            ctrl.reaffectTimbre(TimbreList.getSelectedIndex());
        }
    }//GEN-LAST:event_TimbreListItemStateChanged

    private void InstrumentsComboBoxPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_InstrumentsComboBoxPropertyChange
        // TODO add your handling code here:
        System.out.println("ulaval.ui.MainJFrameView.InstrumentsComboBoxPropertyChange()"
                + " " + evt.getNewValue());
        if (ctrl != null && InstrumentsComboBox != null) {
            changeSelectedInstrument(ctrl.getInstrument(InstrumentsComboBox.getSelectedIndex()).name);
        }
    }//GEN-LAST:event_InstrumentsComboBoxPropertyChange

    private void InstrumentsComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InstrumentsComboBoxActionPerformed
        // TODO add your handling code here:

        if (ctrl != null && InstrumentsComboBox != null) {
            changeSelectedInstrument(ctrl.getInstrument(InstrumentsComboBox.getSelectedIndex()).name);
        }
    }//GEN-LAST:event_InstrumentsComboBoxActionPerformed

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        // TODO add your handling code here:
        //System.out.println("touch " + evt.getKeyCode());
        switch (evt.getKeyCode()) {
            case 48:
                // 0
                ctrl.liveLoop(10);
                PlayBar.updateButton(10);
                break;
            case 49:
                ctrl.liveLoop(1);
                PlayBar.updateButton(1);
                break;
            case 50:
                // 0
                ctrl.liveLoop(2);
                PlayBar.updateButton(2);
                break;
            case 51:
                // 0
                ctrl.liveLoop(3);
                PlayBar.updateButton(3);
                break;
            case 52:
                // 0
                ctrl.liveLoop(4);
                PlayBar.updateButton(4);
                break;
            case 53:
                // 0
                ctrl.liveLoop(5);
                PlayBar.updateButton(5);
                break;
            case 54:
                // 0
                ctrl.liveLoop(6);
                PlayBar.updateButton(6);
                break;
            case 55:
                // 0
                ctrl.liveLoop(7);
                PlayBar.updateButton(7);
                break;
            case 56:
                // 0
                ctrl.liveLoop(8);
                PlayBar.updateButton(8);
                break;
            case 57:
                // 0
                ctrl.liveLoop(9);
                PlayBar.updateButton(9);
                break;

        }
    }//GEN-LAST:event_formKeyPressed

    /**
     * Change to create mode
     */
    private void changeToTheCreateMode() {
        this.ButtonPannel.setVisible(false);
        KeysPanel.setVisible(true);
        this.isInEditMode = true;
        this.add(KeysPanel, BorderLayout.SOUTH);
        this.propBar.setVisible(true);
        this.add(propBar, BorderLayout.EAST);
        PlayBar.setVisible(false);
        TimbreLabel.setVisible(true);
        TimbreList.setVisible(true);
        DelInstrumentButton.setVisible(true);
        // register checkBox MoveCheckBox
        // pour permettre a l'utilisateur d'activer 
        // ou de desactiver le deplacement des touches
        for (Component component : getAllComponents(this.propBar)) {
            //System.out.println("component : " + component.getName());
            // creer nos deux type de listener possible pour les textField ou les buttons
            MyMouseKeySizeListener myMouseListener = new MyMouseKeySizeListener();
            myMouseListener.ctrl = ctrl;
            myMouseListener.jFrame = this;
            myMouseListener.components = getAllComponents(this.propBar);
            myCurrentMouseListener = myMouseListener;
            myMouseListener.InstrumentDisplay = InstrumentDisplay;

            if (null != component.getName()) {
                switch (component.getName()) {
                    case "MoveCheckBox":
                        //System.out.println("try to add listener in MoveCheckBox");
                        for (MouseListener mouseListener : component.getMouseListeners()) {
                            if (mouseListener instanceof MyMouseKeySizeListener) {
                                component.removeMouseListener(mouseListener);
                            }
                        }

                        // 36 pour la checkBox modifiant la variable isMovable
                        myMouseListener.type = 36;
                        myMouseListener.MoveCheck = (JCheckBox) component;
                        myMouseListener.MoveCheck.setSelected(!this.isMovable);
                        component.addMouseListener(myMouseListener);
                        break;
                }
            }
        }

        refreshSize();
        ctrl.recalculKeyCoord();
        InstrumentDisplay.repaint();
        //redefini la taille de l'afficher d'instrument parce que la taille du panel a changer
        //notifyInstrumentPrinter();
    }

    /**
     *
     * @param curKey
     */
    public void registerPropertyButton(Key curKey) {
        //System.out.println("set Prop Bar");     
        if (curKey == null) {
            return;
        }
        // recupere le nom de l'instrument dans la combo box et change 
        // la valeur de l'instrument

        registerButtonEditKey(curKey);
        // exemple augmente et diminue la taille d'une touche
    }

    private void registerButtonEditKey(Key curKey) {
        int i = 0;
        JTextField text = null;

        // parcour les composant et supprime les mouse listener pour remettre les 
        // correspondant
        for (Component component : getAllComponents(this.propBar)) {
            //System.out.println("component : " + component.getName());
            // creer nos deux type de listener possible pour les textField ou les buttons
            MyMouseKeySizeListener myMouseListener = new MyMouseKeySizeListener();
            myMouseListener.ctrl = ctrl;
            myMouseListener.key = curKey;
            myMouseListener.jFrame = this;
            myMouseListener.components = getAllComponents(this.propBar);
            myCurrentMouseListener = myMouseListener;

            myMouseListener.InstrumentDisplay = InstrumentDisplay;
            // lui pour les boutons
            MyKeyListener keyListener1 = new MyKeyListener();
            keyListener1.ctrl = ctrl;
            keyListener1.key = curKey;
            keyListener1.components = getAllComponents(this.propBar);
            keyListener1.InstrumentDisplay = InstrumentDisplay;

            JCheckBox check = null;
            JComboBox<?> combo = null;

            registerToneCombo(curKey.tone.name);

            if (null != component.getName()) {
                switch (component.getName()) {
                    case "ToneValue":
                        combo = null;
                        if (component instanceof JComboBox<?>) {
                            combo = (JComboBox<?>) component;
                        }
                        if (combo == null) {
                            break;
                        }
                        for (ActionListener actionListener : combo.getActionListeners()) {
                            combo.removeActionListener(actionListener);
                        }
                        //combo.setSelectedIndex(curKey.tone.timbre.ordinal());
                        ActionListener timbreListener = (ActionEvent) -> {
                            //System.out.println("setTone");
                            setToneValue(curKey);
                        };
                        combo.addActionListener(timbreListener);
                        break;

                    case "OctaveValue":
                        combo = null;
                        if (component instanceof JComboBox<?>) {
                            combo = (JComboBox<?>) component;
                        }
                        if (combo == null) {
                            break;
                        }
                        for (ActionListener actionListener : combo.getActionListeners()) {
                            combo.removeActionListener(actionListener);
                        }
                        //combo.setSelectedIndex(curKey.tone.timbre.ordinal());
                        timbreListener = (ActionEvent) -> {
                            //System.out.println("setTone");
                            setOctave(curKey);
                        };
                        combo.addActionListener(timbreListener);

                        break;
                    case "OtherValue":
                        combo = null;
                        if (component instanceof JComboBox<?>) {
                            combo = (JComboBox<?>) component;
                        }
                        if (combo == null) {
                            break;
                        }
                        for (ActionListener actionListener : combo.getActionListeners()) {
                            combo.removeActionListener(actionListener);
                        }
                        //combo.setSelectedIndex(curKey.tone.timbre.ordinal());
                        timbreListener = (ActionEvent) -> {
                            //System.out.println("setTone");
                            setOtherValue(curKey);
                        };
                        combo.addActionListener(timbreListener);
                        break;

                    case "ShowBordureLeft":
                        check = (JCheckBox) component;
                        for (MouseListener mouseListener : component.getMouseListeners()) {
                            component.removeMouseListener(mouseListener);
                            //System.out.println("supprimer listener " + component.getName());
                        }
                        myMouseListener.checkBokLeft = check;
                        myMouseListener.checkBok = check;

                        myMouseListener.checkBokDown = (JCheckBox) getComponentByName(this.propBar, "ShowBordureDown");
                        myMouseListener.checkBokUp = (JCheckBox) getComponentByName(this.propBar, "ShowBordureUp");
                        myMouseListener.checkBokRight = (JCheckBox) getComponentByName(this.propBar, "ShowBordureRight");
                        myMouseListener.text = (JTextField) getComponentByName(this.propBar, "SizeBorderTextField");

                        myMouseListener.checkBokDown.setSelected(curKey.showDownBordure);
                        myMouseListener.checkBokLeft.setSelected(curKey.showLeftBordure);
                        myMouseListener.checkBokRight.setSelected(curKey.showRightBordure);
                        myMouseListener.checkBokUp.setSelected(curKey.showUpBordure);

                        // this.checkBoxShowBordureLeft = check;
                        myMouseListener.type = 32;
                        component.addMouseListener(myMouseListener);
                        break;
                    case "ShowBordureUp":
                        check = (JCheckBox) component;
                        for (MouseListener mouseListener : component.getMouseListeners()) {
                            component.removeMouseListener(mouseListener);
                            //System.out.println("supprimer listener " + component.getName());
                        }
                        myMouseListener.checkBokUp = check;
                        myMouseListener.checkBok = check;

                        myMouseListener.checkBokDown = (JCheckBox) getComponentByName(this.propBar, "ShowBordureDown");
                        myMouseListener.checkBokLeft = (JCheckBox) getComponentByName(this.propBar, "ShowBordureLeft");
                        myMouseListener.checkBokRight = (JCheckBox) getComponentByName(this.propBar, "ShowBordureRight");
                        myMouseListener.text = (JTextField) getComponentByName(this.propBar, "SizeBorderTextField");

                        myMouseListener.checkBokDown.setSelected(curKey.showDownBordure);
                        myMouseListener.checkBokLeft.setSelected(curKey.showLeftBordure);
                        myMouseListener.checkBokRight.setSelected(curKey.showRightBordure);
                        myMouseListener.checkBokUp.setSelected(curKey.showUpBordure);

                        myMouseListener.type = 33;
                        component.addMouseListener(myMouseListener);
                        break;
                    case "ShowBordureRight":
                        check = (JCheckBox) component;
                        for (MouseListener mouseListener : component.getMouseListeners()) {
                            component.removeMouseListener(mouseListener);
                            //System.out.println("supprimer listener " + component.getName());
                        }
                        myMouseListener.checkBokRight = check;
                        myMouseListener.checkBok = check;

                        myMouseListener.checkBokDown = (JCheckBox) getComponentByName(this.propBar, "ShowBordureDown");
                        myMouseListener.checkBokUp = (JCheckBox) getComponentByName(this.propBar, "ShowBordureUp");
                        myMouseListener.checkBokLeft = (JCheckBox) getComponentByName(this.propBar, "ShowBordureLeft");
                        myMouseListener.text = (JTextField) getComponentByName(this.propBar, "SizeBorderTextField");

                        myMouseListener.checkBokDown.setSelected(curKey.showDownBordure);
                        myMouseListener.checkBokLeft.setSelected(curKey.showLeftBordure);
                        myMouseListener.checkBokRight.setSelected(curKey.showRightBordure);
                        myMouseListener.checkBokUp.setSelected(curKey.showUpBordure);

                        myMouseListener.type = 34;
                        component.addMouseListener(myMouseListener);
                        break;
                    case "ShowBordureDown":
                        check = (JCheckBox) component;
                        for (MouseListener mouseListener : component.getMouseListeners()) {
                            component.removeMouseListener(mouseListener);
                            //System.out.println("supprimer listener " + component.getName());
                        }
                        myMouseListener.checkBokDown = check;
                        myMouseListener.checkBok = check;

                        myMouseListener.checkBokLeft = (JCheckBox) getComponentByName(this.propBar, "ShowBordureLeft");
                        myMouseListener.checkBokUp = (JCheckBox) getComponentByName(this.propBar, "ShowBordureUp");
                        myMouseListener.checkBokRight = (JCheckBox) getComponentByName(this.propBar, "ShowBordureRight");
                        myMouseListener.text = (JTextField) getComponentByName(this.propBar, "SizeBorderTextField");

                        myMouseListener.checkBokDown.setSelected(curKey.showDownBordure);
                        myMouseListener.checkBokLeft.setSelected(curKey.showLeftBordure);
                        myMouseListener.checkBokRight.setSelected(curKey.showRightBordure);
                        myMouseListener.checkBokUp.setSelected(curKey.showUpBordure);

                        myMouseListener.type = 35;
                        component.addMouseListener(myMouseListener);
                        break;
                    case "OpenSampleFileChooser":
                        for (MouseListener mouseListener : component.getMouseListeners()) {
                            if (mouseListener instanceof MyMouseKeySizeListener) {
                                component.removeMouseListener(mouseListener);
                            }
                        }
                        if (curKey.tone.samplePath != null) {
                            this.propBar.getSampleTextField().setText(curKey.tone.samplePath);
                        } else {
                            this.propBar.getSampleTextField().setText("");
                        }
                        myMouseListener.type = 37;
                        component.addMouseListener(myMouseListener);
                        break;
                    case "OpenFileChooser":
                        for (MouseListener mouseListener : component.getMouseListeners()) {
                            if (mouseListener instanceof MyMouseKeySizeListener) {
                                component.removeMouseListener(mouseListener);
                            }
                        }
                        myMouseListener.type = 38;
                        component.addMouseListener(myMouseListener);
                        break;
                    case "HeightTextField":
                        text = (JTextField) component;
                        text.setText(curKey.height.toString());
                        keyListener1.textField = text;
                        keyListener1.type = 5;
                        for (KeyListener listener : text.getKeyListeners()) {
                            component.removeKeyListener(listener);
                        }
                        component.addKeyListener(keyListener1);
                        break;
                    case "WidthTextField":
                        text = (JTextField) component;
                        text.setText(curKey.width.toString());
                        keyListener1.textField = text;
                        keyListener1.type = 4;
                        for (KeyListener listener : text.getKeyListeners()) {
                            component.removeKeyListener(listener);
                        }
                        component.addKeyListener(keyListener1);
                        break;
                    case "PersistanceTextField":
                        text = (JTextField) component;
                        text.setText(curKey.tone.name);
                        keyListener1.textField = text;
                        keyListener1.type = 3;
                        for (KeyListener listener : text.getKeyListeners()) {
                            component.removeKeyListener(listener);
                        }
                        text.setText(curSelectedKey.persistance.toString());
                        component.addKeyListener(keyListener1);
                        break;
                    case "SetToneTextField":
                        // nom de la touche text field
                        // j'ai retrouver mon composant donc je supprime les
                        // listener qui lui son attaché et je rattache le nouveau
                        // liée a la bonne touche
                        text = (JTextField) component;
                        text.setText(curKey.tone.name);
                        keyListener1.textField = text;

                        for (KeyListener listener : text.getKeyListeners()) {
                            component.removeKeyListener(listener);
                        }
                        component.addKeyListener(keyListener1);
                        break;
                    case "DisplayNameTextField":
                        // label de la touche text field
                        text = (JTextField) component;
                        text.setText(curKey.label);
                        for (KeyListener listener : text.getKeyListeners()) {
                            component.removeKeyListener(listener);
                        }
                        // listener pour modifier le label de la touche
                        //System.out.println("set DisplayName Listener");
                        keyListener1.type = 1;
                        keyListener1.textField = text;
                        component.addKeyListener(keyListener1);
                        break;
                    case "BorderColorButton":
                        // change la couleur de la bordure
                        for (MouseListener mouseListener : component.getMouseListeners()) {
                            if (mouseListener instanceof MyMouseKeySizeListener) {
                                component.removeMouseListener(mouseListener);
                            }
                            //System.out.println("supprimer listener " + component.getName());
                        }
                        //myMouseListener.controller = ctrl;
                        myMouseListener.type = 40;
                        component.addMouseListener(myMouseListener);
                        break;
                    case "ColorButton":
                        // change la couleur de la bordure
                        for (MouseListener mouseListener : component.getMouseListeners()) {
                            if (mouseListener instanceof MyMouseKeySizeListener) {
                                component.removeMouseListener(mouseListener);
                            }
                            //System.out.println("supprimer listener " + component.getName());
                        }
                        //myMouseListener.controller = ctrl;
                        myMouseListener.type = 39;
                        component.addMouseListener(myMouseListener);
                        break;
                    case "SizeBorderTextField":
                        // TextField pour la taille des bordure
                        text = (JTextField) component;

                        for (KeyListener listener : text.getKeyListeners()) {
                            component.removeKeyListener(listener);
                        }
                        // listener pour modifier le label de la touche
                        //System.out.println("set DisplayName Listener");
                        keyListener1.type = 2;
                        keyListener1.textField = text;
                        keyListener1.checkBokDown = (JCheckBox) getComponentByName(this.propBar, "ShowBordureDown");
                        keyListener1.checkBokUp = (JCheckBox) getComponentByName(this.propBar, "ShowBordureUp");
                        keyListener1.checkBokRight = (JCheckBox) getComponentByName(this.propBar, "ShowBordureRight");
                        keyListener1.checkBokLeft = (JCheckBox) getComponentByName(this.propBar, "ShowBordureLeft");
                        component.addKeyListener(keyListener1);
                        break;
                    case "ToneList":
                        // list des sons de la touche
                        //System.out.println("set ToneList Listener");
                        combo = null;
                        if (component instanceof JComboBox<?>) {
                            combo = (JComboBox<?>) component;
                        }
                        if (combo == null) {
                            break;
                        }
                        for (ActionListener actionListener : combo.getActionListeners()) {
                            combo.removeActionListener(actionListener);
                        }
                        combo.setSelectedIndex(curKey.tone.timbre.ordinal());
                        timbreListener = (ActionEvent) -> {
                            //System.out.println("setTone");
                            setToneTimbre(curKey);
                        };
                        combo.addActionListener(timbreListener);
                        break;

                    case "PrintLabelCheckBox":
                        JCheckBox checko = (JCheckBox) component;
                        for (MouseListener mouseListener : component.getMouseListeners()) {
                            if (mouseListener instanceof MyMouseKeySizeListener) {
                                component.removeMouseListener(mouseListener);
                                //System.out.println("supprimer listener " + component.getName());
                            }
                            //System.out.println("supprimer listener " + component.getName());
                        }

                        myMouseListener.checkBok = checko;
                        this.checkBoxShowKeyLabel = checko;
                        myMouseListener.type = 41;
                        checko.addMouseListener(myMouseListener);
                        break;
                    case "TonePlusRightButton":
                        for (MouseListener mouseListener : component.getMouseListeners()) {
                            component.removeMouseListener(mouseListener);
                            //System.out.println("supprimer listener " + component.getName());
                        }
                        // j'ai retrouver mon composant donc je supprime les
                        // listener qui lui son attaché et je rattache le nouveau
                        // liée a la bonne touche
                        component.addMouseListener(myMouseListener);
                        break;
                    case "ToneMinusRightButton":
                        for (MouseListener mouseListener : component.getMouseListeners()) {
                            component.removeMouseListener(mouseListener);
                            //System.out.println("supprimer listener " + component.getName());
                        }
                        // on change le type pour changer le role du listener
                        myMouseListener.type = 0;
                        component.addMouseListener(myMouseListener);
                        break;
                    case "TonePlusTopButton":
                        for (MouseListener mouseListener : component.getMouseListeners()) {
                            component.removeMouseListener(mouseListener);
                            //System.out.println("supprimer listener " + component.getName());
                        }
                        myMouseListener.type = 2;
                        component.addMouseListener(myMouseListener);
                        break;
                    case "ToneMinusTopButton":
                        for (MouseListener mouseListener : component.getMouseListeners()) {
                            component.removeMouseListener(mouseListener);
                            //System.out.println("supprimer listener " + component.getName());
                        }
                        myMouseListener.type = 3;
                        component.addMouseListener(myMouseListener);
                        break;
                    case "DeleteKey":
                        for (MouseListener mouseListener : component.getMouseListeners()) {
                            component.removeMouseListener(mouseListener);
                            //System.out.println("supprimer listener " + component.getName());
                        }
                        // 42 pour delete par exemple

                        myMouseListener.type = 42;
                        component.addMouseListener(myMouseListener);
                        break;
                    default:
                        break;
                }
            }
            i++;
        }
    }

    private void setToneTimbre(Key curSelectedKey) {
        getAllComponents(this.propBar).stream().filter((component) -> ("ToneList".equals(component.getName()))).forEachOrdered((component) -> {
            JComboBox<?> combo = (JComboBox<?>) component;
            if (curSelectedKey == null) {
                System.err.println("Selectionne une touche !!!");
            } else {
                curSelectedKey.tone.timbre = MidiInstrument.valueOf(combo.getSelectedItem().toString());
                curSelectedKey.tone.samplePath = "";
                //System.out.println("set Tone instrumentName : "
                //        + combo.getSelectedItem().toString());
                component.repaint();
            }
        });
    }

    private Component getComponentByName(final Container c, String name) {
        List<Component> comps = getAllComponents(c);
        Component res = null;
        for (Component comp : comps) {
            if (comp instanceof Container) {
                if (name != null && name.equals(comp.getName())) {
                    res = comp;
                    return res;
                }
            }
        }
        return res;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddInstrumentButton;
    private javax.swing.JPanel ButtonPannel;
    private javax.swing.JButton CancelColorButton;
    private javax.swing.JColorChooser ColorChooser;
    private javax.swing.JButton CreateModeButton;
    private javax.swing.JButton DelInstrumentButton;
    private javax.swing.JFileChooser FileChooser;
    private javax.swing.JPanel FileChooserPanel;
    private javax.swing.JComboBox<String> GabaritList;
    private javax.swing.JButton HidePanel;
    private ulaval.ui.InstrumentPanel InstrumentDisplay;
    private javax.swing.JComboBox<String> InstrumentsComboBox;
    private javax.swing.JButton Key1;
    private javax.swing.JButton Key2;
    private javax.swing.JButton Key3;
    private javax.swing.JButton Key4;
    private javax.swing.JButton Key5;
    private javax.swing.JPanel KeysPanel;
    private javax.swing.JButton MetroActionButton;
    private javax.swing.JLabel MetroLabel;
    private javax.swing.JTextField MetroTextField;
    private javax.swing.JPanel NavPanel;
    private javax.swing.JButton NextGabaritButton;
    private javax.swing.JButton PlayModeButton;
    private javax.swing.JButton PrevGabaritButton;
    private javax.swing.JButton SaveColorButton;
    private javax.swing.JLabel SearchLabel;
    private javax.swing.JComboBox<String> SearchList;
    private javax.swing.JTextField SearchTextField;
    private javax.swing.JLabel TimbreLabel;
    private javax.swing.JComboBox<String> TimbreList;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JButton muteButton;
    private javax.swing.JButton muteButton2;
    // End of variables declaration//GEN-END:variables

    JFileChooser getJFileChooser() {
        return this.FileChooser;
    }

    private List<Component> getAllComponents(final Container c) {
        Component[] comps = c.getComponents();
        List<Component> compList = new ArrayList<>();
        for (Component comp : comps) {
            compList.add(comp);
            if (comp instanceof Container) {
                compList.addAll(getAllComponents((Container) comp));
            }
        }
        return compList;
    }

    private void removeAllListener() {
        int i = 0;

        // parcour les composant et supprime les mouse listener pour remettre les 
        // correspondant et les keyListener
        for (Component component : getAllComponents(this.propBar)) {

            if (null != component.getName()) {
                switch (component.getName()) {
                    case "HeightTextField":
                    case "WidthTextField":
                    case "DisplayNameTextField":
                    case "PersistanceTextField":
                    case "SizeBorderTextField":
                    case "SetToneTextFiled":
                        for (KeyListener listener : component.getKeyListeners()) {
                            if (listener instanceof MyKeyListener) {
                                component.removeKeyListener(listener);
                            }
                        }
                        // listener pour modifier le label de la touche
                        break;
                    case "ToneList":
                    case "ToneValue":
                    case "OctaveValue":
                    case "OtherValue":
                        //System.out.println("set ToneList Listener");
                        JComboBox<?> combo = null;
                        if (component instanceof JComboBox<?>) {
                            combo = (JComboBox<?>) component;
                        }
                        if (combo == null) {
                            break;
                        }
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
                    case "ShowBordureUp":
                    case "ShowBordureRight":
                    case "ShowBordureDown":
                        for (MouseListener mouseListener : component.getMouseListeners()) {
                            if (mouseListener instanceof MyMouseKeySizeListener) {
                                component.removeMouseListener(mouseListener);
                            }
                            //System.out.println("supprimer listener " + component.getName());
                        }

                        break;

                    default:

                        break;
                }
            }
            i++;
        }
    }

    /**
     * Doit supprimer toute les valeur dans la barre de propriete remettre les
     * champs par default
     */
    private void removeAllPropValue() {
        int i = 0;

        // parcour les composant et supprime les mouse listener pour remettre les 
        // correspondant et les keyListener
        for (Component component : this.propBar.getComponents()) {

            if (null != component.getName()) {
                switch (component.getName()) {
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
            }
            i++;
        }
    }

    private void changeSelectedInstrument(String instrumentName) {
        if (InstrumentsComboBox != null && instrumentName != null
                && ctrl != null) {
            selectedInstrument = new Instrument();

            System.out.println("name : " + instrumentName);
            ctrl.setInstrument(instrumentName);
            selectedInstrument = ctrl.getCurrentInstrument();
            System.out.println("Play Instrument : " + InstrumentsComboBox.getSelectedIndex());
            //InstrumentDisplay.repaint();

            // mettre l'id generator a id + i en fonction des touches deja presente dans l'instru
            // selectionné
            for (int i = 0; i < selectedInstrument.listKeys.size(); i++) {
                if (selectedInstrument.listKeys.get(i).id > idGenerator) {
                    idGenerator = selectedInstrument.listKeys.get(i).id;
                }
            }
            idGenerator++;
            System.out.println("id Generator : " + idGenerator);
            //notifyInstrumentPrinter();
            //Initialiser l'instrument
            //refresh InstrumentDisplay et redesiner l'instrument qui s'appel
            //InstrumentList.getSelectedValue()
            curSelectedKey = new Key();
            removeAllListener();
            removeAllPropValue();
            //registerButtonEditKey(curSelectedKey);
            refreshSize();
            ctrl.recalculKeyCoord();
            InstrumentDisplay.repaint();
        }
    }

    private void addSavedInstrument() {
        DefaultComboBoxModel<String> combo = new DefaultComboBoxModel<>();
        for (Instrument instru : ctrl.getSavedInstrument()) {
            combo.addElement(instru.name);
        }
        // bind la list à listModel
        //InstrumentList.
        //ScrollPanelList.add(InstrumentList);
        InstrumentsComboBox.setModel(combo);
        InstrumentsComboBox.setSelectedIndex(combo.getSize() - 1);
    }

    private void setOtherValue(Key curKey) {
        getAllComponents(this.propBar).stream().filter((component) -> ("OtherValue".equals(component.getName()))).forEachOrdered((component) -> {
            JComboBox<?> combo = (JComboBox<?>) component;
            if (curSelectedKey == null) {
                System.err.println("!!! Selectionne une touche !!!");
            } else {
                curSelectedKey.tone.name = getToneValue();
                //System.out.println("set Tone instrumentName : "
                //        + combo.getSelectedItem().toString());
                component.repaint();
            }
        });
    }

    private String getToneValue() {
        String res = "";
        String tones[] = {"A", "B", "C", "D", "E", "F", "G"};
        String octaves[] = {"1", "2", "3", "4", "5", "6", "7", "8"};
        String other[] = {"", "b", "#"};

        JComboBox<?> combo = (JComboBox<?>) getComponentByName(this.propBar, "ToneValue");

        res += tones[combo.getSelectedIndex()];

        combo = (JComboBox<?>) getComponentByName(this.propBar, "OctaveValue");

        res += octaves[combo.getSelectedIndex()];

        combo = (JComboBox<?>) getComponentByName(this.propBar, "OtherValue");

        res += other[combo.getSelectedIndex()];

        return res;
    }

    private void setOctave(Key curKey) {
        getAllComponents(this.propBar).stream().filter((component) -> ("OctaveValue".equals(component.getName()))).forEachOrdered((component) -> {
            JComboBox<?> combo = (JComboBox<?>) component;
            if (curSelectedKey == null) {
                System.err.println("!!! Selectionne une touche !!!");
            } else {
                curSelectedKey.tone.name = getToneValue();
                //System.out.println("set Tone instrumentName : "
                //        + combo.getSelectedItem().toString());
                component.repaint();
            }
        });
    }

    private void setToneValue(Key curKey) {
        getAllComponents(this.propBar).stream().filter((component) -> ("ToneValue".equals(component.getName()))).forEachOrdered((component) -> {
            JComboBox<?> combo = (JComboBox<?>) component;
            if (curSelectedKey == null) {
                System.err.println("!!! Selectionne une touche !!!");
            } else {
                curSelectedKey.tone.name = getToneValue();
                //System.out.println("set Tone instrumentName : "
                //        + combo.getSelectedItem().toString());
                component.repaint();
            }
        });
    }

    private void registerToneCombo(String name) {
        String tones[] = {"A", "B", "C", "D", "E", "F", "G"};
        String octaves[] = {"1", "2", "3", "4", "5", "6", "7", "8"};
        String other[] = {"", "b", "#"};

        JComboBox<?> combo = (JComboBox<?>) getComponentByName(this.propBar, "ToneValue");
        int i = 0;
        for (String tone : tones) {
            if (name != null && name.contains(tone)) {
                combo.setSelectedIndex(i);
            }
            i++;
        }
        i = 0;
        combo = (JComboBox<?>) getComponentByName(this.propBar, "OctaveValue");

        for (String tone : octaves) {
            if (name != null && name.contains(tone)) {
                combo.setSelectedIndex(i);
            }
            i++;
        }
        i = 0;

        combo = (JComboBox<?>) getComponentByName(this.propBar, "OtherValue");

        for (String tone : other) {
            if (name != null && name.contains(tone)) {
                combo.setSelectedIndex(i);
            }
            i++;
        }
        i = 0;

    }
}
