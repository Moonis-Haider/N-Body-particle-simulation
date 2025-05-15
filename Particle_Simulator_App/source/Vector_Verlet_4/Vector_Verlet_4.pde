import controlP5.*;
import java.util.*;

World world;
int rate = 3;
float dt = 0.05;
float c = 3;
float minnum = 100;
float maxnum = 200;
int rownum = 20;
int colnum = 20;
float chance = 0.5;
boolean exists = false;
SimType force = SimType.NOFORCE;
SimType distribution = SimType.UNIFORMDISTRIBUTION;

ControlP5 cp5;

void setup() {
  fullScreen();
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
     .setRange(0.5,1)
     .setValue(0.5)
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

void draw() {
  if (exists) {
    world.run(rate);
  }
}

void mousePressed() {
  if (exists) {
    world.pressHandler();
  }
}

void mouseWheel(MouseEvent event) {
  if (exists) {
    world.wheelHandler(event);
  }
}

void keyPressed() {
  if (exists) {
    world.keyHandler();
  }
}

void Create_World() {
  world = new World(chance, rownum, colnum, minnum, maxnum, dt, c, force, distribution);
  exists = true;
}

void Force(int n) {
  if (n == 0) {
    force = SimType.NOFORCE;
  } else if (n == 1) {
    force = SimType.GRAVITYFORCE;
  } else {
    force = SimType.ELECTRICFORCE;
  }
}

void Distribution(int n) {
  if (n == 0) {
    distribution = SimType.RANDOMDISTRIBUTION;
  } else {
    distribution = SimType.UNIFORMDISTRIBUTION;
  }
}

void Strength_of_Force(float strength) {
  c = strength;
}

void controlEvent(ControlEvent theControlEvent) {
  if(theControlEvent.isFrom("Particle_Size")) {
    minnum = int(theControlEvent.getController().getArrayValue(0));
    maxnum = int(theControlEvent.getController().getArrayValue(1));
  }
}

void Number_of_Rows(int rows) {
  rownum = rows;
}

void Number_of_Columns(int cols) {
  colnum = cols;
}

void Chance_of_Negative_Charge(float chance_) {
  chance = chance_;
}
