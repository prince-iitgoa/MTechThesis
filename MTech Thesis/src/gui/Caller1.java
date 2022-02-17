package gui;

import weka.classifiers.rules.Caller2;


public class Caller1 {
	public static void main(String args[]) throws Exception
	{
		String filename="C:\\iris.arff";
		int partitions=4;
		int crossval=10;
		int attnum=4;
		weka.classifiers.rules.Caller2 ts= new Caller2();
		ts.Caller(filename,partitions,crossval,attnum);
	}
}
