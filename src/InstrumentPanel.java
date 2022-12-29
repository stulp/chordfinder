import javax.swing.*;
import java.awt.*;
import java.util.Vector;

@SuppressWarnings("serial")
public abstract class InstrumentPanel extends JPanel implements NotesChangedListener {
  
  public abstract String getName();

  public abstract void setShowGhostNotes(boolean drawGhostNotes);
  public abstract void setShowPlayedNotes(boolean drawPlayedNotes);
  public abstract void addNotesChangedListener(NotesChangedListener ncl);
  public abstract void removeNotesChangedListener(NotesChangedListener ncl);
  
  /*
  public void setShowGhostNotes(boolean drawGhostNotes): {
    instrumentCanvas.setShowGhostNotes(drawGhostNotes);
  }
  public void setShowPlayedNotes(boolean drawPlayedNotes) {
    instrumentCanvas.setShowPlayedNotes(drawPlayedNotes);
  }
  public void addNotesChangedListener(NotesChangedListener ncl) {
    instrumentCanvas.addNotesChangedListener(ncl);
  }
  public void removeNotesChangedListener(NotesChangedListener ncl) {
    instrumentCanvas.removeNotesChangedListener(ncl);
  }
  */
}
