public class GuitarTuning {
  private String name;
  private int[] openNotes;

  public GuitarTuning(String name, int[] openNotes) {
    this.name = name;
    this.openNotes = openNotes;
  }
  
  public String toString() {
    return name; 
  }
  public String getName() {
    return name; 
  }
  public int[] getOpenNotes() {
    return openNotes; 
  }
  
  public String[] getGuitarTuningNames() {
    String[] names = new String[guitarTunings.length];
    for (int i=0; i<guitarTunings.length; i++) 
      names[i] = guitarTunings[i].getName();
    return names;
    
  }
  
  public static GuitarTuning getGuitarTuning(String name) {
    for (int i=0; i<guitarTunings.length; i++) 
      if (guitarTunings[i].getName().equals(name))
        return guitarTunings[i];
    // todo issue warning
    return null;
  }
  
  public static GuitarTuning guitarTunings[] =  {
    //                                                   Bass
    new GuitarTuning("Standard",             new int[] { Note.E,   Note.A,   Note.D,   Note.G,   Note.B,   Note.E            }),
    new GuitarTuning("Standard (7-String)",  new int[] { Note.B,   Note.E,   Note.A,   Note.D,   Note.G,   Note.B,   Note.E  }),
    new GuitarTuning("Dropped D",            new int[] { Note.D,   Note.A,   Note.D,   Note.G,   Note.B,   Note.E            }),
    new GuitarTuning("E-Flat",               new int[] { Note.DIS, Note.GIS, Note.CIS, Note.FIS, Note.AIS, Note.DIS          }),
    new GuitarTuning("D",                    new int[] { Note.D,   Note.G,   Note.C,   Note.F,   Note.A,   Note.D            }),
    new GuitarTuning("Slide",                new int[] { Note.D,   Note.A,   Note.D,   Note.FIS, Note.A,   Note.D            }),
    new GuitarTuning("Bass",                 new int[] { Note.E,   Note.A,   Note.D,   Note.G                                }),
    new GuitarTuning("Bass (5-String)",      new int[] { Note.B,   Note.E,   Note.A,   Note.D, Note.G                        })
  };
  
}
