/**
 * Defines the root note and intervals that constitute a chord.
 * A Chord is simply a AbstractChord, with a specific root note. For instance,
 * "major" is an abstract chord (symbol), whereas "Cmajor" is a chord.
 */
public class Chord extends AbstractChord implements Comparable {
  
  /**
   * The root note of this chord.
   */ 
  protected int rootNote;
  
  /**
   * Copy constructor.
   * @param chord is cloned into this Chord.
   */
  public Chord(Chord chord) {
    super(chord.getChordSymbol(),chord.getIntervals());
    this.rootNote = chord.getRootNote();
  }

  /**
   * Initializing constructor.
   * @param rootNote This chord's root note.
   * @param abstractChord This chord's chord symbol and intervals.
   */
  public Chord(int rootNote, AbstractChord abstractChord) {
    super(abstractChord);
    this.rootNote = rootNote;
  }


  /**
   * Get the notes that constitute this chord.
   * For instance, if the chord is "Emajor", this function returns [4,8,11], 
   * which is equivalent to [Note::E, Note::GIS, Note::B].
   * @return The notes that constitute this chord.
   * @see Note
   */
  public int[] getNotes() {
    int[] notes = new int[intervals.length];
    for (int i=0; i<intervals.length; i++)
      notes[i] = (intervals[i]+rootNote)%12;
    return notes;
  }
  
  /**
   * Compares two chords
   * Simply calls AbstractChord::compareTo()
   * @param otherObject The other chord
   * @return the value of AbstractChord::compareTo()
   * @see AbstractChord::compareTo()
   */
  public int compareTo(Object otherObject) {
    return super.compareTo(otherObject);
  }
  
  /**
   * Return this chord's root note.
   * @return This chord's root note.
   */
  public int getRootNote() {
    return rootNote; 
  }
  
  /**
   * Return this chord's name.
   * @return This chord's name, e.g. Em11.
   */
  public String getName() {
    return Note.noteStrings[rootNote].toUpperCase()+getChordSymbol();    
  }
  
  /**
   * Returns a string representation of this chord.
   * @return String representation of this chord.
   */
  public String toString() {
    String string = "Chord["+Note.noteStrings[rootNote].toUpperCase()+getChordSymbol()+",(";
    int[] notes = getNotes();    
    for (int i=0; i<intervals.length; i++) {
      string += (i==0?"":" ") + intervals[i];
      string += "_"+Note.toString(notes[i]);
    }
    string += ")]";
    return string; 
  }

  /**
   * main function that prints a fairly random list of chords.
   * @param arguments Are ignored.
   */
  public static void main(String[] arguments) {
    for (int c=0; c<abstractChords.length; c++) {
      Chord chord = new Chord(c%12,abstractChords[c]);
      System.out.println("chord "+c+" = "+chord);
    }
  }
  

}
