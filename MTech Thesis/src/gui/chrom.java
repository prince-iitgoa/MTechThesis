package gui;

import java.io.IOException;
import java.text.NumberFormat.Field;

public class chrom{
public static void main(String args[]) throws IOException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
	
	for(int i=0;i<10;i++)
	{
	ProcessBuilder proc=new ProcessBuilder("cmd", "/C start" + " " + "https://www.youtube.com/watch?v=xNn8QmcXEuA");
    proc.start();
	}
    //proc.
  //  pb.wait(1000);
  //  java.lang.reflect.Field f = proc.getClass().getDeclaredField("pid");
    /*f.setAccessible(true);
    int pid = (int) f.get(proc);
    System.out.println(pid);*/
	
	//   System.Diagnostics.Process.Start("cmd", "/C start" + " " + "www.google.com"); 
	}
}