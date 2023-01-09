package src;

import java.awt.*;

public class Component{

  public String type;
  public int xPos;
  public int yPos;
  public Image image;

  public Component(String init_type, int init_x, int init_y){
    type = init_type;
    xPos = init_x;
    yPos = init_y;
    image = Toolkit.getDefaultToolkit().getImage("src/assets/gate_" + init_type + ".png");
  }
}
