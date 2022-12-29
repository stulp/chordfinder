# ChordFinder

## History



# Manual

## Introduction

The ChordFinder applet is intended to help find the names of chords
played on a guitar, bass or piano, as well as alternate positions for
this chord. "Playing" chords is done by clicking them in a
visualization of the instrument. There is a seperate section on [finding
chords](#finding-chords).

## Interface Manual

The ChordFinder applet should be displayed more or less as below. Its
three main components are:

- The [Instrument Panel](#Instrument-Panel), which shows an instrument
    on which notes are displayed.
- [Current Chord Panel](#Current-Chord-Panel). This panel displays the
    current chord. The name of this chord is in its border, and a
    representation of this chord below.
- [Select Chord Panel](#Select-Chord-Panel). This panel allows you to
    choose the current chord. It also lists the chords currently played
    in the instrument.

![ChordFinder applet overview.](images/chordfinderapplet-annotated.png)

## Instrument Panel

This panel allows you to "play" notes on an instrument (in green), and
visualize notes from the chord in the Chord Panel (in blue). It either
displays the fretboard of a guitar, or the keyboard of a piano.

It is important to understand the difference between green and blue
notes (pun not intended). Green notes are manually specified through
clicking in the [Instrument Panel](#Instrument-Panel), for instance on the
guitar's fretboard, or the piano's keyboard. These notes are
"played". Blue notes are not actually played, but are part of the
current chord displayed in the [Current Chord Panel](#Current-Chord-Panel).

### Guitar Panel

Strings can be strummed by clicking at a fret. The 0th fret is the nut.
The number of frets and the tuning can be chosen at the top of this
panel. Not strumming a string can be achieved by clicking in the tabs
notation to the right of the neck, where right handed guitar players
would normally strum. Pressing clear removes any notes played.

![Guitar Panel](images/guitarpanel.png)


### Piano Panel

Essentially as the guitar panel, but then, well, a piano. The number of
octaves displayed can be varied.

![Piano Panel](images/pianopanel.png)


## Current Chord Panel

The name of the currently selected chord is displayed in this panel's
border. In the panel itself, a representation of the chord is shown in
two rows of circles. The name of the intervals (1,3,b7,\...) are listed
above, and the corresponding note (c,c3,d\#\...) below.

Green circles mean that this note is played in the chord found in the
[Instrument Panel](#Instrument-Panel), and blue means it is not. Red
indicates that the note is the bass note (the note with the lowest
frequency) being played.

In the example below, the chord Esus4 is displayed. The
"e","a","b" notes of this chord are played in the instrument
panel, but the "b" is not. The bass note is "a". This corresponds to
the note being played in the Instrument Panels abov.e

![chordpanel](images/chordpanel.png)

The "Show chord in instrument" determines whether or not the current
chord is displayed in the [Instrument Panel](#Instrument-Panel).


## Select Chord Panel

In this panel, you can choose the current chord. It can be chosen from a
list of all chords, or a list of chords that match the notes played in
the [Instrument Panel](#Instrument-Panel).


### All Chords List

![allchords](images/allchords.png)

This panel lists all possible root notes (c..b) and most common chords.


### Found Chords List

![foundrootisplayed](images/foundrootisplayed.png)

This tab lists the chords that match notes currently played in the
[Instrument Panel](#Instrument-Panel). How these chords are found, and
what the checkboxes below this list mean is explained in the next
section on the page on [finding chords](#finding-chords).


### Filtering Chords

This section will probably not make much sense if you have not yet read
the page on [finding chords](#finding-chords), especially the section
on [filtering chords](#filtering-found-chords).

Which chords are listed can be controlled with the check boxes below the
list.

1. All chords whose notes are a superset of the notes played are listed.

![foundall](images/foundall.png)

2. As 1), but the root note of the chord must be played.

![foundrootisplayed](images/foundrootisplayed.png)

3. As 2), but all notes of the chord must be played.

![foundallareplayed](images/foundallareplayed.png)

4. As 2), and the root note must be the bass note.

![foundrootisbass](images/foundrootisbass.png)


# Finding Chords

To determine which chord belongs to a sequence of notes, we need to
know:

1.  All known chords
2.  How to match a set of intervals to a known chord
3.  How to compute a set of intervals given a set of notes and a root note
4.  How to match a set of notes and a root note to a known chord

With these knowledge many chords will fit a sequence of notes.
Therefore, I define some rules with which unlikely candidates can be
excluded.

## All known chords.

To find the chord played in the Instrument Panel, I first defined a set
chords, along with the intervals they contain, as follows:

```
  Name : Intervals       Name : Intervals       Name : Intervals          Name : Intervals  
     1 :  1                 7 :  1  3  5 b7        9 :  1  3  5 b7  9       11 :  1  3  5 b7  9  11     
     5 :  1  5             m7 :  1 b3  5 b7       m9 :  1 b3  5 b7  9      m11 :  1 b3  5 b7  9  11
 major :  1  3  5        maj7 :  1  3  5  7     maj9 :  1  3  5  7  9    maj11 :  1  3  5  7  9  11
     m :  1 b3  5           6 :  1  3  5  6    9sus4 :  1  4  5 b7  9      11+ :  1  3  5 b7  9 #11
   dim :  1 b3 b5          m6 :  1 b3  5  6      6*9 :  1  3  5  6  9     m11+ :  1 b3  5 b7  9 #11
    +5 :  1  3 #5       7sus2 :  1  2  5 b7     m6*9 :  1 b3  5  6  9       13 :  1  3  5 b7  9  11  13
   m+5 :  1 b3 #5       7sus4 :  1  4  5 b7      7-9 :  1  3  5 b7 b9      m13 :  1 b3  5 b7  9  11  13
  sus2 :  1  2  5         7-5 :  1  3 b5 b7     m7-9 :  1 b3  5 b7 b9 
  sus4 :  1  4  5        m7-5 :  1 b3 b5 b7     7-10 :  1  3  5 b7 b10
                          7+5 :  1  3 #5 b7      9+5 :  1 b7 b9       
                         m7+5 :  1 b3 #5 b7     m9+5 :  1 b7  9       
                                               7+5-9 :  1  3 #5 b7 b9 
                                              m7+5-9 :  1 b3 #5 b7 b9 
```

It is easier to store these intervals as semitone distances to the root
note. For instance, the semitone distances of the minor chord are
`(1-1, b3-1, 5-1) = (0 3 7)`. The list of known chords is actually stored as follows:

```
    Name : Intervals       Name : Intervals       Name : Intervals          Name : Intervals  
     1 : 0                  7 : 0 4 7 10           9 : 0 4 7 10 14          11 : 0 4 7 10 14 17               
     5 : 0 7               m7 : 0 3 7 10          m9 : 0 3 7 10 14         m11 : 0 3 7 10 14 17                
    major : 0 4 7           maj7 : 0 4 7 11        maj9 : 0 4 7 11 14       maj11 : 0 4 7 11 14 17                
     m : 0 3 7              6 : 0 4 7 9        9sus4 : 0 5 7 10 14         11+ : 0 4 7 10 14 18                
    dim : 0 3 6             m6 : 0 3 7 9          6*9 : 0 4 7 9 14         m11+ : 0 3 7 10 14 18                
    +5 : 0 4 8          7sus2 : 0 2 7 10        m6*9 : 0 3 7 9 14           13 : 0 4 7 10 14 17 21             
    m+5 : 0 3 8          7sus4 : 0 5 7 10         7-9 : 0 4 7 10 13         m13 : 0 3 7 10 14 17 21             
    sus2 : 0 2 7            7-5 : 0 4 6 10        m7-9 : 0 3 7 10 13                                             
    sus4 : 0 5 7           m7-5 : 0 3 6 10        7-10 : 0 4 7 10 15                                             
                          7+5 : 0 4 8 10         9+5 : 0 10 13                                             
                         m7+5 : 0 3 8 10        m9+5 : 0 10 14                                             
                                               7+5-9 : 0 4 8 10 13                                       
                                              m7+5-9 : 0 3 8 10 13
```

## Matching intervals to a known chord.

Given a set of intervals (for instance `(0 5 10) = (1 4 b7)`) it is now
possible to compute which chord this might be. I simply compare these
intervals to those in the table of known chords. If each given interval
is also in the list of intervals of a known chord, it might be this
chord. Given `(0 5 10)`, the possible chords are therefore
`7sus4 : 0 5 7 10` and `9sus4 : 0 5 7 10 14`. Although both chords
contain all the given intervals, it doesn't make much sense to choose
9sus4. The "best chord" for a given set of intervals is defined as the
possible chord with the lowest index in the table above.

## Computing intervals given notes and a root note.

Suppose we have the notes `(e,a,d)` and want to know the intervals,
given the root note `e`. This is done my measuring the distance, in
semitones, between each note and the root note. For this example, this
yields `(e-e,a-e,d-e) = (0 4 10)`. As we saw, the best chord for this
set of intervals is 7sus4. Because the root note is `e`, we therfore
know it is a E7sus4 chord.

## Matching notes and a root note to a known chord.

We now know how to match intervals to a known chord, and also how to
compute intervals given a set of notes and a root note. Therefore, we
can also match a set of notes and a root note to a known chord. Suppose
we have the notes `(a# c d f d# g)` and specify that the root note is
`c`. First we compute the intervals given the root note
`(a#-c c-c d-c f-c d#-c g-c) = (10 0 2 5 3 7)`. Unfortunately, there is
no known chord with the intervals `( 0 2 3 5 7 10)`. The reason is that
when computing the intervals of a set of notes, the interval can never
become larger than 11 semitones. For example
`a#-c=10, b-c=11, c-c=0, c#-c=1`. Therefore the intervals of the known
chords should also be confined to the range \[0-11\]. For instance the
chord `m11 : 0 3 7 10 14 17` is converted to `m11 : 0 3 7 10 2 5`. This
*does* correspong to the intervals `(10 0 2 5 3 7)`. Therefore, the
notes `(a# c d f d# g)` with root note `c` correspond to a `Cm11` chord.

## Matching notes with unknown root note to known chords.

What if the root note is unknown? The procedure is to simply try each
note c..b as a root note, compute the intervals, and match the best
chord. For the notes `(a d e)`.

```
Root note : Intervals =  Known chord (intervals)         CAI  RNP  ANP  R=B
            a  d  e                                   
      c  :  9  2  4  =  C6*9     (0 4 7 9! 14=2!)        Y
      c# :  8  1  3  =  C#m7+5-9 (0 3! 8! 10 13=1!)      Y
      d  :  7  0  2  =  Dsus2    (0! 2! 7!)              Y    Y    Y
      d# :  6 11  1  =  X                                
      e  :  5 10  0  =  E7sus4   (0! 5! 7 10!)           Y    Y
      f  :  4  9 11  =  X
      f# :  3  8 10  =  F#m7+5   (0 3! 8! 10!)           Y
      g  :  2  7  9  =  G6*9     (0 4 7! 9! 14=2!)       Y
      g# :  1  6  8  =  X 
      a  :  0  5  7  =  Asus4    (0! 5! 7!)              Y    Y    Y    Y
      a# : 11  4  6  =  X
      b  : 10  3  5  =  Bm11     (0 3! 7 10! 14=2 17=5!) Y
```

An `X` means that now known chord contains all given intervals. So,
given the notes `(a d e)`, the possible chords are C6\*9, C\#m7+5-9,
Dsus2, E7sus4, F\#m7+5, G6\*9, Asus4, Bm11. Their intervals are also
listed. An exclamation mark means this interval is actually played.
[]{#filteringchords}

## Filtering found chords

The list above shows the naivety of the procedure: no mucisian would
call `(a d e)` a Bm11. The root note and fifth aren't even played! It
is simply included, because Bm11 contains the intervals `10 3 5`.
Therefore, several "filters"/"rules" should be applied. These are
liste below. Which chords conform to the rules is depicted in the table
above.

-   Contains all intervals (CAI). This rule says that all the given
    intervals must be contained in the intervals of the known chord.
    This is actually not a filter at all. It is basically the main rule
    for finding chords in the first place.
-   Root note played (RNP). This rule says the root note must be played.
    Applying it already removes most chords. Only Dsus2, E7sus4, and
    Asus4 remain. It can easily be read from the intervals: they must
    contain 0.
-   All notes played (ANP). This rule says all the notes in the chord
    must be played. This does not hold for E7sus4, because the fifth is
    not played. It can easily be read from the intervals, which must
    contain as many notes as the intervals of the known chord.
-   Root note is base note (R=B). This rule says that the lowest note
    (the first in the set) must be the root note. This only holds for
    Asus4. It can easily be read from the intervals: the first interval
    should be 0. This rule is very restrictive: theoretically, this rule
    holds for at most one chord.
