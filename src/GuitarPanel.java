import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

@SuppressWarnings("serial")
public class GuitarPanel extends InstrumentPanel implements ItemListener, ActionListener {
  
  GuitarCanvas canvas;

  int numberOfFrets[] = {3,5,7,9,12, 15,17,19,21,24};

  static int NOTE_PLAYED = 0;
  static int NOTE_GHOST = 1;
  static int NOTE_TUNING = 2;
  
  JComboBox<Integer> numberOfFretsJComboBox;
  JComboBox<GuitarTuning> tuningsJComboBox;
  JButton clearButton;
  
  int fretBoardMarkerRadius;
  int noteMarkerRadius;


  boolean drawPlayedNotes = true;
  boolean drawGhostNotes = false;

  public GuitarPanel() {
    
    clearButton = new JButton("Clear");

    numberOfFretsJComboBox = new JComboBox<>();
    for (int i=0; i<numberOfFrets.length; i++)
      numberOfFretsJComboBox.addItem(Integer.valueOf(numberOfFrets[i]));
    numberOfFretsJComboBox.setSelectedIndex(5);
    
    tuningsJComboBox = new JComboBox<>();
    for (int i=0; i<GuitarTuning.guitarTunings.length; i++)
      tuningsJComboBox.addItem(GuitarTuning.guitarTunings[i]);

    canvas = new GuitarCanvas(
      ((Integer)numberOfFretsJComboBox.getSelectedItem()).intValue(),
      (GuitarTuning)tuningsJComboBox.getSelectedItem()
    
    );
    
    tuningsJComboBox.addItemListener(this);
    numberOfFretsJComboBox.addItemListener(this);
    clearButton.addActionListener(this);
    
    JPanel componentsPanel = new JPanel(new FlowLayout());
    componentsPanel.add(clearButton);
    componentsPanel.add(new JLabel("  #frets: "));
    componentsPanel.add(numberOfFretsJComboBox);
    componentsPanel.add(new JLabel("  tuning: "));
    componentsPanel.add(tuningsJComboBox);
    
    // todo: choose something sensible    
    setPreferredSize(new Dimension(400,200));
    
    setLayout(new BorderLayout());
    add("Center",canvas);
    add("North",componentsPanel);

  }
  
  public String getName() {
    return new String("Guitar"); 
  }

  public void actionPerformed(ActionEvent e){
    Object source = e.getSource();
    if (source==clearButton) {
      canvas.clear();
    }
  }

  public void itemStateChanged(ItemEvent e) {
    Object source = e.getSource();
    if (source==numberOfFretsJComboBox) {
      Integer index = (Integer)numberOfFretsJComboBox.getSelectedItem();
      canvas.setNumberOfFrets(index.intValue());
      
    } else if (source==tuningsJComboBox) {
      canvas.setTuning((GuitarTuning)tuningsJComboBox.getSelectedItem());
      
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
    
  Vector<NotesChangedListener> notesChangedListeners = new Vector<>();
  
  public void addNotesChangedListener(NotesChangedListener ncl) {
     notesChangedListeners.add(ncl);
  }

  public void removeNotesChangedListener(NotesChangedListener ncl) {
     notesChangedListeners.remove(ncl);
  }

  public void notifyListeners(int[] notes) {
    for (int i=0; i<notesChangedListeners.size(); i++) {
      notesChangedListeners.get(i).notesChanged(notes);
    }
  }


  protected class GuitarCanvas extends JPanel implements MouseListener {
     
     
    Rectangle[] stringRectangles;
    Rectangle[] fretBoardRectangles;
    private int NO_HIT = -1;
    
    Dimension dimension = new Dimension(-1,-1);
  
    int[] tuning;
    int numberOfFrets; // The nut is counted as a fret too
    int frets[] = new int[0];
    int[] ghostNotes = new int[0];  
    
    //Font noteFont = null;
    boolean updateFont = true;
    
    int NOT_STRUMMED = -1;
    int strummed_index;
  
    public GuitarCanvas(int numberOfFrets, GuitarTuning guitarTuning) {
      
      this.numberOfFrets = numberOfFrets+1; // The nut is counted as a fret too 
      strummed_index = this.numberOfFrets;
  
      // http://en.wikipedia.org/wiki/Guitar_tuning#Alternate_tunings
      setTuning(guitarTuning);
      
      addMouseListener(this);
      setSize(400,140);
    }
    
    public void setNumberOfFrets(int numberOfFrets) {
      this.numberOfFrets = numberOfFrets+1; // The nut is counted as a fret too 
      this.strummed_index = this.numberOfFrets;
      for (int i=0; i<tuning.length; i++) {
        frets[i] =  (frets[i]<this.numberOfFrets ? this.frets[i] : NOT_STRUMMED );
      }
      updateRectangles();
      repaint();
    }
    
    private void setTuning(GuitarTuning guitarTuning) {
      
      int[] newTuning = guitarTuning.getOpenNotes();
      
      // Copy the current finger positions into the old one
      int[] newFrets = new int[newTuning.length];
      for (int i=0; i<newTuning.length; i++) {
        newFrets[i] =  (i<this.frets.length ? this.frets[i] : NOT_STRUMMED );
      }
      this.frets = newFrets;
      this.tuning = newTuning;
      
      updateRectangles();
      repaint();

      notifyListeners(getNotes());
    }
    
    public void updateRectangles() {
      int width = getWidth();
      int height = getHeight();
      
      double stringHeight = 1.0;
      double firstFretBoardWidth = 2.5*stringHeight;
  
      // Compute the width of the entire fretBoard
      double totalWidth = 0.0;
      double currentFretBoardWidth = firstFretBoardWidth;
      for (int i=0; i<numberOfFrets; i++) {
        totalWidth += currentFretBoardWidth;
        currentFretBoardWidth *= 0.947; 
      }
      // Now add some padding, and then the strumming part
      totalWidth += firstFretBoardWidth; // padding
      totalWidth += stringHeight; // strumming part
      
      double totalHeight = tuning.length*stringHeight;
  
      double pixelsPerUnitX = width/totalWidth;  
      double pixelsPerUnitY = height/(totalHeight+0.5);  
      double pixelsPerUnit = 0.9*Math.min(pixelsPerUnitX, pixelsPerUnitY);
      
      int xPadding = (int)(0.5*(width-pixelsPerUnit*totalWidth));
      int yPadding = (int)(0.5*(height-pixelsPerUnit*totalHeight));
      
      // Horizontal rectangles for strings
      stringRectangles = new Rectangle[tuning.length];
      int x = xPadding;
      int totalWidthI = (int)(totalWidth*pixelsPerUnit);
      for (int i=0; i<tuning.length; i++) {
        int y1 = yPadding + (int)(i*stringHeight*pixelsPerUnit);
        int y2 = yPadding + (int)((i+1)*stringHeight*pixelsPerUnit);
        stringRectangles[tuning.length-1-i] = new Rectangle(x,y1,totalWidthI,y2-y1);
      }
      
      // Vertical rectangles for frets 
      fretBoardRectangles = new Rectangle[numberOfFrets+1]; // don't forget strummed part
      int y = yPadding;
      int totalHeightI = (int)(totalHeight*pixelsPerUnit);


      totalWidth = 0.0;
      currentFretBoardWidth = firstFretBoardWidth;
      for (int i=0; i<numberOfFrets; i++) {
        int x1 = xPadding + (int)(totalWidth*pixelsPerUnit);
        int x2 = xPadding + (int)((totalWidth+currentFretBoardWidth)*pixelsPerUnit);
        fretBoardRectangles[i] = new Rectangle(x1,y,x2-x1,totalHeightI);
        
        totalWidth += currentFretBoardWidth;
        currentFretBoardWidth *= 0.947;
      }
      // Don't forget the strummed part
      totalWidth += firstFretBoardWidth;
      int x1 = xPadding + (int)(totalWidth*pixelsPerUnit);
      int x2 = xPadding + (int)((totalWidth+stringHeight)*pixelsPerUnit);
      fretBoardRectangles[strummed_index] = new Rectangle(x1,y,x2-x1,totalHeightI);
  
      // Reset some other measurer, 1.0 is the height of string rectangle 
      fretBoardMarkerRadius = (int)(0.15*pixelsPerUnit);
      noteMarkerRadius = (int)(0.4*pixelsPerUnit);
  
      // I would like to update the fonts here too. Unfortunately, I need a 
      // Graphics object for that, which is only available in drawInstrument(). I 
      // just set the font to null, and drawInstrument() will know it needs 
      // updating.
      updateFont=true;
    }
  
    public void drawNeck(Graphics g) {
      Dimension newDimension = getSize();
      if (!dimension.equals(newDimension)) {
        // If the size of the canvas changed, the keys must be updated.
        updateRectangles();
        dimension = newDimension;
      }

      
      if (updateFont) {
        // Determine the new fonts
        int font_size = 72;
        boolean fits = false;
        while ( (!fits) && (font_size>5) ) {
          font_size--;
          g.setFont( new Font("Times",Font.BOLD,font_size) );
          if (g.getFontMetrics().stringWidth("g#") < 0.9*2*noteMarkerRadius) {
            fits = true;
          }
        }
      }
      //noteFont = g.getFont();
      //g.setFont(noteFont);
      

  /*    
    fretBoardColor = new Color(184,63,10);
    strummedStringColor = new Color(200,200,200);
    mutedStringColor = new Color(220,90,30);
    markerColor = new Color(243,235,187);
    nutColor = markerColor;
    fretColor = new Color(220,220,220);
    */
  
        
      Rectangle r;
      
      // Draw frets and fretBoard, as well as the numbers below
      for (int f=0; f<fretBoardRectangles.length; f++) {
        r = fretBoardRectangles[f];
        
        if (f==0) g.setColor(ColorScheme.nutColor);
        else g.setColor(ColorScheme.fretBoardColor);
        g.fillRect(r.x,r.y,r.width,r.height);
  
        // Draw fret or nut left of rectangle
        if (f==0) g.setColor(ColorScheme.nutColor);
        else g.setColor(ColorScheme.fretColor);
        int x = r.x+r.width;
        int y1 = r.y;
        int y2 = r.y+r.height;
        for (int dx=-3; dx<0; dx++) { 
          g.drawLine(x+dx, y1, x+dx, y2);
        }

        if (f<numberOfFrets) {
          g.setColor(Color.black);
          String numString = ""+f; 
          FontMetrics fontMetrics = g.getFontMetrics();
          int fontHeight = fontMetrics.getHeight();
          int fontWidth = fontMetrics.stringWidth(numString);
          g.drawString(numString, x-fontWidth/2, y2+fontHeight);
        }
      }
      
      // Draw markers
      g.setColor(ColorScheme.markerColor);
      int fretBoardCenterY = stringRectangles[stringRectangles.length/2-1].y;
      for (int f=0; f<fretBoardRectangles.length; f++) {      
        int fmod12 = f%12; 
        if ( fmod12==3 || fmod12==5 || fmod12==7 || fmod12==9 ) { 
          r = fretBoardRectangles[f];
          int centerX = r.x + r.width/2;
          g.fillOval(centerX-fretBoardMarkerRadius,fretBoardCenterY-fretBoardMarkerRadius,2*fretBoardMarkerRadius,2*fretBoardMarkerRadius);
          
        } else if (fmod12==0 && f>1) {
          r = fretBoardRectangles[f];
          int centerX = r.x + r.width/2;
          int dy = stringRectangles[tuning.length/2-1].height;     
          g.fillOval(centerX-fretBoardMarkerRadius,fretBoardCenterY-fretBoardMarkerRadius-dy,2*fretBoardMarkerRadius,2*fretBoardMarkerRadius);
          g.fillOval(centerX-fretBoardMarkerRadius,fretBoardCenterY-fretBoardMarkerRadius+dy,2*fretBoardMarkerRadius,2*fretBoardMarkerRadius);
          
        }
      }
        
      // Draw strings
      for (int s=0; s<stringRectangles.length; s++) {
        if (frets[s] == NOT_STRUMMED) g.setColor(ColorScheme.mutedStringColor);
        else g.setColor(ColorScheme.strummedStringColor);
        r = stringRectangles[s];
        for (int dy=0; dy<=1; dy++) { 
          int y = r.y+r.height/2+dy;
          g.drawLine(r.x, y, r.x+r.width, y);
        }
      }

      // Draw tuning
      Rectangle nutRectangle = fretBoardRectangles[0];
      int centerX = nutRectangle.x + nutRectangle.width/2;
      for (int string=0; string<tuning.length; string++) {
        Rectangle stringRectangle = stringRectangles[string];        
        int centerY = stringRectangle.y + stringRectangle.height/2;
        String noteString = Note.noteStrings[toNote(string,0)];
        drawNote(noteString,centerX,centerY,g,NOTE_TUNING);
      }
      
  
      // Draw tab at the end
      r = fretBoardRectangles[strummed_index];
      g.setColor(Color.WHITE);
      g.fillRect(r.x,r.y,r.width,r.height);
      
    }
  
    public int[] getNotes() {
      int nNotes = 0;
      for (int i=0; i<tuning.length; i++) 
        if (frets[i]>=0) // if (strummed[i]) 
          nNotes++;
      
      int[] notes = new int[nNotes];
      nNotes = 0;
      for (int i=0; i<tuning.length; i++)
        if (frets[i]>=0) // if (strummed[i]) 
          notes[nNotes++] = toNote(i,frets[i]);
  
      return notes;
    }
  
    public void clear() {
      for (int i=0; i<tuning.length; i++) frets[i] = NOT_STRUMMED;
      repaint();
      notifyListeners(new int[0]);
    }
  
  
    public void drawInstrument(Graphics g) {
      //updateRectangles();
      drawNeck(g);
      if (drawGhostNotes) drawGhostNotes(g);
      if (drawPlayedNotes) drawPlayedNotes(g);
    }
  
    public void drawGhostNotes(Graphics g) {
      for (int string=0; string<tuning.length; string++) {
        Rectangle stringRectangle = stringRectangles[string];        
        int centerY = stringRectangle.y + stringRectangle.height/2;
  
        for (int fret=0; fret<numberOfFrets; fret++) {
          int note = toNote(string,fret);
          
          if (contains(note,ghostNotes)) {
            Rectangle fretBoardRectangle = fretBoardRectangles[fret];
            int centerX = fretBoardRectangle.x + fretBoardRectangle.width/2;
  
            String noteString = Note.noteStrings[toNote(string,fret)];
            drawNote(noteString,centerX,centerY,g,NOTE_GHOST);
          }
        }
      }
    }
  
    public void drawPlayedNotes(Graphics g) {
      for (int string=0; string<tuning.length; string++) {
        if (frets[string] != NOT_STRUMMED) {
          Rectangle fretBoardRectangle = fretBoardRectangles[frets[string]];        
          Rectangle stringRectangle = stringRectangles[string];
  
          int centerX = fretBoardRectangle.x + fretBoardRectangle.width/2;
          int centerY = stringRectangle.y + stringRectangle.height/2;
  
          String noteString = Note.noteStrings[toNote(string,frets[string])];
          drawNote(noteString,centerX,centerY,g,NOTE_PLAYED);
        }
        updateBridge(string,g);
      }
    }
    
    public int toNote(int string, int fret) {
      return (tuning[string] + fret)%12;
    }
      
    public void updateBridge(int string, Graphics g) {
      Rectangle fretBoardRectangle = fretBoardRectangles[strummed_index];        
      Rectangle stringRectangle = stringRectangles[string];
  
      int centerX = fretBoardRectangle.x + fretBoardRectangle.width/2;
      int centerY = stringRectangle.y + stringRectangle.height/2;
    
      String s = "X";
      if (frets[string] != NOT_STRUMMED) s = ""+frets[string];
  
      FontMetrics fontMetrics = g.getFontMetrics();
      int fontHeight = fontMetrics.getHeight();
      int fontWidth = fontMetrics.stringWidth(s);
  
      g.setColor(Color.black);
      g.drawString(s, centerX-fontWidth/2, centerY+fontHeight/2);
    }
    
    public void notesChanged(int[] ghostNotes) {
      this.ghostNotes = ghostNotes;
      repaint();
    }
    
    public void drawNote(String note, int x, int y, Graphics g) {
      drawNote(note,x,y,g,NOTE_PLAYED);
    }
    
    public void drawNote(String note, int x, int y, Graphics g, int noteType) {
      
      if (noteType==NOTE_GHOST) g.setColor(ColorScheme.ghostColor);
      else if (noteType==NOTE_TUNING) g.setColor(ColorScheme.nutColor);
      else g.setColor(ColorScheme.playedColor);
      g.fillOval(x-noteMarkerRadius,y-noteMarkerRadius,2*noteMarkerRadius,2*noteMarkerRadius);

      g.setColor(Color.black);
      if (noteType!=NOTE_TUNING) {
        g.drawOval(x-noteMarkerRadius,y-noteMarkerRadius,2*noteMarkerRadius,2*noteMarkerRadius);
      }

      FontMetrics fontMetrics = g.getFontMetrics();
      y = y+(fontMetrics.getHeight()/3);
      x = x-(fontMetrics.stringWidth(note)/2);
      g.drawString(note,x,y);
      //g.drawString(note,x,y);
    }
  
    public void mouseExited(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
  
    public int getStringClicked(Point clickedPoint) {
      for (int s=0; s<stringRectangles.length; s++)
        if (stringRectangles[s].contains(clickedPoint))
          return s;
      return NO_HIT;
    }
      
    public int getFretClicked(Point clickedPoint) {
      for (int f=0; f<fretBoardRectangles.length; f++)
        if (fretBoardRectangles[f].contains(clickedPoint))
          return f;
      return NO_HIT;
    }
    
    public void mouseClicked(MouseEvent e){
      Point clickedPoint = e.getPoint();
      int stringClicked = getStringClicked(clickedPoint);
      if (stringClicked==NO_HIT) return;
  
      int fretClicked = getFretClicked(clickedPoint);
      if (fretClicked==NO_HIT) return;
      
      
      //System.out.println("Clicked: "+stringClicked+" "+fretClicked);
      if (frets[stringClicked] != fretClicked) {
        frets[stringClicked] = (fretClicked==strummed_index?NOT_STRUMMED:fretClicked);
        int[] notes = getNotes();
        //System.out.println(Note.toString(notes));
        notifyListeners(notes);
        repaint();
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
