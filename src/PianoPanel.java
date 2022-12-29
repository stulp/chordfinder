import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

public class PianoPanel extends InstrumentPanel implements ItemListener, ActionListener {
  
  PianoCanvas canvas;

  int numberOfOctaves[] = {1,2,3,4,5,6};

  JComboBox numberOfOctavesJComboBox;

  JButton clearButton;
  
  boolean drawPlayedNotes = true;
  boolean drawGhostNotes = false;
    
  
  public PianoPanel() {
    clearButton = new JButton("Clear");
    clearButton.addActionListener(this);

    canvas = new PianoCanvas();

    numberOfOctavesJComboBox = new JComboBox();
    for (int i=0; i<numberOfOctaves.length; i++) {
      numberOfOctavesJComboBox.addItem(new Integer(numberOfOctaves[i])); 
    }
    numberOfOctavesJComboBox.addItemListener(this);
    numberOfOctavesJComboBox.setSelectedItem(new Integer(4));

    JPanel componentsPanel = new JPanel(new FlowLayout());
    componentsPanel.add(clearButton);
    componentsPanel.add(new JLabel("   #octaves: "));
    componentsPanel.add(numberOfOctavesJComboBox);
    
    // todo: choose something sensible    
    setPreferredSize(new Dimension(400,200));

    setLayout(new BorderLayout());
    add("North",componentsPanel);
    add("Center",canvas);

  }
  
  public String getName() {
    return new String("Piano"); 
  }

  public void actionPerformed(ActionEvent e){
    Object source = e.getSource();
    if (source==clearButton) {
      canvas.clear();
    }
  }
  
  public void itemStateChanged(ItemEvent e) {
    Object source = e.getSource();
    if (source==numberOfOctavesJComboBox) {
      Integer index = (Integer)numberOfOctavesJComboBox.getSelectedItem();
      canvas.setNumberOfOctaves(index.intValue());
      
    } else {
      System.err.println("WARNING: Unknown source in itemStateChanged: "+source); 
    }
  }

  public void notesChanged(int[] notes) {
    canvas.notesChanged(notes);
  }

  public void setShowGhostNotes(boolean drawGhostNotes) {
    if (this.drawGhostNotes != drawGhostNotes) {
      this.drawGhostNotes = drawGhostNotes;
      canvas.repaint();
    }
  }
  
  public void setShowPlayedNotes(boolean drawPlayedNotes) {
    if (this.drawPlayedNotes != drawPlayedNotes) {
      this.drawPlayedNotes = drawPlayedNotes;
      canvas.repaint();
    }
  }
  

  Vector notesChangedListeners = new Vector();

  public void addNotesChangedListener(NotesChangedListener ncl) {
     notesChangedListeners.add(ncl);
  }

  public void removeNotesChangedListener(NotesChangedListener ncl) {
     notesChangedListeners.remove(ncl);
  }

  public void notifyListeners(int[] notes) {
    for (int i=0; i<notesChangedListeners.size(); i++) {
      ((NotesChangedListener)notesChangedListeners.get(i)).notesChanged(notes);
    }
  }

  protected class PianoCanvas extends JPanel implements MouseListener {
    
    Rectangle[] keys; 
    
    // The width of one white key is the unit
    double whiteKeyWidth = 1.0;
    double whiteKeyHeight = 5.0;
  
    double blackKeyWidth = 0.6;
    double blackKeyHeight = 3.5;
  
    Font whiteFont=null;
    Font blackFont=null;
    Dimension dimension = new Dimension(-1,-1);
    
    int numberOfOctaves;
  
    boolean keysStroked[] = new boolean[0];
    int ghostNotes[];
    
  
    public PianoCanvas() {
      //setBackground(new Color(191,191,191));
      addMouseListener(this);
      setSize(700,300);
  
      ghostNotes = new int[0];
      setNumberOfOctaves(4);
    }
        
    public void notesChanged(int[] notes) {
      ghostNotes = notes; 
      repaint();
    }
    
    public void updateKeyRectangles() {
      int width = getWidth();
      int height = getHeight();
      
      double keyBoardWidth = whiteKeyWidth*7*numberOfOctaves;
      double keyBoardHeight = whiteKeyHeight;
      
      double pixelsPerUnitX = width/keyBoardWidth;  
      double pixelsPerUnitY = height/keyBoardHeight;  
      double pixelsPerUnit = 0.9*Math.min(pixelsPerUnitX, pixelsPerUnitY);
      
      int xPadding = (int)(0.5*(width-pixelsPerUnit*keyBoardWidth));
      int yPadding = (int)(0.5*(height-pixelsPerUnit*keyBoardHeight));
      
      keys = new Rectangle[numberOfOctaves*12];
      
      int whiteKeyWidthI = (int)(pixelsPerUnit*whiteKeyWidth);
      int whiteKeyHeightI = (int)(pixelsPerUnit*whiteKeyHeight);
      int blackKeyWidthI = (int)(pixelsPerUnit*blackKeyWidth);
      int blackKeyHeightI = (int)(pixelsPerUnit*blackKeyHeight);
      
      int whiteKeyCount = 0;
      for (int k=0; k<numberOfOctaves*12; k++) {
        int x1 = xPadding + (int)(whiteKeyCount*pixelsPerUnit*whiteKeyWidth);
        if (Note.isSharp(k%12)) {
          x1 -= blackKeyWidthI/2; 
          int x2 = x1 + blackKeyWidthI;
          keys[k] = new Rectangle(x1,yPadding,x2-x1,blackKeyHeightI);
        } else {
          int x2 = xPadding + (int)((whiteKeyCount+1)*pixelsPerUnit*whiteKeyWidth);
          keys[k] = new Rectangle(x1,yPadding,x2-x1,whiteKeyHeightI);
          whiteKeyCount++;
        }
      }
      
      // I would like to update the fonts here too. Unfortunately, I need a 
      // Graphics object for that, which is only available in drawInstrument(). I 
      // just set the font to null, and drawInstrument() will know it needs 
      // updating.
      whiteFont=null;
    }
      
    public void setNumberOfOctaves(int numberOfOctaves) {

      this.numberOfOctaves = numberOfOctaves; 
      
      boolean[] newKeysStroked = new boolean[numberOfOctaves*12];
      for (int i=0; i<newKeysStroked.length; i++) {
        newKeysStroked[i] =  (i<this.keysStroked.length ? this.keysStroked[i] : false );
      }
      this.keysStroked = newKeysStroked;
      updateKeyRectangles();
      repaint();
      notifyListeners(getNotes());
    }
  
    public String getName() {
      return new String("Piano"); 
    }
  
    public void drawNotes(Graphics g) {
      Dimension newDimension = getSize();
      if (!dimension.equals(newDimension)) {
        // If the size of the canvas changed, the keys must be updated.
        updateKeyRectangles();
        dimension = newDimension;
      }
        
      
      // First draw white keys...
      for (int k=0; k<keys.length; k++) {
        if (!Note.isSharp(k%12)) {
          if (keysStroked[k]) { 
            g.setColor(ColorScheme.playedColor);
          } else if (drawGhostNotes && contains(k%12,ghostNotes)) {
            g.setColor(ColorScheme.ghostColor);
          } else {
            g.setColor(Color.white);
          }
          g.fillRect(keys[k].x,keys[k].y,keys[k].width,keys[k].height);
          
          g.setColor(Color.black);
          g.drawRect(keys[k].x,keys[k].y,keys[k].width,keys[k].height);
        }
      }
      
      // ...then draw black keys
      for (int k=0; k<keys.length; k++) {
        if (Note.isSharp(k%12)) {
          if (keysStroked[k]) { 
            g.setColor(ColorScheme.playedColorBlackKey);
          } else if (drawGhostNotes && contains(k%12,ghostNotes)) {
            g.setColor(ColorScheme.ghostColorBlackKey);
          } else {
            g.setColor(Color.black);
          }
          g.fillRect(keys[k].x,keys[k].y,keys[k].width,keys[k].height);
          
          g.setColor(Color.black);
          g.drawRect(keys[k].x,keys[k].y,keys[k].width,keys[k].height);
        }
      }
  
      if (whiteFont==null) {
        // Determine the new fonts
        Font fonts[] = new Font[2];
        for (int f=Note.C; f<=Note.CIS; f++) {
          int font_size = 24;
          boolean fits = false;
          while ( (!fits) && (font_size>5) ) {
            font_size--;
            g.setFont( new Font("Times",Font.BOLD,font_size) );
            if (g.getFontMetrics().stringWidth("g#") < keys[f].width) {
              fits = true;
            }
          }
          fonts[f] = g.getFont();
        }
        whiteFont = fonts[Note.C];
        blackFont = fonts[Note.CIS];
      }
      
      for (int k=0; k<keys.length; k++) {
        if (Note.isSharp(k%12)) {
          g.setFont(blackFont);
          g.setColor(Color.white);
        } else {
          g.setFont(whiteFont); 
          g.setColor(Color.black);
        }
        
        FontMetrics fontMetrics = g.getFontMetrics();
        String noteString = Note.toString(k);
        int fontWidth = fontMetrics.stringWidth(noteString);
        int fontHeight = fontMetrics.getHeight();
        
        int center = keys[k].x + keys[k].width/2;
        int bottom = keys[k].y + keys[k].height;
        
        g.drawString(noteString,center-fontWidth/2,bottom-fontHeight/2);
      }
        
    }
  
        
    public void clear() {
      for (int k=0; k<keysStroked.length; k++) 
        keysStroked[k] = false;
      repaint();
      notifyListeners(new int[0]);
    }
    
    public int[] getNotes() {
      int nNotes = 0;
      for (int k=0; k<keysStroked.length; k++) 
        if (keysStroked[k])
          nNotes++;
      
      int[] notes = new int[nNotes];
      nNotes = 0;
      for (int k=0; k<keysStroked.length; k++) 
        if (keysStroked[k])
          notes[nNotes++] = k%12;
  
      return notes;
    }
  
    public void drawInstrument(Graphics g) {    
      drawNotes(g);
    }
  
    public void toggleKey(int key) {
      keysStroked[key] = !keysStroked[key];    
      repaint();
      notifyListeners(getNotes());
    }
    
    public void mouseExited(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
  
    public void mouseClicked(MouseEvent e) {
      Point clickedPoint = e.getPoint();
      for (int k=0; k<keys.length; k++) {
        if (Note.isSharp(k%12) && keys[k].contains(clickedPoint)) {
          toggleKey(k);
          return;
        }
      }
      for (int k=0; k<keys.length; k++) {
        if (!Note.isSharp(k%12) && keys[k].contains(clickedPoint)) {
          toggleKey(k);
          return;
        }
      }
    }

    
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      drawInstrument(g);
    }
    
        
    public boolean contains(int element, int[] array) {
      for (int i=0; i<array.length; i++) {
        if (element == array[i]) return true; 
      }
      return false;
    }
    
  }
}
