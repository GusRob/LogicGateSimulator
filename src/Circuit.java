package src;

import java.util.ArrayList;

public class Circuit{

  public ArrayList<Component> components = new ArrayList<Component>();
  public Circuit(){

  }

  public Component addNewGate(String gate, int init_x, int init_y){
    Component ref = new Component(gate, init_x, init_y);
    components.add(ref);
    return ref;
  }
}
