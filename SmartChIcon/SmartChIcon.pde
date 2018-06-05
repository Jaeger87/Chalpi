protected static final int lineSize = 2;
protected static final int columnSpaces = 22;
protected static final int rowSpaces = 17;
void setup()
{
  size(800,800);
  background(255,232,61);
  strokeWeight(7);
  fill(220,0,0);
  rect(140, 205, 520, 410, 7);
  fill(40);
  rect(330,235,315,350,7);
  fill(255,177,10);
  rect(155,235,165,350,7);
  
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
