protected static final int lineSize = 2;
protected static final int columnSpaces = 22;
protected static final int rowSpaces = 17;
int XBoard = 140;
int YBoard = 190;

void setup()
{
  size(800,800);
  background(255,232,61);
  strokeWeight(7);
  fill(175,0,0);
  rect(XBoard, YBoard, 520, 460, 7);
  fill(40);
  rect(XBoard + 190 ,YBoard + 30 ,315,350,7);
  fill(255,177,10);
  rect(XBoard + 15,YBoard + 30,165,350,7);
  fill(70);
  rect(XBoard + 15, YBoard + 395,84,55,7);
  fill(48);
  rect(XBoard + 31,YBoard + 409,49,29,7);
  strokeWeight(4);
  line(XBoard + 40,YBoard + 423,XBoard + 71,YBoard + 423);
  
  //led
  fill(0,255,0);
  noStroke();
  ellipse(XBoard + 89, YBoard + 406, 8, 8);
  fill(0,255,0,70);
  ellipse(XBoard + 89, YBoard + 406, 12, 12);
  stroke(0);
  
  //watch
  strokeWeight(7);
  fill(30);
  rect(XBoard + 110,YBoard + 409,22,29,7);
  noFill();
  stroke(#FFEB98);
  strokeWeight(2);
  ellipse(XBoard + 121, YBoard + 424, 21, 21);
  strokeWeight(4);
  line(XBoard + 120,YBoard + 424,XBoard + 120,YBoard + 420);
  strokeWeight(2);
  line(XBoard + 120,YBoard + 424,XBoard + 126,YBoard + 418);


 
  //Botton
  strokeWeight(5.8);
  fill(255,0,0);
  rect(XBoard + 146,YBoard + 407,22,29,7);
  noStroke();
  fill(255,0,0, 75);
  rect(XBoard + 146,YBoard + 408,23,29,7);
  stroke(30);
  int counter = 0;
  
  for(int columnCounter = 0; columnCounter < 16; columnCounter++)
  {
    for(int rowCounter= 0; rowCounter < 9; rowCounter++)
    {
      if (counter % 2 == 0)
        cross(XBoard + 27 + rowSpaces * rowCounter, YBoard + 40 + columnSpaces * columnCounter);
       else
         dX(XBoard + 27 + rowSpaces * rowCounter, YBoard + 40 + columnSpaces * columnCounter);
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