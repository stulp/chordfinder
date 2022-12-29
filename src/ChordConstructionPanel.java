import javax.swing.*;
import java.lang.System;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

import javax.swing.BorderFactory; 
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;


public class ChordConstructionPanel extends JPanel implements ChordChangedListener {
  
  private PlayedChord playedChord;
  private ChordConstructionCanvas canvas;
  boolean reallyNotifyListeners = false;

  public ChordConstructionPanel() {

    canvas = new ChordConstructionCanvas();
    
    // Make layout
    setLayout(new BorderLayout());
    add("Center",canvas);
    
    //solver_panel.setBorder(BorderFactory.createTitledBorder("Solving"));
    //Border raisedbevel = new BorderBorderFactory.createRaisedBevelBorder();
    TitledBorder titledBorder = new TitledBorder("No Chord");
    setBorder(titledBorder);
  }


  public void chordChanged(PlayedChord chord) {
    if (chord==null) {
      setBorder(new TitledBorder("No Chord"));
    } else {
      setBorder(new TitledBorder(chord.toString()));
    }

    canvas.setPlayedChord(chord);
    if (chord!=null) notifyListeners(chord.getNotes());
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

  public class ChordConstructionCanvas extends JPanel {
  
    private Point circlesUpperLeft[][];
    private Dimension dimension = new Dimension(-1,-1);
    private Font noteFont;
    private int circleDiam;
    
    public ChordConstructionCanvas() {
      setPlayedChord(null);
      int preferredCircleDiam = 50;
      setPreferredSize(new Dimension(7*preferredCircleDiam,2*preferredCircleDiam));
    }
  
    // todo rename to notesChanged ?? should be a listener then
    public void setPlayedChord(PlayedChord newPlayedChord) {
      playedChord = newPlayedChord;
      repaint();
    }
    
  
    public void clear() {
      setPlayedChord(null);
    }
  
    public void updateNoteCenters() {
        int width = getWidth();
        int height = getHeight();
        
        int maxNumberOfIntervals = AbstractChord.getMaxNumberOfIntervals();
        circlesUpperLeft = new Point[2][maxNumberOfIntervals];
        
        double circleRadius = 1.0;
        double horizontalPadding = 0.2;
        double verticalPadding = -0.2;
        
        double totalWidth = maxNumberOfIntervals*2*circleRadius;
        totalWidth += (maxNumberOfIntervals-1)*horizontalPadding;
        double totalHeight = 2*2*circleRadius+verticalPadding;
        
    
        double pixelsPerUnitX = width/totalWidth;  
        double pixelsPerUnitY = height/totalHeight;  
        double pixelsPerUnit = 0.9*Math.min(pixelsPerUnitX, pixelsPerUnitY);
        
        int xPadding = (int)(0.5*(width-pixelsPerUnit*totalWidth));
        int yPadding = (int)(0.5*(height-pixelsPerUnit*totalHeight));
        
        this.circleDiam = (int)(2*circleRadius*pixelsPerUnit);
  
        for (int i=0; i<maxNumberOfIntervals; i++) {
          int x = xPadding + (int)( i*(2*circleRadius+horizontalPadding)*pixelsPerUnit );
          circlesUpperLeft[0][i] = new Point(x,yPadding);
          circlesUpperLeft[1][i] = new Point(x,yPadding+(int)(circleDiam+verticalPadding));
        }
    
        // I would like to update the fonts here too. Unfortunately, I need a 
        // Graphics object for that, which is only available in paintComponent().
        // I just set the font to null, and paintComponent() will know it needs 
        // updating.
        noteFont=null;
      
    }
    
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
  
      Dimension newDimension = getSize();
      if (!dimension.equals(newDimension)) {
        // If the size of the canvas changed, the keys must be updated.
        updateNoteCenters();
        dimension = newDimension;
      }
      
      if (playedChord==null) return;
      
      // todo adapt font size
      g.setFont(new Font("Helvetica",Font.BOLD,16));
      //System.out.println("drawPlayedChord: "+playedChord);
      int[] intervals= playedChord.getIntervals();
      for (int i=0; i<intervals.length; i++) {
        for (int topBottom=0; topBottom<2; topBottom++) {
          int interval = intervals[i];
      
          Point p = circlesUpperLeft[topBottom][i];
          
          // Draw upper and lower circle
          if (playedChord.isIntervalPlayed(interval)) g.setColor(ColorScheme.playedColor);
          else g.setColor(ColorScheme.ghostColor);
          g.fillOval(p.x,p.y,circleDiam,circleDiam);
          
          // Draw interval and tone
          if (playedChord.isBassNote(interval)) g.setColor(ColorScheme.bassColor);
          else g.setColor(Color.black);
    
          FontMetrics metrics =  getFontMetrics(g.getFont());
          int fontHeight = metrics.getHeight();
          
          // todo can be done easier
          String intervalName = ""+interval%12; // AbstractChord.intervalNames[interval];
          String noteName = Note.noteStrings[(interval+playedChord.getRootNote())%12];
          String name = ( topBottom==0 ?  intervalName: noteName );   
          int x = p.x+circleDiam/2-(metrics.stringWidth(name)/2);
          int y = p.y+circleDiam/2+(metrics.getAscent()/2);
          g.drawString(name, x, y);
        }
  
      }
     
    }
    
    public void drawNote(int x, int y, String s, boolean played, boolean bass, Graphics g) {
      if (played) g.setColor(ColorScheme.playedColor);
      else g.setColor(ColorScheme.ghostColor);
      
      g.fillOval(x-14,y-14,28,28);
      
      if (bass) g.setColor(ColorScheme.bassColor);
      else g.setColor(Color.black);
      FontMetrics metrics =  getFontMetrics(g.getFont());
      g.drawString(s,x - (metrics.stringWidth(s)/2),y+(metrics.getAscent()/2));
      
      g.setColor(Color.black);
      g.drawOval(x-14,y-14,28,28);
      g.drawOval(x-15,y-15,30,30);
      
    }
  }
}

