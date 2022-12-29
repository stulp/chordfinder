import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

public class ChordChoosePanel extends JTabbedPane implements NotesChangedListener, ItemListener, ListSelectionListener  {

  //String noChordsFoundString = new String("No chords found.");

  PlayedChord[] foundPlayedChords = new PlayedChord[0];

  JList foundChordsList;
  JCheckBox rootNoteMustBePlayedJCheckBox;
  JCheckBox rootNoteMustBeBassNoteJCheckBox;
  JCheckBox allNotesMustBePlayedJCheckBox;
  

	JList rootNoteList;
	JList chordSymbolList;
  
	public ChordChoosePanel() {
    
    // Initialize components
		//foundChordsList = new JList(new String[] {noChordsFoundString});
		foundChordsList = new JList();
    foundChordsList.setVisibleRowCount(5);
    foundChordsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    rootNoteList = new JList(Note.noteStrings);
    rootNoteList.setVisibleRowCount(9);
    rootNoteList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 

    chordSymbolList = new JList(AbstractChord.abstractChords);
    //chordSymbolList = new JList(AbstractChord.getChordSymbols());
    chordSymbolList.setVisibleRowCount(9);
    chordSymbolList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 

    rootNoteMustBePlayedJCheckBox = new JCheckBox("Root note must be played");
    rootNoteMustBePlayedJCheckBox.setSelected(true);

    allNotesMustBePlayedJCheckBox = new JCheckBox("All notes must be played");
    allNotesMustBePlayedJCheckBox.setSelected(false);

    rootNoteMustBeBassNoteJCheckBox = new JCheckBox("Root note must be bass note");
    rootNoteMustBeBassNoteJCheckBox.setSelected(false);
  
    // Add listeners
    rootNoteMustBePlayedJCheckBox.addItemListener(this);
    rootNoteMustBeBassNoteJCheckBox.addItemListener(this);
    allNotesMustBePlayedJCheckBox.addItemListener(this);
		foundChordsList.addListSelectionListener(this);
		rootNoteList.addListSelectionListener(this);
		chordSymbolList.addListSelectionListener(this);

    // Layout    

    JPanel settingsPanel = new JPanel(new GridLayout(3,1));
    settingsPanel.add(rootNoteMustBePlayedJCheckBox);
    settingsPanel.add(allNotesMustBePlayedJCheckBox);
    settingsPanel.add(rootNoteMustBeBassNoteJCheckBox);
    
    JPanel foundChordsPanel = new JPanel(new BorderLayout());
    foundChordsPanel.add("Center",new JScrollPane(foundChordsList));
    foundChordsPanel.add("South",settingsPanel);
    
    JPanel allChordsPanel = new JPanel(new FlowLayout());
    allChordsPanel.add(new JScrollPane(rootNoteList));
    allChordsPanel.add(new JScrollPane(chordSymbolList));

    add("Found",foundChordsPanel);
    add("All Chords",allChordsPanel);
    //add("Scales",new Label("Under Construction..."));
    
  }
  
  private void clear() { 
    foundChordsList.removeAll();
    notifyListeners(null);
  }

  /**
   *
   */
  public void updateFoundChordsList() {
    Vector playedChords = new Vector();
    for (int i=0; i<foundPlayedChords.length; i++) {
      PlayedChord playedChord = foundPlayedChords[i];

      boolean do_not_include = false;
      
      if (rootNoteMustBeBassNoteJCheckBox.isSelected() && !playedChord.bassNoteIsRootNote())
        do_not_include = true;
      
      if (allNotesMustBePlayedJCheckBox.isSelected() && !playedChord.areAllNotesPlayed())
        do_not_include = true;

      if (rootNoteMustBePlayedJCheckBox.isSelected() && !playedChord.isRootNotePlayed())
        do_not_include = true;

      if (!do_not_include)
        playedChords.add(playedChord);
        
    }
    
    //foundChordsList.removeAll();
    //if (playedChords.size() == 0) {
    //  foundChordsList.setListData(new String[] {noChordsFoundString});
    //} else {
      foundChordsList.setListData(playedChords);
    //}

  }
  
  public void notesChanged(int[] notes) {
    // Find some chords!
    foundPlayedChords = ChordFinder.findPossibleChords(notes);
    updateFoundChordsList();
  }


  public void valueChanged(ListSelectionEvent e) {
    Object source = e.getSource();
    if (source==foundChordsList) {
      if (foundPlayedChords.length==0) {
        notifyListeners(null);
        
      } else {
        Object object = foundChordsList.getSelectedValue();
        //System.out.println(object);
        PlayedChord playedChord = (PlayedChord)object;
        notifyListeners(playedChord);
      }
      
    } else if (source==rootNoteList || source==chordSymbolList) {
      int rootNote = rootNoteList.getSelectedIndex();
      if (rootNote==-1) {
        rootNote = 0;
        rootNoteList.setSelectedIndex(rootNote);
      }

      AbstractChord abstractChord = (AbstractChord)chordSymbolList.getSelectedValue();
      if (abstractChord==null) {
        chordSymbolList.setSelectedIndex(0);
        abstractChord = (AbstractChord)chordSymbolList.getSelectedValue();
      }
      PlayedChord playedChord = new PlayedChord(new Chord(rootNote,abstractChord));
      notifyListeners(playedChord);
    }

  }
      
  /**
   * 
   */
  public void itemStateChanged(ItemEvent e) {
    Object source = e.getSource();
    if (source==rootNoteMustBePlayedJCheckBox 
               || source==rootNoteMustBeBassNoteJCheckBox
               || source==allNotesMustBePlayedJCheckBox) {
      updateFoundChordsList();
    } else {
      System.err.println("WARNING: Unknown source in itemStateChanged: "+source); 
    }
  }
  
  
  
  
  Vector chordChangedListeners = new Vector();

  public void addChordChangedListener(ChordChangedListener ncl) {
     chordChangedListeners.add(ncl);
  }

  public void removeChordChangedListener(ChordChangedListener ncl) {
     chordChangedListeners.remove(ncl);
  }

  public void notifyListeners(PlayedChord playedChord) {
    for (int i=0; i<chordChangedListeners.size(); i++) {
      ((ChordChangedListener)chordChangedListeners.get(i)).chordChanged(playedChord);
    }
  }
  
  
  public static void main(String arguments[]) {
    Frame frame = new Frame();
    frame.setLayout(new BorderLayout());
    ChordChoosePanel panel = new ChordChoosePanel();
    frame.add("Center",panel);
    frame.setSize(300,400);
    frame.setVisible(true);

    int[] notes = new int[arguments.length]; 
    for (int i=0; i<arguments.length; i++) {
      notes[i] = Note.toNote(arguments[i]); 
    }
    System.out.println("notes="+Note.toString(notes));
    panel.notesChanged(notes);
    
  }
  
}

