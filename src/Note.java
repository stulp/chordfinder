/**
 * A note is an integer value that ranges from 0 to 11, which corresponds to 'c' 
 * to 'b'.
 * Note objects do not exist. It could have inherited from Integer or something,
 * but I prefer to keep it a light-weight 'int'. That's why java has primitive 
 * types anyway! 
 */ 
public class Note {

  public static int C   =  0; /**< The 'C' constant */
  public static int CIS =  1; /**< The 'CIS' constant */
  public static int D   =  2; /**< The 'D' constant */
  public static int DIS =  3; /**< The 'DIS' constant */
  public static int E   =  4; /**< The 'E' constant */
  public static int F   =  5; /**< The 'F' constant */
  public static int FIS =  6; /**< The 'FIS' constant */
  public static int G   =  7; /**< The 'G' constant */
  public static int GIS =  8; /**< The 'GIS' constant */
  public static int A   =  9; /**< The 'A' constant */
  public static int AIS = 10; /**< The 'AIS' constant */
  public static int B   = 11; /**< The 'B' constant */
  
  /**
   * The names of the different notes
   */ 
  public static String[] noteStrings = {"c","c#","d","d#","e","f","f#","g","g#","a","a#","b"};

  /**
   * Returns if a note 'note' is sharp or not.
   * @param note Note whose sharpness is determined
   * @return true if this note is sharp, false otherwise
   */
  public static boolean isSharp(int note) {
    if (note==CIS) return true;
    if (note==DIS) return true;
    if (note==FIS) return true;
    if (note==GIS) return true;
    if (note==AIS) return true;
    return false;
  }
  
	/**
   * Get the intervals between an array of notes and a root note
   * @param notes Notes of which the intervals to the root note will be 
   * determined.
   * @param rootNote Note to which the interval distance will be computed. 
   * @return The intervals of the notes in 'notes' to the root note
   * Some examples:
   *   getIntervals( [c,e,g], c ) => [0,4,7]  
   *   getIntervals( [c,e,g], e ) => [8,0,3]
   *   getIntervals( [c,e,g], b ) => [1,5,8]
   */
  public static int[] getIntervals(int[] notes, int rootNote) {
    int[] intervals = new int[notes.length];
    for (int i=0; i<intervals.length; i++) 
      intervals[i] = (notes[i]+12-rootNote)%12;
    return intervals;
  }

  
  /**
   * Convert a string to a note.
   * @param string A string representing a note.
   * @return the integer representation of the note, and -1 if 'string' does not
   * represent a note.
   */
  public static int toNote(String string) {
    string = string.toLowerCase();
    for (int i=0; i<noteStrings.length; i++) {
      if (string.equals(noteStrings[i])) return i;
    }
    System.err.println("WARNING: String '"+string+"' is not a note. Returning '-1'.");
    return -1;
  }
  
  /**
   * Returns a string representation of a note.
   * @param note The note
   * @return String representation of this note.
   * @see Note::noteStrings
   */
  public static String toString(int note) {
    return noteStrings[note%12]; 
  }

  /**
   * Returns a string representation of an array of notes.
   * @param notes The array of notes.
   * @return String representation of this array of notes.
   * @see Note::toString
   */
  public static String toString(int[] notes) {
    String string = new String("[");
    for (int i=0; i<notes.length; i++)
      string += (i==0?"":",") + toString(notes[i]);
    string += "]";
    return string; 
  }

  /**
   * main function that tests some functionality of this class 
   * @param arguments A list of notes, e.g. "a b# c"
   * @see Note::toNote(int)
   * @see Note::toString(int[])
   * @see Note::toString(int)
   * @see Note::getIntervals(int[],int)
   */
  public static void main(String[] arguments) {
    int[] notes = { Note.C, Note.DIS, Note.G, Note.B }; 
    if (arguments.length>0) {
      notes = new int[arguments.length]; 
      for (int i=0; i<arguments.length; i++) 
        notes[i] = Note.toNote(arguments[i]);
    }
    System.out.println("notes = "+Note.toString(notes));
    
    for (int rootNote=Note.C; rootNote<Note.FIS; rootNote++) {
      System.out.print("root note = "+toString(rootNote)+" => [");
      int[] intervals = getIntervals(notes,rootNote);
      for (int i=0; i<intervals.length; i++) {
        System.out.print((i==0?"":", ")+intervals[i]); 
      }
      System.out.println("]");
    }

  }

}
