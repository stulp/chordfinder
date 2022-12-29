import java.util.Vector;
import java.util.Arrays;
import java.lang.System;
import java.lang.Math;

public class ChordFinder {
	
	/**
   *
   */
	public static PlayedChord[] findPossibleChords(int[] notes) {
    // One note does not constitute a chord...
    if (notes.length<1) return new PlayedChord[0];
        
    Vector<PlayedChord> possibleChordsVector = new Vector<>();

    // Try all notes as root note
		for (int rootNote=Note.C; rootNote<=Note.B; rootNote++) {
      // Transpose the intervals so rootNote is root note
      int[] intervals = Note.getIntervals(notes,rootNote);
      // Find the best abstract chord, given these intervals
      AbstractChord bestAbstractChord = AbstractChord.findBestAbstractChord(intervals);
      //System.out.print("rootNote="+Note.toString(rootNote));
      if (bestAbstractChord != null) {
        // If a chord was found, add it to the vector
        Chord chord = new Chord(rootNote,bestAbstractChord);
        PlayedChord playedChord = new PlayedChord(chord,intervals);
        //System.out.print(" => "+playedChord);
        
        possibleChordsVector.add(playedChord);
        
      }
      //System.out.println();
		}
    
    // Sort possible chords according to index
    // 1.5
    PlayedChord[] possibleChords = new PlayedChord[possibleChordsVector.size()];
    for (int i=0; i<possibleChords.length; i++)
      possibleChords[i] = possibleChordsVector.get(i);
    Arrays.sort(possibleChords);

		return possibleChords;
  }
  
	/**
   *
   */
  public static void main(String arguments[]) {
    int[] notes = new int[arguments.length]; 
    for (int i=0; i<arguments.length; i++) {
      notes[i] = Note.toNote(arguments[i]); 
    }
    System.out.println("notes="+Note.toString(notes));
    
    PlayedChord[] possibleChords = findPossibleChords(notes);
    System.out.println("------------------------------------");
    System.out.println("possibleChords=");    
    for (int i=0; i<possibleChords.length; i++) {
      System.out.println("  "+possibleChords[i]); 
    }
    
  }
	
}


/**
public static String toString(int[] intervals) {
  String string = new String("[");
  for (int i=0; i<intervals.length; i++) {
    string += (i==0?"":",") + intervals[i];
  }
  string += "]";
  return string; 
}
 */
   

