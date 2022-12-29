import java.awt.*;
import javax.swing.*;
import java.util.Vector;
import java.awt.event.*;

@SuppressWarnings("serial")
public class ChordFinderApplication extends JFrame implements ItemListener {
  
	ChordChoosePanel chordChoosePanel = new ChordChoosePanel();
	ChordConstructionPanel constructionPanel = new ChordConstructionPanel();
  
  InstrumentPanel[] instrumentPanels = new InstrumentPanel [] {
    new GuitarPanel(),
    new PianoPanel()
  };
  

  JCheckBox showGhostNotesJCheckBox;

	public ChordFinderApplication() {
    
    // Make components
		JTabbedPane instrumentTabbedPane = new JTabbedPane();
    for (int i=0; i<instrumentPanels.length; i++)
      instrumentTabbedPane.add(instrumentPanels[i].getName(),instrumentPanels[i]);
        
    chordChoosePanel.addChordChangedListener(constructionPanel);
    for (int i=0; i<instrumentPanels.length; i++) {
      instrumentPanels[i].addNotesChangedListener(chordChoosePanel);
      constructionPanel.addNotesChangedListener(instrumentPanels[i]);
    }

    showGhostNotesJCheckBox = new JCheckBox("Show chord in instrument");
    showGhostNotesJCheckBox.addItemListener(this);

    

    // Make layout
    JPanel constructionPanelWrapper = new JPanel(new BorderLayout());
    constructionPanelWrapper.add("North",showGhostNotesJCheckBox);
    constructionPanelWrapper.add("Center",constructionPanel);
    
    JPanel chordPanel = new JPanel(new FlowLayout());
		chordPanel.add(chordChoosePanel);
		chordPanel.add(new JLabel("   "));
		chordPanel.add(constructionPanelWrapper);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add("Center",instrumentTabbedPane);
		getContentPane().add("South",chordPanel);

	}
	
  public void itemStateChanged(ItemEvent e){
    Object source = e.getSource();
    if (source==showGhostNotesJCheckBox) {
      for (int i=0; i<instrumentPanels.length; i++) {
        instrumentPanels[i].setShowGhostNotes(showGhostNotesJCheckBox.isSelected());
      }
    }
  }

  public static void main(String[] args) {
    ChordFinderApplication frame = new ChordFinderApplication();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setPreferredSize(new Dimension(1200, 600));
    frame.pack();
    frame.setVisible(true);		
	}


}
