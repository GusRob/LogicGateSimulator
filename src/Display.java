package src;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


//this class handles drawing to the window and user interaction events
public class Display extends JPanel implements MouseWheelListener, MouseMotionListener, MouseListener, ActionListener{
	final int VP_WIDTH;
	final int VP_HEIGHT;
	final int PAGE_SIZE;

	public double zoom = 1;
	public int pageX;
	public int pageY;
	private Timer timer = new Timer(10, this);

	private int pageGrabX = -1;
	private int pageGrabY = -1;
	private boolean isDragging = false;

	private double simSpeed = 0;
	private boolean isSliding = false;

	public Circuit circuit = new Circuit();
	public Component componentHeld = null;

	String[] gateNames = {"BUF", "NOT", "AND", "NAND", "OR", "NOR", "XOR", "XNOR"};

	public Display(int init_width, int init_height){
		addMouseListener(this);
		addMouseWheelListener(this);
		addMouseMotionListener(this);
		VP_WIDTH = init_width;
		VP_HEIGHT = init_height;
		PAGE_SIZE = Math.max(VP_WIDTH, VP_HEIGHT)*2;
		pageX = (int)(PAGE_SIZE/2);
		pageY = pageX;
		repaint();
		timer.start();

		circuit.addNewGate(gateNames[0], PAGE_SIZE/4, PAGE_SIZE/4);
	}

	//////////
	//EVENTS//
	//////////

	@Override
	public void mouseMoved(MouseEvent e){}
	@Override
	public void mouseDragged(MouseEvent e){
		int mouse_x = e.getX();
		int mouse_y = e.getY();
		if(isDragging){
			if(componentHeld == null){
				pageX = pageGrabX - (int)(mouse_x/zoom);
				pageY = pageGrabY - (int)(mouse_y/zoom);
			} else {
				componentHeld.xPos = pageX + (int)((mouse_x-PAGE_SIZE/4)/(zoom)) - (int)(PAGE_SIZE/40);
				componentHeld.yPos = pageY + (int)((mouse_y-PAGE_SIZE/4)/(zoom)) - (int)(PAGE_SIZE/60);
			}
		}
		if(isSliding){
			int unit = (int)(VP_WIDTH/100);
			int min = VP_WIDTH-28*unit;
			double temp = (double)(mouse_x - min)/(double)(20*unit);
			if(temp < 0){temp = 0;}
			if(temp > 1){temp = 1;}
			simSpeed = temp;
		}
	}
	@Override
	public void mouseExited(MouseEvent e){}
	@Override
	public void mouseEntered(MouseEvent e){}
	@Override
	public void mouseClicked(MouseEvent e){System.out.println(pageX + ", " + pageY);}
	@Override
	public void mouseReleased(MouseEvent e){
		int mouse_x = e.getX();
		int mouse_y = e.getY();
		if(isDragging){
			if(componentHeld == null){
				pageX = pageGrabX - (int)(mouse_x/zoom);
				pageY = pageGrabY - (int)(mouse_y/zoom);
				if(pageX < 0){pageX = 0;}
				if(pageX > PAGE_SIZE){pageX = PAGE_SIZE;}
				if(pageY < 0){pageY = 0;}
				if(pageY > PAGE_SIZE){pageY = PAGE_SIZE;}
			} else {
				componentHeld.xPos = pageX + (int)((mouse_x-PAGE_SIZE/4)/(zoom)) - (int)(PAGE_SIZE/40);
				componentHeld.yPos = pageY + (int)((mouse_y-PAGE_SIZE/4)/(zoom)) - (int)(PAGE_SIZE/60);
				componentHeld = null;
			}
			isDragging = false;
		}
		if(isSliding){
			isSliding = false;
		}
	}
	@Override
	public void mouseWheelMoved(MouseWheelEvent e){
		double notches = (double)e.getWheelRotation();
		double temp = (1-(notches/100)) * zoom;
		if(temp > 2){temp=2;}
		if(temp <0.5){temp=0.5;}
		zoom = temp;
	}
	@Override
	public void mousePressed(MouseEvent e){
		int mouse_x = e.getX();
		int mouse_y = e.getY();
		int unit = (int)(VP_WIDTH/100);
		if(mouse_x > (VP_WIDTH-30*unit) && mouse_x < (VP_WIDTH-5*unit) && mouse_y > (VP_HEIGHT-20*unit) && mouse_y < (VP_HEIGHT-10*unit)){
			if(!isSliding){
				if(mouse_x > (VP_WIDTH-29*unit + (int)(20*unit*simSpeed)) && mouse_x < (VP_WIDTH-27*unit + (int)(20*unit*simSpeed)) && mouse_y > (VP_HEIGHT-14*unit) && mouse_y < (VP_HEIGHT-11*unit)){
					pageGrabX = mouse_x;
					pageGrabY = mouse_y;
					isSliding = true;
				}
			}
		} else if(mouse_x < (VP_WIDTH/7) && mouse_y > (VP_HEIGHT/10) && mouse_y < (VP_HEIGHT/10 + 7*VP_HEIGHT/10)) {
			double i = 1.5;
			for(String name : gateNames){
				if((mouse_x >= VP_WIDTH/35) && (mouse_x <= (VP_WIDTH/35 + VP_WIDTH/10)) && (mouse_y >= (int)((i)*(VP_HEIGHT/12))) && (mouse_y <= (int)((i)*(VP_HEIGHT/12)) + (VP_WIDTH/15)) ){
					if(!isDragging){
						pageGrabX = pageX + (int)(mouse_x/zoom);
						pageGrabY = pageY + (int)(mouse_y/zoom);
						isDragging = true;
						int xTemp = pageX + (int)((mouse_x-PAGE_SIZE/4)/(zoom)) - (int)(PAGE_SIZE/40);
						int yTemp = pageY + (int)((mouse_y-PAGE_SIZE/4)/(zoom)) - (int)(PAGE_SIZE/60);
						componentHeld = circuit.addNewGate(gateNames[(int)(i-1.5)], xTemp, yTemp);
					}
				}
				i+=1;
			}

		} else {
			if(!isDragging){
				pageGrabX = pageX + (int)(mouse_x/zoom);
				pageGrabY = pageY + (int)(mouse_y/zoom);
				isDragging = true;


				int cx = (int)(VP_WIDTH / 2);
				int cy = (int)(VP_HEIGHT / 2);
				componentHeld = null;
				for(Component gate : circuit.components){
					int xPos = (int)((gate.xPos-pageX)*zoom)+cx;
					int yPos = (int)((gate.yPos-pageY)*zoom)+cy;
					int width = (int)(zoom*PAGE_SIZE/20);
					int height = (int)(zoom*PAGE_SIZE/30);
					if((mouse_x > xPos) && (mouse_x < xPos+width) && (mouse_y > yPos) && (mouse_y < yPos+height)){
						componentHeld = gate;
						break;
					}
				}
			}
		}
	}
	@Override
	public void actionPerformed(ActionEvent e){
		repaint();
	}

	////////////
	//PAINTING//
	////////////

	public void paint(Graphics g){
		g.setColor(new Color(200, 200, 200, 255));
		g.fillRect(0, 0, VP_WIDTH, VP_HEIGHT);
		paintBg(g);
		paintCircuit(g);
		paintSlider(g);
		paintSidebar(g);
	}

	public void paintSidebar(Graphics g){
		g.setColor(new Color(100, 100, 100, 150));
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(10));
		g.drawRoundRect(-50, VP_HEIGHT/10, 50 + VP_WIDTH/7, 7*VP_HEIGHT/10, 50, 50);
		g.setColor(new Color(200, 200, 200, 150));
		g.fillRoundRect(-45, 5+VP_HEIGHT/10, 40 + VP_WIDTH/7, -10+7*VP_HEIGHT/10, 45, 45);
		double i = 0.5;
		for(String name : gateNames){
			Image im = Toolkit.getDefaultToolkit().getImage("src/assets/gate_" + name + ".png");
			g.drawImage(im, VP_WIDTH/35, (int)((++i)*(VP_HEIGHT/12)), VP_WIDTH/10, (VP_WIDTH/15), this);
		}
	}

	public void paintCircuit(Graphics g){
		for(Component gate : circuit.components){
			g.setColor(new Color(0, 0, 0, 255));
			drawImageOnScrollingPage(g, gate.image, gate.xPos, gate.yPos, PAGE_SIZE/20, (PAGE_SIZE/30));
		}
	}

	public void paintSlider(Graphics g){
		int unit = (int)(VP_WIDTH/100);
		g.setColor(new Color(255, 255, 255, 200));
		g.fillRoundRect(VP_WIDTH-30*unit, VP_HEIGHT-20*unit, 25*unit, 10*unit, 2*unit, 2*unit);
		g.setColor(new Color(0, 0, 0, 255));
		g.drawString("Simulation Speed: ", VP_WIDTH-28*unit, VP_HEIGHT-18*unit);
		int sliderX = (int)(20*(double)(unit)*simSpeed);
		int sliderXInv = (int)(20*(double)(unit)*(1-simSpeed));
		g.setColor(new Color(175, 175, 175, 255));
		g.fillRoundRect(VP_WIDTH-28*unit, VP_HEIGHT-13*unit, sliderX, unit, unit, unit);
		g.setColor(new Color(200, 200, 200, 255));
		g.fillRoundRect(VP_WIDTH-28*unit + sliderX, VP_HEIGHT-13*unit, sliderXInv, unit, unit, unit);
		g.setColor(new Color(225, 225, 225, 255));
		g.fillOval(VP_WIDTH-29*unit + sliderX, VP_HEIGHT-14*unit, unit*3, unit*3);
	}

	public void paintBg(Graphics g){
		g.setColor(new Color(175, 175, 175, 255));
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke((int)(zoom*9/1.5) - 2));

		int numLines = 50;
		for(double i = 0; i <= numLines; i++){
			int n = (int)(PAGE_SIZE*i/numLines);
			drawLineOnScrollingPage(g, 0, n, PAGE_SIZE, n);
			drawLineOnScrollingPage(g, n, 0, n, PAGE_SIZE);
		}
	}
	public void drawLineOnScrollingPage(Graphics g, int x1, int y1, int x2, int y2){
		int cx = (int)(VP_WIDTH / 2);
		int cy = (int)(VP_HEIGHT / 2);
		g.drawLine((int)((x1-pageX)*zoom)+cx, (int)((y1-pageY)*zoom)+cy, (int)((x2-pageX)*zoom)+cx, (int)((y2-pageY)*zoom)+cy);
	}
	public void drawImageOnScrollingPage(Graphics g, Image im, int x, int y, int w, int h){
		int cx = (int)(VP_WIDTH / 2);
		int cy = (int)(VP_HEIGHT / 2);
		g.drawImage(im, (int)((x-pageX)*zoom)+cx, (int)((y-pageY)*zoom)+cy, (int)(w*zoom), (int)(h*zoom), this);
	}
}
