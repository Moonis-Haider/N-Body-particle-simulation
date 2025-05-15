class World {
  ArrayList<Particle> particles;
  float dt;
  float c;
  color col1 = 0;
  color col2 = 0;
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
          Particle p = new Particle(new PVector((width/cols)*(i+0.5),(height/rows)*(j+0.5)), new PVector(0,0), (min+max)/2, q, this);
          if (!gapCheck(p)) {
            particles.add(p);
          }
        }
      }
    }
    c = c_;
  }
  
  void collide() {
    for (Particle p : particles) {
      p.collide();
    }
  }
  
  void step() {
    for (Particle p : particles) {
      p.step();
    }
  }
  
  void show() {
    for (Particle p : particles) {
      p.show();
    }
  }
  
  boolean gapCheck(Particle p1) {
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
  
  void run(int rate) {
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
  
  void delete(float x, float y) {
    Particle marked = null;
    for (Particle p : particles) {
      if (PVector.dist(p.x, new PVector(x,y)) < p.r) {
        marked = p;
      }
    }
    particles.remove(marked);
  }
  
  void pressHandler() {
    if (mouseButton == LEFT && !(mouseX > 1200 && mouseX < 1350 && mouseY > 15 && mouseY < 385)) {
      if (count == 0) {
        store = new PVector(mouseX,mouseY);
        count++;
      } else {
        PVector v = PVector.mult(PVector.sub(new PVector(mouseX,mouseY),store), 0.5);
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
  
  void wheelHandler(MouseEvent event) {
    float e = event.getCount();
    choose += -2*e;
    if (choose < 0) {choose = 0;}
  }
  
  void keyHandler() {
    if (key == 's' || key == 'S') {
      positive = !positive;
    }
  }
}
