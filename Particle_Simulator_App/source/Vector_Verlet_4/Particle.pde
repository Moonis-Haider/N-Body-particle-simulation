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
  color mycol;
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
  
  void step() {
    check();
    PVector a1 = getField();
    x.x = x.x + v.x*dt + 0.5*a1.x*dt*dt;
    x.y = x.y + v.y*dt + 0.5*a1.y*dt*dt;
    check();
    PVector a2 = getField();
    v.x = v.x + ((a1.x+a2.x)/2)*dt;
    v.y = v.y + ((a1.y+a2.y)/2)*dt;
    nv = v;
    nx = x;
  }
  
  void show() {
    stroke(mycol);
    fill(mycol);
    ellipse(x.x,x.y,2*r,2*r);    
  }
  
  void check() {
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
  
  PVector getField() {
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
  
  void collide() {
    for (Particle p : myWorld.particles) {
      if (p != this) {
        if (PVector.dist(p.x,x) < (p.r+r)) {
          float j = ((2*p.m)/(p.m+m));
          float k = PVector.dot(PVector.sub(v,p.v),PVector.sub(x,p.x))/PVector.sub(x,p.x).magSq();
          nv = PVector.sub(v, PVector.mult(PVector.sub(x,p.x), j*k)); 
          PVector r1 = PVector.sub(x,p.x).setMag(r);
          PVector r2 = PVector.sub(x,p.x).setMag(p.r+1);
          nx = PVector.add(x,(PVector.mult(PVector.sub(PVector.add(r1, r2), PVector.sub(x,p.x)),(0.51*(p.m/(p.m+m))))));
        }
      }
    }
  }
}
