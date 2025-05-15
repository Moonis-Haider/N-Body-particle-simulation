import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import controlP5.*; 
import java.util.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Vector_Verlet_4 extends PApplet {




World world;
int rate = 3;
float dt = 0.05f;
float c = 3;
float minnum = 100;
float maxnum = 200;
int rownum = 20;
int colnum = 20;
float chance = 0.5f;
boolean exists = false;
SimType force = SimType.NOFORCE;
SimType distribution = SimType.UNIFORMDISTRIBUTION;

ControlP5 cp5;

public void setup() {
  
  cp5 = new ControlP5(this);
  cp5.addScrollableList("Force")
     .setPosition(1200,15)
     .setSize(150,70)
     .setBarHeight(20)
     .setItemHeight(20)
     .addItems(Arrays.asList("None", "Gravity", "Electric"));
  cp5.addScrollableList("Distribution")
     .setPosition(1200,85)
     .setSize(150,70)
     .setBarHeight(20)
     .setItemHeight(20)
     .addItems(Arrays.asList("Random", "Uniform"));
  cp5.addSlider("Strength_of_Force")
     .setPosition(1200,155)
     .setSize(150,20)
     .setRange(0,5)
     .setValue(1)
     .setColorLabel(0);
  cp5.getController("Strength_of_Force").getCaptionLabel().align(ControlP5.LEFT, ControlP5.BOTTOM_OUTSIDE).setPaddingX(0);
  cp5.addRange("Particle_Size")
     .setBroadcast(false)
     .setPosition(1200,195)
     .setSize(150,20)
     .setHandleSize(5)
     .setRange(0,1000)
     .setRangeValues(100,200)
     .setBroadcast(true)
     .setColorLabel(0);
  cp5.getController("Particle_Size").getCaptionLabel().align(ControlP5.LEFT, ControlP5.BOTTOM_OUTSIDE).setPaddingX(0);
  cp5.addSlider("Number_of_Rows")
     .setPosition(1200,235)
     .setSize(150,20)
     .setRange(0,40)
     .setValue(20)
     .setColorLabel(0);
  cp5.getController("Number_of_Rows").getCaptionLabel().align(ControlP5.LEFT, ControlP5.BOTTOM_OUTSIDE).setPaddingX(0);
  cp5.addSlider("Number_of_Columns")
     .setPosition(1200,275)
     .setSize(150,20)
     .setRange(0,40)
     .setValue(20)
     .setColorLabel(0);
  cp5.getController("Number_of_Columns").getCaptionLabel().align(ControlP5.LEFT, ControlP5.BOTTOM_OUTSIDE).setPaddingX(0);
  cp5.addSlider("Chance_of_Negative_Charge")
     .setPosition(1200,315)
     .setSize(150,20)
     .setRange(0.5f,1)
     .setValue(0.5f)
     .setColorLabel(0);
  cp5.getController("Chance_of_Negative_Charge").getCaptionLabel().align(ControlP5.LEFT, ControlP5.BOTTOM_OUTSIDE).setPaddingX(0);
  cp5.addBang("Create_World")
     .setPosition(1200,355)
     .setSize(150,20)
     .setTriggerEvent(Bang.RELEASE)
     .setLabel("Create_World")
     .setColorLabel(0);
  cp5.getController("Create_World").getCaptionLabel().align(ControlP5.LEFT, ControlP5.BOTTOM_OUTSIDE).setPaddingX(0);
}

public void draw() {
  if (exists) {
    world.run(rate);
  }
}

public void mousePressed() {
  if (exists) {
    world.pressHandler();
  }
}

public void mouseWheel(MouseEvent event) {
  if (exists) {
    world.wheelHandler(event);
  }
}

public void keyPressed() {
  if (exists) {
    world.keyHandler();
  }
}

public void Create_World() {
  world = new World(chance, rownum, colnum, minnum, maxnum, dt, c, force, distribution);
  exists = true;
}

public void Force(int n) {
  if (n == 0) {
    force = SimType.NOFORCE;
  } else if (n == 1) {
    force = SimType.GRAVITYFORCE;
  } else {
    force = SimType.ELECTRICFORCE;
  }
}

public void Distribution(int n) {
  if (n == 0) {
    distribution = SimType.RANDOMDISTRIBUTION;
  } else {
    distribution = SimType.UNIFORMDISTRIBUTION;
  }
}

public void Strength_of_Force(float strength) {
  c = strength;
}

public void controlEvent(ControlEvent theControlEvent) {
  if(theControlEvent.isFrom("Particle_Size")) {
    minnum = PApplet.parseInt(theControlEvent.getController().getArrayValue(0));
    maxnum = PApplet.parseInt(theControlEvent.getController().getArrayValue(1));
  }
}

public void Number_of_Rows(int rows) {
  rownum = rows;
}

public void Number_of_Columns(int cols) {
  colnum = cols;
}

public void Chance_of_Negative_Charge(float chance_) {
  chance = chance_;
}
class Particle {
  World myWorld;
  PVector x;
  PVector nx;
  PVector v;
  PVector nv;
  PVector a;
  float m;
  float r;
  float q;
  float dt;
  int mycol;
  boolean wall = false;
  
  Particle(PVector xi, PVector vi, float m_, float q_, World myWorld_) {
    myWorld = myWorld_;
    x = xi;
    v = vi;
    nv = v;
    nx = x;
    m = m_;
    q = q_;
    r = sqrt(m/PI);
    dt = myWorld.dt;
    if (myWorld.distribution == SimType.RANDOMDISTRIBUTION && myWorld.force != SimType.ELECTRICFORCE) {
      mycol = color(random(200),random(200),random(200));
    } else if (myWorld.force != SimType.ELECTRICFORCE) {
      mycol = myWorld.col1;
    } else {
      if (q == 1) {
        mycol = myWorld.col1;
      } else {
        mycol = myWorld.col2;
      }
    }
  }
  
  public void step() {
    check();
    PVector a1 = getField();
    x.x = x.x + v.x*dt + 0.5f*a1.x*dt*dt;
    x.y = x.y + v.y*dt + 0.5f*a1.y*dt*dt;
    check();
    PVector a2 = getField();
    v.x = v.x + ((a1.x+a2.x)/2)*dt;
    v.y = v.y + ((a1.y+a2.y)/2)*dt;
    nv = v;
    nx = x;
  }
  
  public void show() {
    stroke(mycol);
    fill(mycol);
    ellipse(x.x,x.y,2*r,2*r);    
  }
  
  public void check() {
    if ((x.x > width-r || x.x < r) && !wall) {
      v.x = -v.x;
      wall = true;
      if (x.x > width-r) {x.x = width-r;}
      else {x.x = r;}
    }
    else if ((x.y > height-r || x.y < r) && !wall) {
      v.y = -v.y;
      wall = true;
      if (x.y > height-r) {x.y = height-r;}
      else {x.y = r;}
    }
    else {wall = false;}
    if (nv != v) {v = nv;}
    if (nx != x) {x = nx;}
  }
  
  public PVector getField() {
    if (myWorld.force == SimType.GRAVITYFORCE) {
      PVector s = new PVector(0,0);
      for (Particle p : myWorld.particles) {
        if (p != this) {
          s.add(PVector.sub(p.x,x).setMag(p.m*myWorld.c*(1/PVector.sub(p.x,x).magSq())));  
        }
      }
      return s;
    } else if (myWorld.force == SimType.NOFORCE) {
      return new PVector(0,0);
    } else {
      PVector s = new PVector(0,0);
      for (Particle p : myWorld.particles) {
        if (p != this) {
          s.add(PVector.mult(PVector.sub(x,p.x),((2500*myWorld.c*q*p.q)/(m*PVector.sub(x,p.x).magSq()))));  
        }
      }
      return s;
    }
  }
  
  public void collide() {
    for (Particle p : myWorld.particles) {
      if (p != this) {
        if (PVector.dist(p.x,x) < (p.r+r)) {
          float j = ((2*p.m)/(p.m+m));
          float k = PVector.dot(PVector.sub(v,p.v),PVector.sub(x,p.x))/PVector.sub(x,p.x).magSq();
          nv = PVector.sub(v, PVector.mult(PVector.sub(x,p.x), j*k)); 
          PVector r1 = PVector.sub(x,p.x).setMag(r);
          PVector r2 = PVector.sub(x,p.x).setMag(p.r+1);
          nx = PVector.add(x,(PVector.mult(PVector.sub(PVector.add(r1, r2), PVector.sub(x,p.x)),(0.51f*(p.m/(p.m+m))))));
        }
      }
    }
  }
}
enum SimType {
  NOFORCE, GRAVITYFORCE, ELECTRICFORCE, RANDOMDISTRIBUTION, UNIFORMDISTRIBUTION
}
class World {
  ArrayList<Particle> particles;
  float dt;
  float c;
  int col1 = 0;
  int col2 = 0;
  SimType force;
  SimType distribution;
  boolean positive = true;
  PVector store;
  float min;
  float max;
  float choose;
  float count = 0;
  float chance;
  
  World(float chance_, int rows_, int cols_, float min_, float max_, float dt_, float c_, SimType force_, SimType distribution_) {
    int rows = rows_;
    int cols = cols_;
    chance = chance_;
    min = min_;
    max = max_;
    choose = sqrt((min+max)/(2*PI));
    force = force_;
    distribution = distribution_;
    particles = new ArrayList<Particle>();
    dt = dt_;
    col1 = color(random(200),random(200),random(200));
    col2 = color(random(200),random(200),random(200));
    if (distribution == SimType.RANDOMDISTRIBUTION) {
      for (int i = 0; i < (rows*cols); i++) {
        float q = 0;
        if (force == SimType.ELECTRICFORCE) {
          if (random(1) > chance) {q=1;}
          else {q=-1;}
        }
        Particle p = new Particle(new PVector(random(0,width),random(0,height)), new PVector(0,0), random(min,max), q, this);
        if (!gapCheck(p)) {
          particles.add(p);
        } else {
          i--;
        }
      }
    } else {
      for (int i = 0; i < cols; i++) {
        for (int j = 0; j < rows; j++) {
          float q = 0;
          if (force == SimType.ELECTRICFORCE) {
           if (random(1) > chance) {q=1;}
            else {q=-1;}
          }
          Particle p = new Particle(new PVector((width/cols)*(i+0.5f),(height/rows)*(j+0.5f)), new PVector(0,0), (min+max)/2, q, this);
          if (!gapCheck(p)) {
            particles.add(p);
          }
        }
      }
    }
    c = c_;
  }
  
  public void collide() {
    for (Particle p : particles) {
      p.collide();
    }
  }
  
  public void step() {
    for (Particle p : particles) {
      p.step();
    }
  }
  
  public void show() {
    for (Particle p : particles) {
      p.show();
    }
  }
  
  public boolean gapCheck(Particle p1) {
    boolean r = false;
    for (Particle p2 : particles) {
      if (PVector.dist(p1.x,p2.x) <= p1.r+p2.r) {
        r = true;
      }
    }
    p1.check();
    if (p1.wall) {
      r = true;
    }
    return r;
  }
  
  public void run(int rate) {
    for (int i = 0; i < rate; i++){
      background(255);
      world.collide();
      world.step();
      world.show();
    }
    noFill();
    stroke(0);
    if (force == SimType.ELECTRICFORCE) {
      if (positive) {
        stroke(col1);
      } else {
        stroke(col2);
      }
    }
    ellipse(mouseX,mouseY,choose*2,choose*2);
    if (count != 0) {
      line(store.x,store.y,mouseX,mouseY);
    }
  }
  
  public void delete(float x, float y) {
    Particle marked = null;
    for (Particle p : particles) {
      if (PVector.dist(p.x, new PVector(x,y)) < p.r) {
        marked = p;
      }
    }
    particles.remove(marked);
  }
  
  public void pressHandler() {
    if (mouseButton == LEFT && !(mouseX > 1200 && mouseX < 1350 && mouseY > 15 && mouseY < 385)) {
      if (count == 0) {
        store = new PVector(mouseX,mouseY);
        count++;
      } else {
        PVector v = PVector.mult(PVector.sub(new PVector(mouseX,mouseY),store), 0.5f);
        float q = 0;
        if (force == SimType.ELECTRICFORCE) {
          if (positive) {q=1;}
          else {q=-1;}
        }
        particles.add(new Particle(new PVector(store.x,store.y), v, PI*choose*choose, q, world));
        count--;
      }
    } else if (mouseButton == RIGHT) {
      delete(mouseX,mouseY);
    }
  }
  
  public void wheelHandler(MouseEvent event) {
    float e = event.getCount();
    choose += -2*e;
    if (choose < 0) {choose = 0;}
  }
  
  public void keyHandler() {
    if (key == 's' || key == 'S') {
      positive = !positive;
    }
  }
}
  public void settings() {  fullScreen(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Vector_Verlet_4" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
