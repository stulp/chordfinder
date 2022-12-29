import java.util.Arrays;
import java.util.Comparator;

/**
 * Defines the intervals that constitute a chord, without a specific root note.
 */
public class AbstractChord {
  
  /**
   * The abstract chord's symbol, e.g. "maj7" or "m7+5-9". 
   */ 
  protected String chordSymbol;
  
  /**
   * These are the intervals to the root note that define the chord.
   * If the root note would be "c", the intervals are defined as follows:
   * 
   * c c#  d d#  e  f  f# g g#  a a#  b  c c#  d d#  e  f  f# g g#  a a#  b  
   * 0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 
   *
   * For instance, the intervals of the "major" chord are [0, 4, 7], which
   * correspond to the root note, third and fifth respectively.
   */
  protected int[] intervals;

	public static String[] intervalNames = {	
 //  0     1     2     3     4     5     6     7     8     9    10    11  
    "1",  "x",  "2", "b3",  "3",  "4", "b5",  "5", "#5",  "6", "b7",  "7",
 // 12    13    14    15    16    17    18    19    20    21    22    23 
    "8", "b9",  "9","b10", "10" ,"11","#11",  "x",  "x", "13",  "x",  "x"
  };
  
  /**
   * Copy constructor.
   * @param abstractChord is cloned into this AbstractChord 
   */
  public AbstractChord(AbstractChord abstractChord) {
    this.chordSymbol = abstractChord.getChordSymbol();
    this.intervals = abstractChord.getIntervals();
    //System.out.println("CONSTRUCTED"+toString());
  }
  
  /**
   * Initializing constructor.
   * @param chordSymbol This abstract chord's symbol
   * @param intervals This abstract chord's intervals
   */
  public AbstractChord(String chordSymbol, int[] intervals) {
    this.chordSymbol = chordSymbol;
    this.intervals = intervals;
  }
  
  /**
   * Returns this abstract chord's chord symbol.
   * @return This abstract chord's chord symbol.
   */
  public String getChordSymbol() {
    return chordSymbol; 
  }

  /**
   * Returns this abstract chord's intervals.
   * @return This abstract chord's intervals.
   */
  public int[] getIntervals() {
    return intervals; 
  }
  
  /**
   * Returns this abstract chord's intervals, truncated to 12.
   * Essentially returns AbstractChord::intervals, but first make sure its
   * values do not exceed 12 by calling "modulo 12" on all elements.
   * @return This abstract chord's intervals, truncated to 12.
   */
  public int[] getTruncatedIntervals() {
    int[] truncated_intervals = new int [intervals.length];    
    for (int i=0; i<intervals.length; i++)
      truncated_intervals[i] = intervals[i]%12;
    return truncated_intervals; 
  }
  
  /**
   * Returns a string representation of this abstract chord.
   * @return String representation of this abstract chord.
   */
  public String toString() {
    return getChordSymbol(); 
  }
  
  public String toStringLong() {
    //String string = "AbstractChord["+getChordSymbol()+",(";    
    String string = getChordSymbol()+" : ";    
    for (int i=0; i<intervals.length; i++) {
        string += (i==0?"":" ") + intervals[i];
    }
    //string += ")]";
    return string; 
  }
  
	/**
   * Returns the abstract chord from AbstractChord::abstractChords that best 
   * matches the given intervals.
   *
	 * @param intervals The given intervals for which a match will be found. All 
   * its values should be in the range [0,12].  
	 * @return The abstract chord that best matches the given intervals. 
   *
   * The given intervals are compared to those from the abstract chords in 
   * AbstractChord::abstractChords, and the best is returned. Here, best is 
   * defined as:
   * - An exact match
   * - If there is no exact match, the chord with the lowest index from 
   * AbstractChord::abstractChords whose intervals are a superset of those of 
   * the given interval.
   *
   * For instance, if the given intervals are [0, 4, 7], "major" is and exact
   * match, so it considered the best. 
   * For [0, 4, 10], there is no exact match, and there are several chords it is
   * a subset of (e.g. "7", "7-5","9" etc.) The best one is the one with the 
   * lowest index, which is "7".
   */
	public static AbstractChord findBestAbstractChord(int[] intervals) {
		AbstractChord bestAbstractChord = null;
    // Search from bottom to top, so chord with lower indices overwrite those
    // with higher ones.
    for (int c = abstractChords.length-1; c>=0; c--) {
      // Since the values of intervals are in the range [0,12], so should the
      // intervals with which it is compared.
      int[] abstract_chord_intervals = abstractChords[c].getTruncatedIntervals();

      if (Arrays.equals(intervals,abstract_chord_intervals)) {
        // Exactly the same, so return this chord without further ado
        return abstractChords[c]; 
      } else if (subset(intervals,abstract_chord_intervals)) {
        // A subset is not an exact match, but good enough.
        // A better subset might overwrite this one later.
        bestAbstractChord = abstractChords[c];
      }
    }
		return bestAbstractChord;
	}

  /**
   * subset function that determines if all elements in array1 occur in array2. 
   * Duplicates in array1 are ignored.
   * @param array1 The 'subset' array
   * @param array2 The 'superset' array
   * @return true is array1 is a subset of array2, false otherwise
   */
  public static boolean subset(int[] array1, int[] array2) {
    
    for (int i1=0; i1<array1.length; i1++) {
      int element1 = array1[i1];
      boolean element1_in_array2 = false;
      for (int i2=0; i2<array2.length; i2++) {
        if (array2[i2]==element1) element1_in_array2 = true; 
      }
      // If element1 from array1 was not in array2, then array1 is not a subset
      // of array2
      if (!element1_in_array2) return false;
    }
    return true;
  }

  /**
   * Compares two abstract chords
   * @param otherObject The other abstract chord
   * @return -1 if this abstract chord's index in AbstractChord::abstractChords
   * is smaller than that of otherObject, 1 if the reverse hold, and 0 if their 
   * indices are equal. 
   */
  protected int compareToAbstract(AbstractChord otherAbstractChord) {
    // Sorting is done according to the index in 'abstractChords'
    int thisIndex = this.getIndex();
    int otherIndex = otherAbstractChord.getIndex();
    if (thisIndex<otherIndex) return -1;
    if (thisIndex>otherIndex) return 1;
    return 0;
  }
    
  /**
   * Get the index of this abstract chord in AbstractChord::abstractChords.
   * @return The index of this abstract chord in AbstractChord::abstractChords.
   */
  public int getIndex() {
    for (int c=0; c<abstractChords.length; c++) 
      if (chordSymbol.equals(abstractChords[c].getChordSymbol()))
        return c;
    return -1;
  }
  
  public static String[] getChordSymbols() {
    String chordSymbols[] = new String[abstractChords.length];
    for (int i=0; i<abstractChords.length; i++)
      chordSymbols[i] = abstractChords[i].getChordSymbol();
    return chordSymbols; 
  }
  
  public static int getMaxNumberOfIntervals() {
    int max = 0; 
    for (int i=0; i<abstractChords.length; i++) {
      int n = abstractChords[i].getIntervals().length;
      max = ( n>max ? n : max );
    }
    return max;
  }
  

  /**
   * This array lists all known (in the context of this program) abstract 
   * chords. Each has a name, as well as the intervals that define it.
   * The order matters, as chords with lower indexes are considered 
   * to be "simpler". So, the intervals [0,4,7] are both in "major" and "13", 
   * but "major" is the prefered nomenclature, as it is simpler. Therefore, 
   * "major" has a lower index than "13".
   */
  protected static AbstractChord abstractChords[] = {
    // 1           3        5        7           9       11          13
    // c c#  d d#  e  f  f# g g#  a a#  b  c c#  d d#  e  f  f# g g#  a a#  b  
    // 0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 
    new AbstractChord("1",     new int[] { 0                         }),
    new AbstractChord("5",       new int[] { 0,  7                     }),
    new AbstractChord("major",   new int[] { 0,  4,  7                 }),
    new AbstractChord("m",       new int[] { 0,  3,  7                 }),
    new AbstractChord("dim",     new int[] { 0,  3,  6                 }),
    new AbstractChord("+5",      new int[] { 0,  4,  8                 }),
    new AbstractChord("m+5",     new int[] { 0,  3,  8                 }),
    new AbstractChord("sus2",    new int[] { 0,  2,  7                 }),
    new AbstractChord("sus4",    new int[] { 0,  5,  7                 }),
    new AbstractChord("7",       new int[] { 0,  4,  7, 10             }),
    new AbstractChord("m7",      new int[] { 0,  3,  7, 10             }),
    new AbstractChord("maj7",    new int[] { 0,  4,  7, 11             }),
    new AbstractChord("6",       new int[] { 0,  4,  7,  9             }),
    new AbstractChord("m6",      new int[] { 0,  3,  7,  9             }),
    new AbstractChord("7sus2",   new int[] { 0,  2,  7, 10             }),
    new AbstractChord("7sus4",   new int[] { 0,  5,  7, 10             }),
    new AbstractChord("7-5",     new int[] { 0,  4,  6, 10             }),
    new AbstractChord("m7-5",    new int[] { 0,  3,  6, 10             }),
    new AbstractChord("7+5",     new int[] { 0,  4,  8, 10             }),
    new AbstractChord("m7+5",    new int[] { 0,  3,  8, 10             }),
    new AbstractChord("9",       new int[] { 0,  4,  7, 10, 14         }),
    new AbstractChord("m9",      new int[] { 0,  3,  7, 10, 14         }),
    new AbstractChord("maj9",    new int[] { 0,  4,  7, 11, 14         }),
    new AbstractChord("9sus4",   new int[] { 0,  5,  7, 10, 14         }),
    new AbstractChord("6*9",     new int[] { 0,  4,  7,  9, 14         }),
    new AbstractChord("m6*9",    new int[] { 0,  3,  7,  9, 14         }),
    new AbstractChord("7-9",     new int[] { 0,  4,  7, 10, 13         }),
    new AbstractChord("m7-9",    new int[] { 0,  3,  7, 10, 13         }),
    new AbstractChord("7-10",    new int[] { 0,  4,  7, 10, 15         }),
    new AbstractChord("9+5",     new int[] { 0, 10, 13                 }),     
    new AbstractChord("m9+5",    new int[] { 0, 10, 14                 }),
    new AbstractChord("7+5-9",   new int[] { 0,  4,  8, 10, 13         }),
    new AbstractChord("m7+5-9",  new int[] { 0,  3,  8, 10, 13         }),
    new AbstractChord("11",      new int[] { 0,  4,  7, 10, 14, 17     }),
    new AbstractChord("m11",     new int[] { 0,  3,  7, 10, 14, 17     }),
    new AbstractChord("maj11",   new int[] { 0,  4,  7, 11, 14, 17     }),
    new AbstractChord("11+",     new int[] { 0,  4,  7, 10, 14, 18     }),
    new AbstractChord("m11+",    new int[] { 0,  3,  7, 10, 14, 18     }),
    new AbstractChord("13",      new int[] { 0,  4,  7, 10, 14, 17, 21 }),
    new AbstractChord("m13",     new int[] { 0,  3,  7, 10, 14, 17, 21 }),
    
    
  };

  /**
   * main function that prints the list of abstract chords in 
   * AbstractChord:abstractChords.
   * @param arguments Are ignored.
   */
  public static void main(String[] arguments) {
    for (int c=0; c<abstractChords.length; c++) {
      System.out.println("abstractChord["+c+"] = "+abstractChords[c].toStringLong());
    }
  }
  
  
}


