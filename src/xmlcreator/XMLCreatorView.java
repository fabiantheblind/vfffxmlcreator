/*
 * XMLCreatorView.java
 */

package xmlcreator;

import java.awt.*;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import javax.swing.AbstractAction;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledEditorKit;
import javax.swing.undo.*;
import java.beans.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import org.w3c.dom.Document;
/**
 * The application's main frame.
 */
public class XMLCreatorView extends FrameView {

    public XMLCreatorView(SingleFrameApplication app) {
        super(app);

        initComponents();
       // initDocument();
      //   actions=createActionTable(myTextArea);
        //Add some key bindings.
        addBindings();

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
      
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
     
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");


        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                  
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
    
                } else if ("message".equals(propertyName)) {
                    String text = (String)(evt.getNewValue());
           
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer)(evt.getNewValue());

                }
            }
        });
    }

    /*
      //This one listens for edits that can be undone.
    protected class MyUndoableEditListener
                    implements UndoableEditListener {
        public void undoableEditHappened(final UndoableEditEvent e) {
            //Remember the edit and update the menus.
            undo.addEdit(e.getEdit());
            undoAction.updateUndoState();
            redoAction.updateRedoState();
        }
    }

     */
    	AbstractAction myNew = new AbstractAction("New") {
		/**
		 *
		 */
		private static final long serialVersionUID = -3477966760179323043L;

		public void actionPerformed(ActionEvent e) {
//			saveOld();
			myTextArea.setText("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Root>\n<titel></titel>\n<text></text>\n<zeile></zeile>\n</Root>");
			currentFile = "Untitled";
		//	setTitle(currentFile);
			changed = false;
			//Save.setEnabled(false);
			//SaveAs.setEnabled(false);
		}
	};

     /*
        class UndoAction extends AbstractAction {
   

		public UndoAction() {
            super("Undo");
            setEnabled(false);
        }

        public void actionPerformed(final ActionEvent e) {
            try {
                undo.undo();
            } catch (final CannotUndoException ex) {
                System.out.println("Unable to undo: " + ex);
                ex.printStackTrace();
            }
            updateUndoState();
            redoAction.updateRedoState();
        }

        protected void updateUndoState() {
            if (undo.canUndo()) {
                setEnabled(true);
                putValue(Action.NAME, undo.getUndoPresentationName());
                changed=true;
            } else {
                setEnabled(false);
                putValue(Action.NAME, "Undo");
            }
        }
    }
      
         class RedoAction extends AbstractAction {


		private static final long serialVersionUID = -1819348371159106262L;

		public RedoAction() {
            super("Redo");
            setEnabled(false);
        }

        public void actionPerformed(final ActionEvent e) {
            try {
                undo.redo();
            } catch (final CannotRedoException ex) {
                System.out.println("Unable to redo: " + ex);
                ex.printStackTrace();
            }
            updateRedoState();
            undoAction.updateUndoState();
        }

        protected void updateRedoState() {
            if (undo.canRedo()) {
                setEnabled(true);
                putValue(Action.NAME, undo.getRedoPresentationName());
            } else {
                setEnabled(false);
                putValue(Action.NAME, "Redo");
            }
        }
    }


       */

    /*
  //The following two methods allow us to find an
    //action provided by the editor kit by its name.
    @Action
    private HashMap<Object, Action> createActionTable(final JTextComponent textComponent) {
        final HashMap<Object, Action> actions = new HashMap<Object, Action>();
        final Action[] actionsArray = (Action[]) textComponent.getActions();
        for (int i = 0; i < actionsArray.length; i++) {
            final Action a = actionsArray[i];
            actions.put(a.getValue(Action.NAME), a);
        }
	return actions;
    }

    @Action
    private Action getActionByName(final String name) {
        return actions.get(name);
    }

*/
        //    	this is the key to the xml parser
    protected void initDocument() {

//    	this is the key to the xml parser
        final SimpleAttributeSet[] attrs = initAttributes(initString.length);

        try {
            for (int i = 0; i < initString.length; i ++) {
                doc.insertString(doc.getLength(), initString[i] + newline,
                        attrs[3]);
            }
        } catch (final BadLocationException ble) {
            System.err.println("Couldn't insert initial text.");
        }
    }
         protected SimpleAttributeSet[] initAttributes(final int length) {
        //Hard-code some attributes.
        final SimpleAttributeSet[] attrs = new SimpleAttributeSet[length];

//H1
        attrs[0] = new SimpleAttributeSet();
//        StyleConstants
        StyleConstants.setFontFamily(attrs[0], "Sans-Serif");
        StyleConstants.setFontSize(attrs[0], 23);

//H2
        attrs[1] = new SimpleAttributeSet(attrs[0]);
        StyleConstants.setFontSize(attrs[1], 18);
//      StyleConstants.setBold(attrs[1], true);
//H3
        attrs[2] = new SimpleAttributeSet(attrs[0]);
        StyleConstants.setFontSize(attrs[2], 15);


//Text
        attrs[3] = new SimpleAttributeSet(attrs[0]);
        StyleConstants.setFontSize(attrs[3], 14);
//italic
        attrs[4] = new SimpleAttributeSet(attrs[0]);
//        StyleConstants.setFontFamily(attrs[0], "SansSerif");
        StyleConstants.setFontSize(attrs[4], 14);
        StyleConstants.setItalic(attrs[4], true);

//footnotes
        attrs[5] = new SimpleAttributeSet(attrs[0]);
        StyleConstants.setFontSize(attrs[5], 14);
//        StyleConstants.setForeground(attrs[5], Color.red);

        return attrs;
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = XMLCreatorApp.getApplication().getMainFrame();
            aboutBox = new XMLCreatorAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        XMLCreatorApp.getApplication().show(aboutBox);
    }

        @Action
    public void showChEditor() {
        if (chEditor == null) {
            JFrame mainFrame = XMLCreatorApp.getApplication().getMainFrame();
            chEditor = new XMLCreatorChEditor(mainFrame);
            chEditor.setLocationRelativeTo(mainFrame);
        }
        XMLCreatorApp.getApplication().show(chEditor);

        
    }
              @Action
    public void showFnEditor() {
        if (fnEditor == null) {
            JFrame mainFrame = XMLCreatorApp.getApplication().getMainFrame();
            fnEditor = new XMLCreatorFootnotes(mainFrame);
            fnEditor.setLocationRelativeTo(mainFrame);
        }
        XMLCreatorApp.getApplication().show(fnEditor);


    }
                @Action
    public void showChProp() {
        if (chProp == null) {
            JFrame mainFrame = XMLCreatorApp.getApplication().getMainFrame();
            chProp = new XMLCreatorChProp(mainFrame);
            chProp.setLocationRelativeTo(mainFrame);
        }
        XMLCreatorApp.getApplication().show(chProp);


    }

        protected void addBindings() {
        final InputMap inputMap = myTextArea.getInputMap();

        //Ctrl-b to go backward one character
        KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_B, Event.CTRL_MASK);
        inputMap.put(key, DefaultEditorKit.backwardAction);

        //Ctrl-f to go forward one character
        key = KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK);
        inputMap.put(key, DefaultEditorKit.forwardAction);

        //Ctrl-p to go up one line
        key = KeyStroke.getKeyStroke(KeyEvent.VK_P, Event.CTRL_MASK);
        inputMap.put(key, DefaultEditorKit.upAction);

        //Ctrl-n to go down one line
        key = KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK);
        inputMap.put(key, DefaultEditorKit.downAction);

        //Ctrl-z to go back
       // key = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.CTRL_MASK);
      //  inputMap.put(key,undoAction);
        //Ctrl-v to paste
        key = KeyStroke.getKeyStroke(KeyEvent.VK_V, Event.CTRL_MASK);
        inputMap.put(key,DefaultEditorKit.pasteAction);
        //Ctrl-x for Cut
        key = KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.CTRL_MASK);
        inputMap.put(key,DefaultEditorKit.cutAction);
        //Ctrl-c for copy
        key = KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK);
        inputMap.put(key,DefaultEditorKit.copyAction);
/*
        Action h1Action = new StyledEditorKit.FontSizeAction("H1",23);
        key = KeyStroke.getKeyStroke(KeyEvent.VK_W, Event.CTRL_MASK);
        inputMap.put(key,h1Action);


        Action h2Action =new StyledEditorKit.FontSizeAction("H2",18);
        key = KeyStroke.getKeyStroke(KeyEvent.VK_E, Event.CTRL_MASK);
        inputMap.put(key,h2Action);

        Action h3Action = new StyledEditorKit.FontSizeAction("H3",16);
        key = KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK);
        inputMap.put(key,h3Action);

        Action txtAction = new StyledEditorKit.FontSizeAction("TXT",14);
        key = KeyStroke.getKeyStroke(KeyEvent.VK_T, Event.CTRL_MASK);
        inputMap.put(key,txtAction);

        Action itAction = new StyledEditorKit.ItalicAction();
        key = KeyStroke.getKeyStroke(KeyEvent.VK_I, Event.CTRL_MASK);
        inputMap.put(key,itAction);

        Action noteAction = new StyledEditorKit.FontSizeAction("NOTE",10);
        key = KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK);
        inputMap.put(key,noteAction);

        */

    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        myTextArea = new javax.swing.JTextArea();
        wordCountLabel = new javax.swing.JLabel();
        wordCountResult = new javax.swing.JLabel();
        selectionCountResult = new javax.swing.JLabel();
        selectionCountLabel = new javax.swing.JLabel();
        chapterNoLabel = new javax.swing.JLabel();
        chapterNoResult = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        jMenuItemBook = new javax.swing.JMenuItem();
        jMenuItemNewCh = new javax.swing.JMenuItem();
        jMenuItemOpenBook = new javax.swing.JMenuItem();
        jMenuItemSave = new javax.swing.JMenuItem();
        jMenuItemSaveAs = new javax.swing.JMenuItem();
        javax.swing.JMenuItem jMenueItemExit = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        jMenuItemH4 = new javax.swing.JMenuItem();
        jMenuItemH5 = new javax.swing.JMenuItem();
        jMenuItemH6 = new javax.swing.JMenuItem();
        jMenuItemIT1 = new javax.swing.JMenuItem();
        jMenuItemTXT1 = new javax.swing.JMenuItem();
        styleMenu = new javax.swing.JMenu();
        jMenuItemH1 = new javax.swing.JMenuItem();
        jMenuItemH2 = new javax.swing.JMenuItem();
        jMenuItemH3 = new javax.swing.JMenuItem();
        jMenuItemIT = new javax.swing.JMenuItem();
        jMenuItemTXT = new javax.swing.JMenuItem();
        BookMenu = new javax.swing.JMenu();
        renameBook = new javax.swing.JMenuItem();
        chapterMenu = new javax.swing.JMenu();
        OpenChEditor = new javax.swing.JMenuItem();
        OpenFnEditor = new javax.swing.JMenuItem();
        renameChapter = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        jPopupMenu1 = new javax.swing.JPopupMenu();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(xmlcreator.XMLCreatorApp.class).getContext().getResourceMap(XMLCreatorView.class);
        mainPanel.setBackground(resourceMap.getColor("mainPanel.background")); // NOI18N
        mainPanel.setAutoscrolls(true);
        mainPanel.setInheritsPopupMenu(true);
        mainPanel.setMaximumSize(new java.awt.Dimension(2000, 2000));
        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setPreferredSize(new java.awt.Dimension(0, 0));

        myTextArea.setBackground(resourceMap.getColor("myTextArea.background")); // NOI18N
        myTextArea.setColumns(20);
        myTextArea.setFont(resourceMap.getFont("myTextArea.font")); // NOI18N
        myTextArea.setLineWrap(true);
        myTextArea.setRows(5);
        myTextArea.setText(resourceMap.getString("myTextArea.text")); // NOI18N
        myTextArea.setWrapStyleWord(true);
        myTextArea.setAutoscrolls(false);
        myTextArea.setBorder(null);
        myTextArea.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        myTextArea.setMinimumSize(new java.awt.Dimension(300, 300));
        myTextArea.setName("myTextArea"); // NOI18N
        myTextArea.setPreferredSize(new java.awt.Dimension(300, 500));
        myTextArea.setSelectionColor(resourceMap.getColor("myTextArea.selectionColor")); // NOI18N
        myTextArea.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                myTextAreaMouseClicked(evt);
            }
        });

        wordCountLabel.setText(resourceMap.getString("wordCountLabel.text")); // NOI18N
        wordCountLabel.setName("wordCountLabel"); // NOI18N

        wordCountResult.setText(resourceMap.getString("wordCountResult.text")); // NOI18N
        wordCountResult.setName("wordCountResult"); // NOI18N

        selectionCountResult.setText(resourceMap.getString("selectionCountResult.text")); // NOI18N
        selectionCountResult.setName("selectionCountResult"); // NOI18N

        selectionCountLabel.setText(resourceMap.getString("selectionCountLabel.text")); // NOI18N
        selectionCountLabel.setName("selectionCountLabel"); // NOI18N

        chapterNoLabel.setText(resourceMap.getString("chapterNoLabel.text")); // NOI18N
        chapterNoLabel.setName("chapterNoLabel"); // NOI18N

        chapterNoResult.setText(resourceMap.getString("chapterNoResult.text")); // NOI18N
        chapterNoResult.setName("chapterNoResult"); // NOI18N

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(myTextArea, javax.swing.GroupLayout.DEFAULT_SIZE, 479, Short.MAX_VALUE)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(wordCountLabel)
                        .addComponent(wordCountResult)
                        .addGap(18, 18, 18)
                        .addComponent(selectionCountLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(selectionCountResult)
                        .addGap(81, 81, 81)
                        .addComponent(chapterNoLabel)
                        .addGap(18, 18, 18)
                        .addComponent(chapterNoResult)))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(myTextArea, javax.swing.GroupLayout.DEFAULT_SIZE, 534, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(wordCountResult)
                        .addComponent(selectionCountLabel)
                        .addComponent(selectionCountResult)
                        .addComponent(chapterNoResult)
                        .addComponent(chapterNoLabel))
                    .addComponent(wordCountLabel))
                .addContainerGap())
        );

        menuBar.setBackground(resourceMap.getColor("menuBar.background")); // NOI18N
        menuBar.setBorder(null);
        menuBar.setBorderPainted(false);
        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setBackground(resourceMap.getColor("fileMenu.background")); // NOI18N
        fileMenu.setBorder(null);
        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setFont(resourceMap.getFont("fileMenu.font")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        jMenuItemBook.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemBook.setBackground(resourceMap.getColor("jMenuItemBook.background")); // NOI18N
        jMenuItemBook.setText(resourceMap.getString("jMenuItemBook.text")); // NOI18N
        jMenuItemBook.setBorder(null);
        jMenuItemBook.setBorderPainted(false);
        jMenuItemBook.setName("jMenuItemBook"); // NOI18N
        fileMenu.add(jMenuItemBook);

        jMenuItemNewCh.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemNewCh.setBackground(resourceMap.getColor("jMenuItemNewCh.background")); // NOI18N
        jMenuItemNewCh.setText(resourceMap.getString("jMenuItemNewCh.text")); // NOI18N
        jMenuItemNewCh.setBorder(null);
        jMenuItemNewCh.setBorderPainted(false);
        jMenuItemNewCh.setName("jMenuItemNewCh"); // NOI18N
        fileMenu.add(jMenuItemNewCh);

        jMenuItemOpenBook.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemOpenBook.setBackground(resourceMap.getColor("jMenueItemExit.background")); // NOI18N
        jMenuItemOpenBook.setText(resourceMap.getString("jMenuItemOpenBook.text")); // NOI18N
        jMenuItemOpenBook.setBorder(null);
        jMenuItemOpenBook.setBorderPainted(false);
        jMenuItemOpenBook.setName("jMenuItemOpenBook"); // NOI18N
        fileMenu.add(jMenuItemOpenBook);

        jMenuItemSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemSave.setBackground(resourceMap.getColor("jMenueItemExit.background")); // NOI18N
        jMenuItemSave.setText(resourceMap.getString("jMenuItemSave.text")); // NOI18N
        jMenuItemSave.setBorder(null);
        jMenuItemSave.setBorderPainted(false);
        jMenuItemSave.setName("jMenuItemSave"); // NOI18N
        fileMenu.add(jMenuItemSave);

        jMenuItemSaveAs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemSaveAs.setBackground(resourceMap.getColor("jMenueItemExit.background")); // NOI18N
        jMenuItemSaveAs.setText(resourceMap.getString("jMenuItemSaveAs.text")); // NOI18N
        jMenuItemSaveAs.setBorder(null);
        jMenuItemSaveAs.setBorderPainted(false);
        jMenuItemSaveAs.setName("jMenuItemSaveAs"); // NOI18N
        fileMenu.add(jMenuItemSaveAs);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(xmlcreator.XMLCreatorApp.class).getContext().getActionMap(XMLCreatorView.class, this);
        jMenueItemExit.setAction(actionMap.get("quit")); // NOI18N
        jMenueItemExit.setBackground(resourceMap.getColor("jMenueItemExit.background")); // NOI18N
        jMenueItemExit.setBorder(null);
        jMenueItemExit.setBorderPainted(false);
        jMenueItemExit.setName("jMenueItemExit"); // NOI18N
        fileMenu.add(jMenueItemExit);

        menuBar.add(fileMenu);

        editMenu.setBackground(resourceMap.getColor("editMenu.background")); // NOI18N
        editMenu.setBorder(null);
        editMenu.setText(resourceMap.getString("editMenu.text")); // NOI18N
        editMenu.setFont(resourceMap.getFont("editMenu.font")); // NOI18N
        editMenu.setName("editMenu"); // NOI18N

        jMenuItemH4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemH4.setBackground(resourceMap.getColor("jMenuItemH4.background")); // NOI18N
        jMenuItemH4.setText(resourceMap.getString("jMenuItemH4.text")); // NOI18N
        jMenuItemH4.setName("jMenuItemH4"); // NOI18N
        editMenu.add(jMenuItemH4);

        jMenuItemH5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemH5.setBackground(resourceMap.getColor("jMenuItemH5.background")); // NOI18N
        jMenuItemH5.setText(resourceMap.getString("jMenuItemH5.text")); // NOI18N
        jMenuItemH5.setName("jMenuItemH5"); // NOI18N
        editMenu.add(jMenuItemH5);

        jMenuItemH6.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemH6.setBackground(resourceMap.getColor("jMenuItemH6.background")); // NOI18N
        jMenuItemH6.setText(resourceMap.getString("jMenuItemH6.text")); // NOI18N
        jMenuItemH6.setName("jMenuItemH6"); // NOI18N
        editMenu.add(jMenuItemH6);

        jMenuItemIT1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemIT1.setBackground(resourceMap.getColor("jMenuItemIT1.background")); // NOI18N
        jMenuItemIT1.setText(resourceMap.getString("jMenuItemIT1.text")); // NOI18N
        jMenuItemIT1.setName("jMenuItemIT1"); // NOI18N
        editMenu.add(jMenuItemIT1);

        jMenuItemTXT1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemTXT1.setBackground(resourceMap.getColor("jMenuItemTXT1.background")); // NOI18N
        jMenuItemTXT1.setText(resourceMap.getString("jMenuItemTXT1.text")); // NOI18N
        jMenuItemTXT1.setName("jMenuItemTXT1"); // NOI18N
        editMenu.add(jMenuItemTXT1);

        menuBar.add(editMenu);

        styleMenu.setBackground(resourceMap.getColor("styleMenu.background")); // NOI18N
        styleMenu.setBorder(null);
        styleMenu.setText(resourceMap.getString("styleMenu.text")); // NOI18N
        styleMenu.setFont(resourceMap.getFont("fileMenu.font")); // NOI18N
        styleMenu.setName("styleMenu"); // NOI18N

        jMenuItemH1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_1, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemH1.setBackground(resourceMap.getColor("jMenuItemTXT.background")); // NOI18N
        jMenuItemH1.setText(resourceMap.getString("jMenuItemH1.text")); // NOI18N
        jMenuItemH1.setName("jMenuItemH1"); // NOI18N
        styleMenu.add(jMenuItemH1);

        jMenuItemH2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_2, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemH2.setBackground(resourceMap.getColor("jMenuItemTXT.background")); // NOI18N
        jMenuItemH2.setText(resourceMap.getString("jMenuItemH2.text")); // NOI18N
        jMenuItemH2.setName("jMenuItemH2"); // NOI18N
        styleMenu.add(jMenuItemH2);

        jMenuItemH3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_3, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemH3.setBackground(resourceMap.getColor("jMenuItemTXT.background")); // NOI18N
        jMenuItemH3.setText(resourceMap.getString("jMenuItemH3.text")); // NOI18N
        jMenuItemH3.setName("jMenuItemH3"); // NOI18N
        styleMenu.add(jMenuItemH3);

        jMenuItemIT.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemIT.setBackground(resourceMap.getColor("jMenuItemTXT.background")); // NOI18N
        jMenuItemIT.setText(resourceMap.getString("jMenuItemIT.text")); // NOI18N
        jMenuItemIT.setName("jMenuItemIT"); // NOI18N
        styleMenu.add(jMenuItemIT);

        jMenuItemTXT.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemTXT.setBackground(resourceMap.getColor("jMenuItemTXT.background")); // NOI18N
        jMenuItemTXT.setText(resourceMap.getString("jMenuItemTXT.text")); // NOI18N
        jMenuItemTXT.setName("jMenuItemTXT"); // NOI18N
        styleMenu.add(jMenuItemTXT);

        menuBar.add(styleMenu);

        BookMenu.setBackground(resourceMap.getColor("BookMenu.background")); // NOI18N
        BookMenu.setBorder(null);
        BookMenu.setText(resourceMap.getString("BookMenu.text")); // NOI18N
        BookMenu.setFont(new java.awt.Font("Lucida Grande", 1, 14));
        BookMenu.setName("BookMenu"); // NOI18N

        renameBook.setBackground(resourceMap.getColor("renameBook.background")); // NOI18N
        renameBook.setText(resourceMap.getString("renameBook.text")); // NOI18N
        renameBook.setName("renameBook"); // NOI18N
        BookMenu.add(renameBook);

        menuBar.add(BookMenu);

        chapterMenu.setBackground(resourceMap.getColor("chapterMenu.background")); // NOI18N
        chapterMenu.setBorder(null);
        chapterMenu.setText(resourceMap.getString("chapterMenu.text")); // NOI18N
        chapterMenu.setFont(resourceMap.getFont("chapterMenu.font")); // NOI18N
        chapterMenu.setName("chapterMenu"); // NOI18N

        OpenChEditor.setAction(actionMap.get("showChEditor")); // NOI18N
        OpenChEditor.setBackground(resourceMap.getColor("renameChapter.background")); // NOI18N
        OpenChEditor.setText(resourceMap.getString("OpenChEditor.text")); // NOI18N
        OpenChEditor.setName("OpenChEditor"); // NOI18N
        chapterMenu.add(OpenChEditor);

        OpenFnEditor.setAction(actionMap.get("showFnEditor")); // NOI18N
        OpenFnEditor.setBackground(resourceMap.getColor("renameChapter.background")); // NOI18N
        OpenFnEditor.setText(resourceMap.getString("OpenFnEditor.text")); // NOI18N
        OpenFnEditor.setName("OpenFnEditor"); // NOI18N
        chapterMenu.add(OpenFnEditor);

        renameChapter.setAction(actionMap.get("showChProp")); // NOI18N
        renameChapter.setBackground(resourceMap.getColor("renameChapter.background")); // NOI18N
        renameChapter.setText(resourceMap.getString("renameChapter.text")); // NOI18N
        renameChapter.setName("renameChapter"); // NOI18N
        chapterMenu.add(renameChapter);

        menuBar.add(chapterMenu);

        helpMenu.setBackground(resourceMap.getColor("helpMenu.background")); // NOI18N
        helpMenu.setBorder(null);
        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setFont(resourceMap.getFont("fileMenu.font")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setBackground(resourceMap.getColor("aboutMenuItem.background")); // NOI18N
        aboutMenuItem.setText(resourceMap.getString("aboutMenuItem.text")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        jPopupMenu1.setBorder(null);
        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        setComponent(mainPanel);
        setMenuBar(menuBar);
    }// </editor-fold>//GEN-END:initComponents

    private void myTextAreaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_myTextAreaMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_myTextAreaMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu BookMenu;
    private javax.swing.JMenuItem OpenChEditor;
    private javax.swing.JMenuItem OpenFnEditor;
    private javax.swing.JMenu chapterMenu;
    private javax.swing.JLabel chapterNoLabel;
    private javax.swing.JLabel chapterNoResult;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem jMenuItemBook;
    private javax.swing.JMenuItem jMenuItemH1;
    private javax.swing.JMenuItem jMenuItemH2;
    private javax.swing.JMenuItem jMenuItemH3;
    private javax.swing.JMenuItem jMenuItemH4;
    private javax.swing.JMenuItem jMenuItemH5;
    private javax.swing.JMenuItem jMenuItemH6;
    private javax.swing.JMenuItem jMenuItemIT;
    private javax.swing.JMenuItem jMenuItemIT1;
    private javax.swing.JMenuItem jMenuItemNewCh;
    private javax.swing.JMenuItem jMenuItemOpenBook;
    private javax.swing.JMenuItem jMenuItemSave;
    private javax.swing.JMenuItem jMenuItemSaveAs;
    private javax.swing.JMenuItem jMenuItemTXT;
    private javax.swing.JMenuItem jMenuItemTXT1;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JTextArea myTextArea;
    private javax.swing.JMenuItem renameBook;
    private javax.swing.JMenuItem renameChapter;
    private javax.swing.JLabel selectionCountLabel;
    private javax.swing.JLabel selectionCountResult;
    private javax.swing.JMenu styleMenu;
    private javax.swing.JLabel wordCountLabel;
    private javax.swing.JLabel wordCountResult;
    // End of variables declaration//GEN-END:variables

    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;

    private JDialog aboutBox;
    private JDialog chEditor;
    private JDialog fnEditor;
    private JDialog chProp;
    public HashMap<Object, Action> actions;

    //undo helpers
//    protected UndoAction undoAction;
  //  protected RedoAction redoAction;
    //protected UndoManager undo = new UndoManager();
    private boolean changed = false;
    private String currentFile = "myUntitled.xml";
    private JFileChooser dialog = new JFileChooser(System.getProperty("user.dir"));
    public String newline = "\n";
    public AbstractDocument doc;

        final String initString[] =
    { "This",
	"is",
      "some",
      "startup",
      "text" };

}
