package weka.classifiers.rules;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.classifiers.Evaluation;
import weka.classifiers.lazy.IBk;

import java.util.Random;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.annotation.processing.ProcessingEnvironment;
public class Caller2 {
	public  void Caller(String filename,int partitions,int crossval,int attnum) throws Exception
	{	
		// String message;
		 PreProcessing tr= new PreProcessing();
		 int[] PartitionNumber=tr.Preprocessing(filename,partitions,attnum);    // Number of partitions for each attribute gets returned here.
		 BufferedReader reader = new BufferedReader(new FileReader("TempArff.arff"));
		 Instances data = new Instances(reader);
		 reader.close();
		 Classifier m_classifier = new CAP();
		 // setting class attribute
		 data.setClassIndex(data.numAttributes() - 1);
		 m_classifier.buildClassifier(data);
		 System.out.println(filename+"\tPartitions: "+partitions+"\tAttribute Number: "+attnum);
		 /*CLASSIFIER BUILT SUCCESFULLY*/
		 
		 
		/* BufferedReader reader1 = new BufferedReader(new FileReader("Temp.arff"));
		 Instances data1 = new Instances(reader1);
		
		 double index;
		 for (int i = 0; i < data1.numInstances(); i++) 
	        { 
			 	Instance inst=data1.instance(i);
			 	System.out.print(inst);
			 	index=m_classifier.classifyInstance(inst);
			 	if(index==1.0)
			 		System.out.println('g');
			 	else
			 		System.out.println('b');
	        }
		 reader1.close();*/
		 Evaluation eval = new Evaluation(data);
	//	 eval.crossValidateModel(m_classifier, data, crossval, new Random(1));
		 System.out.println("Result of 10-Fold Cross Validation");
			
		 System.out.println(eval.toSummaryString("\nResults\n======\n", false));
	//	 return message;
	//	return;
		 System.gc();
	}
	

}
