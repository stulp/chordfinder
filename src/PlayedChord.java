/**
 * A PlayedChord is a Chord, with the added specification of which notes of the 
 * chord are played.
 * For instance, the notes [a,c#,e,d,f#] represent the chord "A13" with the 
 * intervals [0, 4, 7, 10, 14, 17, 21], of which the intervals 10 (g) and 14 (b) 
 * aren't played.
 */
public class PlayedChord extends Chord implements Comparable {
  
  /**
   * The intervals that are actually played.
   * It is always a subset of Chord::intervals.
   */ 
  protected int[] played_intervals;
  
  
  /**
   * Initializing constructor.
   * @param chord The chord being played.
   * This constructor assumes no notes are played at all. 
   */
  public PlayedChord(Chord chord) {
    super(chord);
    played_intervals = new int[0];
  }
  
  /**
   * Initializing constructor.
   * @param chord The chord being played.
   * @param played_intervals The intervals that are actually played.
   */
  public PlayedChord(Chord chord, int[] played_intervals) {
    super(chord);
    this.played_intervals = played_intervals;
  }
  
  /**
   * Returns whether the bass note is the root note.
   * For instance, the note 'a' is the root note of the chord 'Am'. The bass
   * note is played note with the lowest frequency. So if the played chord is
   * [a,c,e] this function returns true. For [c,a,e] it returns false.
   * @return true if the bass note is the root note, false otherwise.
   */
  public boolean bassNoteIsRootNote() {
    return isBassNote(intervals[0]); 
  }

  /**
   * Returns whether the root note is played.
   * For instance, the note 'b' is the root note of the chord 'Bm11'. The notes
   * [d,f#,a,c#,e] might constitute a 'Bm11' chord, but do not contain the note 
   * 'b'. For this example the function would return false.
   * @return true if the root note is played, false otherwise.
   */
  public boolean isRootNotePlayed() {    
    return isIntervalPlayed(intervals[0]); 
  }

  /**
   * Returns whether all the notes in the chord are actually played.
   * For instance, the 'E9' can be played as [e,g#,b,d,f#], but also as 
   * [e,g#,b,f#]. Only in the first case are all the notes that could constitute
   * 'E9' actually played, and would this function return true for 'E9'.  
   * @return true if all the notes in the chord are actually played, false 
   * otherwise.
   */
  public boolean areAllNotesPlayed() {
    for (int i=0; i<intervals.length; i++)
      if (!isIntervalPlayed(intervals[i]))
        return false;
    return true;
  }
  
  public boolean isIntervalPlayed(int interval) {
    if (contains(interval%12,played_intervals)) return true;
    return false;
  }

  public int compareTo(Object otherObject) {
    PlayedChord otherChord = (PlayedChord)otherObject;
    if (otherChord.isRootNotePlayed()==this.isRootNotePlayed()) {
      if (otherChord.getIntervals().length == this.intervals.length) {
        if (this.bassNoteIsRootNote()==otherChord.bassNoteIsRootNote()) {
          return super.compareTo(otherObject);
        } 
        if (this.bassNoteIsRootNote()) return -1; // The other isn't played!
        return 1; // The other is played!
      }
      return super.compareTo(otherObject);
    }
    if (this.isRootNotePlayed()) return -1; // The other isn't played!
    return 1; // The other is played!
  }

  public boolean isBassNote(int interval) {
    if (played_intervals.length==0) return false;
    if (interval%12==played_intervals[0]) return true;
    return false;
  }

  public String toString() {
    return Note.noteStrings[rootNote].toUpperCase()+getChordSymbol();
  }
  
  public String toStringLong() {
    String string = "PlayedChord["+Note.noteStrings[rootNote].toUpperCase()+getChordSymbol()+",(";
    int[] intervals_truncated = getTruncatedIntervals();     
    int[] notes = getNotes();    
   
    for (int i=0; i<intervals_truncated.length; i++) {
      if (i!=0) string += " ";
      
      String noteString = ""+intervals[i]+Note.toString(notes[i]);
      if (isIntervalPlayed(intervals_truncated[i])) {
        // This note is actually played
        noteString = noteString.toUpperCase();
        if (isBassNote(intervals_truncated[i])) {
          // Bass note
          noteString = "["+noteString+"]";
        }
      }
      string += ""+noteString+"";
    }
    string += ")]";
    return string; 
  }
  
  public static boolean contains(int element, int[] array) {
    for (int i=0; i<array.length; i++) {
      if (element == array[i]) return true; 
    }
    return false;
  }

}
