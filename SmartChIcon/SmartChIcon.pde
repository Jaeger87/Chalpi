protected static final int lineSize = 2;
protected static final int columnSpaces = 22;
protected static final int rowSpaces = 17;
void setup()
{
  size(800,800);
  background(255,232,61);
  strokeWeight(7);
  fill(175,0,0);
  rect(140, 205, 520, 460, 7);
  fill(40);
  rect(330,235,315,350,7);
  fill(255,177,10);
  rect(155,235,165,350,7);
  fill(70);
  rect(155,600,84,55,7);
  fill(48);
  rect(171,614,49,29,7);
  strokeWeight(4);
  line(183,628,209,628);
  
  //led
  fill(0,255,0);
  noStroke();
  ellipse(229, 611, 8, 8);
  fill(0,255,0,70);
  ellipse(229, 611, 12, 12);
  stroke(0);
  
  strokeWeight(7);
  fill(30);
  rect(250,614,22,29,7);
  noFill();
  stroke(#FFEB98);
  strokeWeight(2);
  ellipse(261, 629, 21, 21);
  strokeWeight(4);
  line(260,629,260,625);
  strokeWeight(2);
  line(260,629,266,623);
  //fill(#EBEDEB);
  //rect(280,615,15,22,7);
  strokeWeight(5.8);
  fill(255,0,0);
  rect(286,612,22,29,7);
  noStroke();
  fill(255,0,0, 75);
  rect(286,613,23,29,7);
  stroke(30);
  int counter = 0;
  
  for(int columnCounter = 0; columnCounter < 16; columnCounter++)
  {
    for(int rowCounter= 0; rowCounter < 9; rowCounter++)
    {
      if (counter % 2 == 0)
        cross(167 + rowSpaces * rowCounter, 245 + columnSpaces * columnCounter);
       else
         dX(167 + rowSpaces * rowCounter, 245 + columnSpaces * columnCounter);
      counter++;
    }
    
  }
 saveFrame("icon.png");
}


void loop()
{
  
}



  public void cross(int x, int y)
  {
    strokeWeight(3);
    line(x - lineSize , y, x + lineSize, y); 
    line(x, y - lineSize, x, y + lineSize); 
    strokeWeight(1);
  }
  
  
  public void dX(int x, int y)
  {
    strokeWeight(3);
    line(x - lineSize , y - lineSize, x + lineSize, y + lineSize); 
    line(x + lineSize, y - lineSize, x - lineSize, y + lineSize); 
    strokeWeight(1);
  }
