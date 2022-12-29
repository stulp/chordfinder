import java.awt.Color;

public class ColorScheme {
  public static Color playedColor = new Color(100,255,100);
  public static Color ghostColor = new Color(150,150,255);
  public static Color bassColor = new Color(200,50,50);

  public static Color playedColorBlackKey = new Color(
    (int)(0.7*playedColor.getRed()),
    (int)(0.7*playedColor.getGreen()),
    (int)(0.7*playedColor.getBlue())
  );

  public static Color ghostColorBlackKey = new Color(
    (int)(0.7*ghostColor.getRed()),
    (int)(0.7*ghostColor.getGreen()),
    (int)(0.7*ghostColor.getBlue())
  );
  
  public static Color fretBoardColor = new Color(184,63,10);
  public static Color strummedStringColor = new Color(200,200,200);
  public static Color mutedStringColor = new Color(220,90,30);
  public static Color markerColor = new Color(243,235,187);
  public static Color nutColor = markerColor;
  public static Color fretColor = new Color(220,220,220);

}
