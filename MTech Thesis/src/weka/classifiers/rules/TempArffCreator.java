package weka.classifiers.rules;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;


public class TempArffCreator {
/*a second run through the file is done to create a temporary file based on the important attributes   can this be avoided?*/
//receive important attribute names their index. and the partition values for numeric attributes. & names of values for nominal
//convert everything into numeric and save in the temporary arff file created here.
	
	
	public static int[] ArffCreator(String FilePath,ArrayList<String> AttributeNames,ArrayList<String> SortedAttNames,double[] EntropyAtt,int[] flagNumorRealCopy,ArrayList<ArrayList<String>> outer,ArrayList<String> classNames, ArrayList<ArrayList<Float>> attPartitionValues,int AttNumber) throws IOException
		{
			//int AttNumber=10; //No: of  attributes in the reduced data set
			BufferedReader br = null;
			String[] words = null;
			 ArrayList<ArrayList<Float>> attPartitionValuesOrdered= new ArrayList<ArrayList<Float>>();
			br = new BufferedReader(new FileReader(FilePath));
			String CurrentLine;
			int[] EntropyFlag= new int[200];
			while ((CurrentLine = br.readLine()) != null)  //Processing Header
			{
				if(!CurrentLine.isEmpty())
				{
					//System.out.println(sCurrentLine.substring(0,1));
					if(CurrentLine.charAt(0)!='%')
					//if(!sCurrentLine.substring(0,1).equals("%"))   -> also correct
					{
					//	System.out.println(sCurrentLine);
						words= CurrentLine.split(" |\\t");
						//System.out.println(CurrentLine);
						if(words[0].equalsIgnoreCase("@data"))
								break;
					}	
				}
			}
			
			 int numericattcount=0;
			System.out.println("\n__Sorted list of selected attributes__");
			for(int k=0;k<AttNumber;k++)
			{
				numericattcount=0;
				System.out.println(SortedAttNames.get(k)+"\t"+EntropyAtt[k]);
				for(int i=0;i<AttributeNames.size();i++)
				{
					if(SortedAttNames.get(k).equalsIgnoreCase(AttributeNames.get(i)))
					{
						 // ordering partition values based on entropy
						if(flagNumorRealCopy[i]==1)
							attPartitionValuesOrdered.add(attPartitionValues.get(numericattcount));
						
						EntropyFlag[i]=1;
						break;
					}
					if(flagNumorRealCopy[i]==1)
						numericattcount++;
				}
			}
			
		//	System.out.println("Hello");
			/* Now to store only the required attributes
			 * 1.Determine how to select the number of attributes from attributes names sorted based on entropy values.
			 * 2.Find the index of those attributes
			 * 3.Store only the values of attributes at the specified indexes
			 * 4.Do we need to convert the numeric values into their corresponding partition values? - Then pass the partitions also to this function.
			 * 
			 */
			int PartitionNumbers[]= new int[AttributeNames.size()]; 
			int PartitionNumbersEntrop[]= new int[AttNumber]; // To pass the partition numbers in order (numeric as well as nominal)
			Writer wr = new FileWriter("TempArff.arff");
			wr.write("% _____ AUTO-GENERATED TEMPORARY ARFF FILE _____\n\n\n");
			wr.write("@RELATION Temp\n\n");
			
			int nomattcount=0;
			numericattcount=0;
		  	for(int k=0;k<AttributeNames.size();k++)
			{
				if(EntropyFlag[k]==1)
				{
					wr.write("@ATTRIBUTE ");
					wr.write(AttributeNames.get(k));
					wr.write(" NUMERIC\n");
				/*	if(flagNumorRealCopy[k]==1)
					{
						PartitionNumbers[k]=attPartitionValuesOrdered.get(numericattcount).size()-1;
						numericattcount++;
					//	
						
					}
					else
					{
						PartitionNumbers[k]=outer.get(nomattcount).size()-1;
						nomattcount++;
					}

					System.out.println(AttributeNames.get(k)+"\t"+PartitionNumbers[k]);*/
				}
				/*else
				{
					if(flagNumorRealCopy[k]==1)
						numericattcount++;
					else
						nomattcount++;
				}*/
			}
			wr.write("@ATTRIBUTE class {");
			for(int k=0;k<classNames.size();k++)
			{
				wr.write(classNames.get(k));
				if(k!=classNames.size()-1)
					wr.write(",");
			}
			wr.write("}\n");
			wr.write("\n@DATA\n");
		
			/*
			 *  wr.write("123");
				wr.write(new Integer(123).toString());
				wr.write( String.valueOf(123) );
			*/
			//int nomattcount;
			/////////////////////////////////////////////////////////////////////////////////
			///////////////////////////////////////////////////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////////////////
			nomattcount=0;
			numericattcount=0; 
			int index=0;
			System.out.println("\n_______ Its Over ________\n");
				
			for(int i=0;i<AttributeNames.size();i++)   
			{													//18/03/2016
				//numericattcount=0;
				if(EntropyFlag[i]==1)
				{
					if(flagNumorRealCopy[i]==1)
					{		// Convert numeric values to representative elements of corresponding partition.
						//System.out.println(attPartitionValuesOrdered.get(numericattcount)); // sort this list   DONE :)
						PartitionNumbers[i]=attPartitionValues.get(numericattcount).size()-1;
						PartitionNumbersEntrop[index++]=attPartitionValues.get(numericattcount).size()-1;
						numericattcount++;
					}
					else
					{	//Write code for saving clas membrshp of att values into attdegsum (copy abov code)
						//tempattcount=0;
						PartitionNumbers[i]=outer.get(nomattcount).size()-1;
						PartitionNumbersEntrop[index++]=outer.get(nomattcount).size()-1;
						nomattcount++;
					}
				}
				else
				{
					if(flagNumorRealCopy[i]==1)
						numericattcount++;
					else
						nomattcount++;
				}
				System.out.println(AttributeNames.get(i)+"\t"+PartitionNumbers[i]);
			}
			
			for(int i=0;i<PartitionNumbersEntrop.length;i++)
				System.out.println(PartitionNumbersEntrop[i]);
			
			
			
			
			///////////////////////////////////////////////////////////////////////////////////
			//////////////////////////////////////////////////////////////////////////////////
			//////////////////////////////////////////////////////////////////////////////////
			
			
			while ((CurrentLine = br.readLine()) != null) // reading data portion
			{
					words=null;
					if(!CurrentLine.isEmpty())
					{
						//System.out.println(sCurrentLine.substring(0,1));
						if(CurrentLine.charAt(0)!='%')
						//if(!sCurrentLine.substring(0,1).equals("%"))   -> also correct
						{
							//System.out.println(sCurrentLine);  // training samples
							//sampleNo++;
							words= CurrentLine.split(",");		// Splitting into components
							//int tempattcount=0; //count to index classDeg array
						//	nomattcount=0; // count of nominal attributes to index outer
								nomattcount=0;
								numericattcount=0; // for indexing the partition values
								for(int i=0;i<AttributeNames.size();i++)   //13/01/2016
								{													//18/03/2016
									//numericattcount=0;
									if(EntropyFlag[i]==1)
									{
										if(flagNumorRealCopy[i]==1)
										{		// Convert numeric values to representative elements of corresponding partition.
											//System.out.println(attPartitionValuesOrdered.get(numericattcount)); // sort this list   DONE :)
											for(int t=0;t<attPartitionValues.get(numericattcount).size()-1;t++)
											{				// replacing with rep. values.
												if((Float.valueOf(words[i])>=attPartitionValues.get(numericattcount).get(attPartitionValues.get(numericattcount).size()-1)))
												{  // greater than last partition value.
													wr.write(attPartitionValues.get(numericattcount).size()-1+",");
													break;
												}
												else if((Float.valueOf(words[i])>=attPartitionValues.get(numericattcount).get(t))&&(Float.valueOf(words[i])<attPartitionValues.get(numericattcount).get(t+1)))
												{
													wr.write(t+",");	
													break;
												}	
											/*	else
												{
													wr.write(words[i]+",");	
													break;
												}*/
												
											}											//18/03/2016
											//	wr.write(words[i]+",");			
											///////
											numericattcount++;
										}
										else
										{	//Write code for saving clas membrshp of att values into attdegsum (copy abov code)
											//tempattcount=0;
											for(int j=0;j<outer.get(nomattcount).size();j++) // comparing with all values of nominal att . and incr. count
											{
												if(words[i].equalsIgnoreCase(outer.get(nomattcount).get(j)))
												{ 	
													wr.write(String.valueOf(j)+",");
												}											
											}
											nomattcount++;
										}
									}
									else
									{
										if(flagNumorRealCopy[i]==1)
											numericattcount++;
										else
											nomattcount++;
									}
								}

						//		System.out.println(words[AttributeNames.size()]+"\n");
						//		System.out.println(CurrentLine);
								wr.write(words[AttributeNames.size()]+"\n"); //class value
							}
							//System.out.println(temp);
							
						}
					
					}	
				
			br.close();
			wr.close();
		/*
			nomattcount=0;
			System.out.println("____________________________________________\n\n");
			for(int k=0;k<AttributeNames.size();k++)
			{
				//System.out.print(AttributeNames.get(k));
				if(flagNumorRealCopy[k]==1)
					System.out.print("\tNUMERIC");
				else
				{
				for(int j=0;j<outer.get(nomattcount).size();j++)		
					{
					//System.out.println()
					System.out.print("\t"+outer.get(nomattcount).get(j)); 
					//System.out.print(" "+attributedegsum); 
					}
					nomattcount++;
				}
				System.out.println("");
			}*/
			return PartitionNumbersEntrop;
			
		}
	
}
