package weka.classifiers.rules;


import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.ArrayList;

public class PreProcessing {
	ArrayList<ArrayList<Float>> AttPartitionValues = new ArrayList<ArrayList<Float>>();
	// partition values of numeric attributes
    ArrayList<Integer> AttPartitionNameindex= new ArrayList<Integer>();
	public int[] Preprocessing(String FilePath,int partitionno,int attnum) throws IOException
	{
		BufferedReader br = null;
		int[] PartitionEntrop = null;
			
		/*
		 ArrayList<ArrayList<Integer>> outer = new ArrayList<ArrayList<Integer>>();
    ArrayList<Integer> inner = new ArrayList<Integer>();        

    //inner.add(100);     
    //inner.add(200);
    //outer.add(inner); // add first list
   outer.add(new ArrayList<[var type]>(inner)); // create a new inner list that has the same content as  
                                           // the original inner list
  //  outer.add(inner); // add second list

    outer.get(0).add(300); // changes only the first inner list

    System.out.println(outer); 
		 
		 */
		try {

			String sCurrentLine;
		//	String FilePath;
			String[] words = null;
			int flag1=0,sampleNo=0;
			int i=0,j;
			int attc1=0,orgattc=0;
			int[] flagNumorReal= new int[200];
			int inittempflag=0;
			int attributedegsum=0;
			String temp;
			ArrayList<String> ar = new ArrayList<String>();
			ArrayList<String> clas = new ArrayList<String>();
			ArrayList<ArrayList<String>> outer = new ArrayList<ArrayList<String>>();
		 	ArrayList<String> inner = new ArrayList<String>();  
		 	

			ArrayList<ArrayList<Float>> numattouter = new ArrayList<ArrayList<Float>>();
		 	ArrayList<Float> numattinner = new ArrayList<Float>();  
		 	ArrayList<ArrayList<Integer>> numattouterValue = new ArrayList<ArrayList<Integer>>();
		 	ArrayList<Integer> numattinnerValue = new ArrayList<Integer>();  
		 	
			//List<Integer> classmemNo = new ArrayList<Integer>();
		 //	FilePath="C:\\iris.arff";
			br = new BufferedReader(new FileReader(FilePath));
			
			while ((sCurrentLine = br.readLine()) != null)  //Processing Header
			{
				words=null;
				if(!sCurrentLine.isEmpty())
				{
					//System.out.println(sCurrentLine.substring(0,1));
					if(sCurrentLine.charAt(0)!='%')
					//if(!sCurrentLine.substring(0,1).equals("%"))   -> also correct
					{
					//	System.out.println(sCurrentLine);
						if(flag1==0)
						{
							words= sCurrentLine.split(" |\\t");
							//System.out.println(words[0]);
							//System.out.println(words[1]);
							if(words[0].equalsIgnoreCase("@ATTRIBUTE"))
							{
							//	attributeNames[c]=words[1];
							//	String temp1="\'"+"class"+"\'";
							//	System.out.println(temp1);
								numattouter.add(new ArrayList<Float>(numattinner));
								numattouterValue.add(new ArrayList<Integer>(numattinnerValue));
								if(!words[1].equalsIgnoreCase("class")&&!words[1].equalsIgnoreCase("\'"+"class"+"\'"))
								{
									//System.out.println(words[1]);
									ar.add(words[1]);
									if(words[2].equalsIgnoreCase("numeric")||words[2].equalsIgnoreCase("real"))
									{
										flagNumorReal[orgattc]=1; // numeric or real attribute
									}
									else  // list of values
									{
										temp=sCurrentLine.substring(sCurrentLine.indexOf("{") + 1, sCurrentLine.indexOf("}")+1);
										outer.add(new ArrayList<String>(inner));
									//	System.out.println(temp);
										words=null;
										words=temp.split(" |\\{|\\}|,");
										//attc2=0;
										for(i=0;i<words.length;i++)
										{ // attribute values (predefined ones (lists))
															
										//	System.out.println(words[i]);
											outer.get(attc1).add(words[i]);
										}
										attc1++;
										//System.out.println();
									
									}
								}
								else
								{ //save class names
								//	temp=words[3];
									if(!words[2].equalsIgnoreCase("numeric") && !words[2].equalsIgnoreCase("real"))
									{
										temp=sCurrentLine.substring(sCurrentLine.indexOf("{") + 1, sCurrentLine.indexOf("}"));
									
											//System.out.println(temp);
										words=null;
										words=temp.split(" |\\{|\\}|,");
										for(i=0;i<words.length;i++)
										{
											clas.add(words[i]);
											//	System.out.print(words[i]);
										}
									}
								}
								//c++;
								orgattc++;
							}
							else if(words[0].equalsIgnoreCase("@data"))
							{
								flag1=1;
								break;
								//System.out.println(sCurrentLine);
							}
						}
					}
				}
			}
			//System.out.println(outer);
			int nomattcount=0;
			//System.out.println("Nominal Attribute's Values");
			for(i=0;i<ar.size();i++)
		    {
				System.out.println("\nAttribute Name : "+ar.get(i));
				if(flagNumorReal[i]==0)
					{
					for(j=0;j<outer.get(nomattcount).size();j++)		
						{
						//System.out.println()
						System.out.print("\t"+outer.get(nomattcount).get(j)); 
						attributedegsum++; // count number of diff values of attibutes
						//System.out.print(" "+attributedegsum); 
						}
						nomattcount++;
					}
		//   	System.out.println("\n"+attributedegsum); 
		    }
			
			int[] clasmemNo= new int[clas.size()];
			double[] EntropyAtt= new double[ar.size()];
		//	int[][] minmaxAtt= new int[2][ar.size()];
			int[][]classDeg=new int[attributedegsum][clas.size()]; // storing class membership values for every value of attribute
			float[][] MaxMin= new float[ar.size()][3];
		//	@SuppressWarnings("unchecked")
		//	ArrayList<Float>[] numatt = (ArrayList<Float>[])new ArrayList[ar.size()];
		//	int[] tempAttVals= new int[ar.size()];
			// int[][] attvalsMembrshp = new int[numatvals][clas.size()];
			while ((sCurrentLine = br.readLine()) != null) // reading data portion
			{
				
				{
					words=null;
					if(!sCurrentLine.isEmpty())
					{
						//System.out.println(sCurrentLine.substring(0,1));
						if(sCurrentLine.charAt(0)!='%')
						//if(!sCurrentLine.substring(0,1).equals("%"))   -> also correct
						{
							//System.out.println(sCurrentLine);  // training samples
							sampleNo++;
							words= sCurrentLine.split(",");		// Splitting into components
							int tempattcount=0; //count to index classDeg array
							nomattcount=0; // count of nominal attributes to index outer
							if(inittempflag==0)
							{
								for(i=0;i<ar.size();i++)   //13/01/2016
								{
									if(flagNumorReal[i]==1)			//Initializing values with that of first instance. (numeric only)
									{
										MaxMin[i][0]=Float.valueOf(words[i]);
										MaxMin[i][1]=Float.valueOf(words[i]);
										MaxMin[i][2]=Float.valueOf(words[i]);  // Loop again over the whole data for partitioning numeric attributes
								
										//duunno	numatt[i].add(Float.valueOf(words[i]));
										numattouter.get(i).add(Float.valueOf(words[i]));
										
										
												for(int k=0;k<clas.size();k++)
												{
													//if(numeric or real)- then updata max and min values
													if(words[ar.size()].equalsIgnoreCase(clas.get(k)))
														numattouterValue.get(i).add(k);					//update values in array list corresonding to values of attributes-class here.
												}
												
											
			
										
									}
									
									else
									{	//Write code for saving clas membrshp of att values into attdegsum
										for(j=0;j<outer.get(nomattcount).size();j++) // comparing with all values of nominal att . and incr. count
										{
											if(words[i].equalsIgnoreCase(outer.get(nomattcount).get(j)))
											{ 	int tem=0;
												for(int k=0;k<clas.size();k++)
												{
													//if(numeric or real)- then updata max and min values
													if(words[ar.size()].equalsIgnoreCase(clas.get(k)))
														tem=k;								//update values in array list corresonding to values of attributes-class here.
												}
												classDeg[tempattcount][tem]++;
											}
											tempattcount++;
											
										}
										nomattcount++;
									}
								}
								inittempflag=1;
							}
							else
							{
								for(i=0;i<ar.size();i++)   //13/01/2016
								{
									if(flagNumorReal[i]==1)
									{
										if(MaxMin[i][0]<Float.valueOf(words[i]))
											MaxMin[i][0]=Float.valueOf(words[i]);
										if(MaxMin[i][1]>Float.valueOf(words[i]))
											MaxMin[i][1]=Float.valueOf(words[i]);
										MaxMin[i][2]=MaxMin[i][2]+Float.valueOf(words[i]);
									//not sure	numatt[i].add(Float.valueOf(words[i]));
										numattouter.get(i).add(Float.valueOf(words[i]));
										
										for(int k=0;k<clas.size();k++)
										{
											//if(numeric or real)- then updata max and min values
											if(words[ar.size()].equalsIgnoreCase(clas.get(k)))
												numattouterValue.get(i).add(k);					//update values in array list corresonding to values of attributes-class here.
										}
										
									}
									else
									{	//Write code for saving clas membrshp of att values into attdegsum (copy abov code)
										//tempattcount=0;
										for(j=0;j<outer.get(nomattcount).size();j++) // comparing with all values of nominal att . and incr. count
										{
											if(words[i].equalsIgnoreCase(outer.get(nomattcount).get(j)))
											{ 	int tem=0;
												for(int k=0;k<clas.size();k++)
												{
													//if(numeric or real)- then updata max and min values
													if(words[ar.size()].equalsIgnoreCase(clas.get(k)))
														tem=k;								//update values in array list corresonding to values of attributes-class here.
												}
												classDeg[tempattcount][tem]++;
											}
											tempattcount++;
											
										}
										nomattcount++;
									}
								}
							}
							
							
							temp=words[ar.size()]; //class value
							//System.out.println(temp);
							for(i=0;i<clas.size();i++)
							{
								//if(numeric or real)- then updata max and min values
								if(words[ar.size()].equalsIgnoreCase(clas.get(i)))
									clasmemNo[i]++;										//update values in array list corresonding to values of attributes-class here.
							}
						}
					
					}	
				
				}
			}
			
			
			
			//System.out.println("Class membership values of Attribute values :");
			System.out.println("\n\nTotal Sample Number "+sampleNo);
			
			
		
			System.out.println("\n__________ Attribute\t\tMax\tMin\tMean __________\n");
			for(i=0;i<ar.size();i++)   //13/01/2016
			{
				if(flagNumorReal[i]==1)
				{
						System.out.println("\t"+ar.get(i)+"\t\t"+MaxMin[i][0]+"\t"+MaxMin[i][1]+"\t"+(MaxMin[i][2]/sampleNo)); //mean not stored
				}
			}
		/*	System.out.println("\n__________Attribute Names__________\n");
			for(i=0;i<ar.size();i++)
				System.out.println(ar.get(i));*/

			System.out.println("\n__________Class Names__________\n");
			for(i=0;i<clas.size();i++)
			{
				System.out.print(clas.get(i));
				System.out.println("\t"+clasmemNo[i]);
				//System.out.println(sampleNo);
			}
			
			System.out.println("\n____________________Nominal Attribute's Values & Memberships____________________");
			System.out.println("________________________________________________________________________________\n________________________________________________________________________________\n");
			
			attributedegsum=0;
			int h=0;
			for(i=0;i<outer.size();i++)
		    {
				while(flagNumorReal[h]==1)  ////////
				{
					h++;
				}
				System.out.println("\nAttribute Name : "+ar.get(h));
				h++;  ///////////
				
				int[][] tem= new int[outer.get(i).size()][clas.size()];////////////////
		    	for(j=0;j<outer.get(i).size();j++)		
		    	{
		    		
		    		System.out.print("\n"+outer.get(i).get(j));
		    		for(int k=0;k<clas.size();k++)
		    		{
		    			System.out.print("\t\t"+classDeg[attributedegsum][k]); 
		    			tem[j][k]=classDeg[attributedegsum][k]; ////////////////
		    		}
		    		attributedegsum++; // count number of diff values of attibutes
		    	}
		    		System.out.println("\n\n__________ Calculate Entropy : Attribute "+ar.get(h-1)+" __________");
		    		EntropyAtt[h-1]=CalculateEntropy(tem,outer.get(i).size(),clas.size(),sampleNo);
		   	//System.out.println(attributedegsum); 
		    }
			
			//ArrayList<ArrayList<Float>> outerPart = new ArrayList<ArrayList<Float>>();
		//	ArrayList<Float> innerPart = new ArrayList<Float>();  
			System.out.println("____________________Numeric Attribute's Values & Memberships____________________");
			for(i=0;i<ar.size();i++)
			{
				if(flagNumorReal[i]==1)
				{
					System.out.println("\nAttribute Name : "+ar.get(i));
				//	System.out.println("________________________________________________________________________________\n________________________________________________________________________________\n");
					
				//	System.out.println(numattouter.get(i));
							EntropyAtt[i]=PartitionNumeric(numattouter.get(i),numattouterValue.get(i),clas.size(),sampleNo,i,partitionno);
				}
			}
			//next run through data can calculate standard deviation of numeric data and partition them in the next run (Without SD ?)
			//not needed.. ! :P
			//
			ArrayList<String> OrgAttributeValues = new ArrayList<String>();
			//OrgAttributeValues=ar;
			
			System.out.println("__________ Entropy Values of Attributes __________\n");
			for(int k=0;k<ar.size();k++)
			{
				System.out.println(ar.get(k)+" "+EntropyAtt[k]);
				OrgAttributeValues.add(ar.get(k));
			}
			System.out.println("\n__________ Partition Values of Numeric Attributes __________");
			for(int k=0;k<AttPartitionValues.size();k++)
			{
				System.out.println((ar.get(AttPartitionNameindex.get(k))));
				System.out.println(AttPartitionValues.get(k));
			}
			//now second ru
			double tempent;
			String tempClas;
			for(int k=0;k<ar.size();k++)
			{
				for(int l=0;l<ar.size()-k-1;l++)
				{
					if(EntropyAtt[l]>EntropyAtt[l+1])
					{
						tempent=EntropyAtt[l];
						tempClas=ar.get(l);
						ar.set(l,ar.get(l+1));
						ar.set(l+1,tempClas);
					//	System.out.print("\n"+arrayList.get(j)+" "+arrayList.get(j+1));
						EntropyAtt[l]=EntropyAtt[l+1];
						EntropyAtt[l+1]=tempent;
					//	System.out.print("\t"+arrayList.get(j)+" "+arrayList.get(j+1));
					}
				}
			}
			
		
			System.out.println("\n______________ Sorted Entropy Values ______________\n");
			for(int k=0;k<ar.size();k++)
			{
				System.out.println(ar.get(k)+" "+EntropyAtt[k]);
			}
			//System.out.println("Hello");
			PartitionEntrop=TempArffCreator.ArffCreator(FilePath,OrgAttributeValues,ar,EntropyAtt,flagNumorReal,outer,clas,AttPartitionValues,attnum);
			//
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		finally 
		{
			try 
			{
				if (br != null)br.close();
			} 
			catch (IOException ex) 
			{
				ex.printStackTrace();
			}
		}
		 //System.out.println(FilePath+"\tPartitions: "+partitionno+"\tAttribute Number: "+attnum);
		br.close();	
		return PartitionEntrop;
	}


	/// get the stored values of numeric attributes (probably an array list) and do partitioning 
	// The total data can be initially partitioned into 10 ranges about the mean . 5 below and 5 above
	//caculate freqency(histogram) in each patition...
	//if freqency in a partition less than threshold combine with the nearby one if their sum is still below (repeat this)
	//if frequency less than threshold split into two  //say -  Threshold(num of samples/10) lowthreshold= threshold/2 uperthreshold= threshld+threshold/2
	// if cannot be partitioned leave as it is.
	
	public void SavePartitionValue(int AttributeNum,ArrayList<Float> PartitionVal)
	{
		//PartitionVal.add((float)AttributeNum);  // No. last element of the arraylist PartitionVal obtained by
						//PartitionVal.size() gives the Attribute Number.
		AttPartitionValues.add(PartitionVal);
		AttPartitionNameindex.add(AttributeNum);
	}
	
	//for time being partition into 6 three below mean and three above mean
	private double PartitionNumeric(ArrayList<Float> arrayList,ArrayList<Integer> arrayList1,int classsize,int sampleNo,int AttributeNo,int partno) 
	{
		//seven a[7] needed to store the offset of 6 partition array
		//sort the obtained arraylist
	 	float temp;
	 	int tempclas;
	/*	System.out.println("UNSorted List");
		for(int k=0;k<arrayList.size();k++)
		{
		System.out.println(arrayList.get(k)+" "+arrayList1.get(k));
		}*/
		//Now sort the arrayList   - here we use quick Sort
		for(int k=0;k<arrayList.size();k++)
		{
			for(int j=0;j<arrayList.size()-k-1;j++)
			{
				if(arrayList.get(j)>arrayList.get(j+1))
				{
					temp=arrayList.get(j);
					if(arrayList1.size()>j)
					{
					tempclas=arrayList1.get(j);
				//	System.out.print("\n"+arrayList.get(j)+" "+arrayList.get(j+1));
					arrayList.set(j,arrayList.get(j+1));
					arrayList1.set(j,arrayList1.get(j+1));
					arrayList.set(j+1,temp);
					arrayList1.set(j+1,tempclas);
				//	System.out.print("\t"+arrayList.get(j)+" "+arrayList.get(j+1));
					}
				}
			}
		}
		System.out.println("Sorted List of Numeric Values & Corresponding Classes");
		for(int k=0;k<arrayList.size();k++)
		{
		System.out.println(arrayList.get(k)+" "+arrayList1.get(k));
		}
	//	System.out.println(arrayList); //sorted List
		//Now Count the number of eacn value and store it removing redundant numbers
		//float AttvalueCount[][]=new float[arrayList.size()][2];
		ArrayList<Float> AttValue = new ArrayList<Float>();  
		ArrayList<Integer> AttvalueCount = new ArrayList<Integer>();  
		int flag=0;
		float temp1;
		int count=-1;
		int threshold= arrayList.size()/partno;      /// value from spinner gui
		for(int j=0;j<arrayList.size()-1;j++)
		{
			temp=arrayList.get(j);
			temp1=arrayList.get(j+1);
		/*	if((j==arrayList.size()-2)&&(temp!=temp1))
			{
				AttValue.add(arrayList.get(j+1));
				AttvalueCount.add(1);
			}*/
			if(flag==0)
			{
				AttValue.add(arrayList.get(j));
				AttvalueCount.add(1);
				count++;
				flag=1;
			}
			if(temp==temp1)
			{
				//System.out.println("Hello");
			//	System.out.println(AttvalueCount.get(count));
				AttvalueCount.set(count,AttvalueCount.get(count)+1);
				//System.out.println(AttvalueCount.get(count));
			}
			else if((j==arrayList.size()-2))
			{
				AttValue.add(arrayList.get(j+1));
				AttvalueCount.add(1);
			}
			else
				flag=0;
		}
		System.out.println("Attribute values and their count ");
		for(int j=0;j<AttValue.size();j++)
		{
			System.out.println(AttValue.get(j)+" "+AttvalueCount.get(j));
		}
		//System.out.println("Class SIze"+classsize);
		//now set maximum threshold (ie no of elements) in a partition based on the no:
		//of partitions required. say if we want no: of partitions around 8..ie say 8- 12
		//we set threshold as no:of samples/8;
		ArrayList<Float> PartitionValue= new ArrayList<Float>();  
		
		
		count=0;
		
		for(int j=0;j<AttValue.size();j++)
		{
			if(count==0)
			{
				PartitionValue.add(AttValue.get(j));
				count=count+AttvalueCount.get(j);
			}
			else
			{
				if(count+AttvalueCount.get(j)<=threshold)
				{
					count=count+AttvalueCount.get(j);
				}
				else
				{
					PartitionValue.add(AttValue.get(j));
					count=AttvalueCount.get(j);
				}
			}
		//	System.out.println(("asd"+PartitionValue.get(PartitionValue.size()-1)+" "+AttValue.get(j)));
		/*	if((j==AttValue.size()-1)&&(PartitionValue.get(PartitionValue.size()-1)!=AttValue.get(j)))
			{
				PartitionValue.add(AttValue.get(j));
				//System.out.println(("asd"+PartitionValue.get(PartitionValue.size()-1)+" "+AttValue.get(j)));
			}*///no need i think
		}
		System.out.println("Partition"+ PartitionValue+" " +"\nNo: Partitions = "+PartitionValue.size());
		SavePartitionValue(AttributeNo,PartitionValue);
		/*	System.out.println("Sorted List");
		for(int k=0;k<arrayList.size();k++)
		{
		System.out.println(arrayList.get(k)+" "+arrayList1.get(k));
		}*/
		// Return(Partition);     
		// we have arrayList and arrayList1 with attribute values and corresponding classes.
		int[][] PartitionMembership= new int[PartitionValue.size()][classsize];
		
		//Now calculate the attibute value - class membership values   //take stored values of attributes and corresponding class values.
		int partcount=0;
		//System.out.println("Size"+arrayList.size());
		for(int j=0;j<arrayList.size();j++)
		{
	/*		if((arrayList.get(j)>=PartitionValue.get(partcount+1))&&(partcount!=PartitionValue.size()-2))
			{
				
			}*/
		/*	if(PartitionValue.size()==1) // only one element in partition
			{ 
				for(int g=0;g<classsize;g++)
				{
					if(arrayList1.get(j)==g)
					{
						//System.out.println(arrayList1.get(j)+" "+g);
						PartitionMembership[partcount][g]=PartitionMembership[partcount][g]+1;
						break;
					}
				}
			}
			else*/ if(partcount==PartitionValue.size()-1)
			{
				if(arrayList.get(j)>=PartitionValue.get(partcount))
				{
					for(int g=0;g<classsize;g++)
					{
						if(arrayList1.get(j)==g)
						{
							//System.out.println(arrayList1.get(j)+" "+g);
							PartitionMembership[partcount][g]=PartitionMembership[partcount][g]+1;
							break;
						}
					}
				}
			}
			else
			{
				if(arrayList.get(j)<PartitionValue.get(partcount+1))
				{
					for(int g=0;g<classsize;g++)
					{
						if(arrayList1.get(j)==g)
						{
						//	System.out.println(PartitionMembership[partcount][g]);
						//	System.out.println(arrayList1.get(j)+" "+g);
							PartitionMembership[partcount][g]=PartitionMembership[partcount][g]+1;
						//	System.out.println(PartitionMembership[partcount][g]);
							break;
						}
					}
				}
				else
				{
					partcount++;
					j--;
				}
			}
		}
	/*	System.out.print("\nPartitionMembership [Partition][Class]");
		for(int j=0;j<PartitionValue.size();j++)
		{
			System.out.println();
			for(int k=0;k<classsize;k++)
			{
				System.out.print(PartitionMembership[j][k]+" ");
			}
		}*/
		System.out.println("\n\n__________ Calculate Entropy Numeric __________");
		double entrop=CalculateEntropy(PartitionMembership,PartitionValue.size(),classsize,sampleNo);
		return(entrop);
	}


	private static double CalculateEntropy(int[][] PartitionMembership,int partitionsize,int classsize,int sampleNo) {
				
		double Entropy=0; ///
		//System.out.println("\nCalculate Entropy");
	/*	for(int j=0;j<partitionsize;j++)
		{
			System.out.println();
			for(int k=0;k<classsize;k++)
			{
				System.out.print(PartitionMembership[j][k]+" ");
			}
		}*/
		// Now calculate the Shannon's Entropy and find the information gain using eqtn pi*log(1/pi)
		
		
		//int partitionsize=3,classsize=2,sampleNo=14;
		//int[][] PartitionMembership= new int[partitionsize][classsize];
		//int [][]PartitionMembership={{2,3},{4,0},{3,2}};//{{3,6,0},{1,2,3}};//
		int[] partitioncount=new int[partitionsize];
		double[] partitionEntropy=new double[partitionsize];
	//	double Entropy=0;
		System.out.println("\nPartition MemberShip [Partition][Class]");
		for(int j=0;j<partitionsize;j++)
		{
			System.out.println();
			for(int k=0;k<classsize;k++)
			{
				System.out.print(PartitionMembership[j][k]+" ");
				partitioncount[j]=partitioncount[j]+PartitionMembership[j][k];
			}
		}
		System.out.println("\n\nPartition Count");
		for(int j=0;j<partitionsize;j++)
		{
			System.out.print(partitioncount[j]+"\n");
		}
		
		System.out.println("\nCalculating Shannon's Entropy for Each Partition");
		for(int j=0;j<partitionsize;j++)
		//int j=0;
		{
			System.out.println();
			for(int k=0;k<classsize;k++)
			{
				//System.out.println((PartitionMembership[j][k]+" "+partitioncount[j]));
				if(PartitionMembership[j][k]!=0)
				{
					System.out.print(partitionEntropy[j]+"+(("+(double)PartitionMembership[j][k] /partitioncount[j]+")*(log("+(double)partitioncount[j]/PartitionMembership[j][k]+")/log("+(double)2+")))");
					partitionEntropy[j]= (partitionEntropy[j]+-1*(((double)PartitionMembership[j][k]/partitioncount[j])*(Math.log((double)PartitionMembership[j][k]/partitioncount[j])/Math.log((double)2))));
					System.out.println(" = "+partitionEntropy[j]);
				}
				//System.out.println(((PartitionMembership[j][k]/partitioncount[j])*(Math.log(partitioncount[j]/PartitionMembership[j][k])/Math.log(2))));
			}
		//I(s1,s2)= -s1/(s1+s2)*log[s1/(s1+s2)] -s2/(s1+s2)*log[s2/(s1+s2)]
		
	//	System.out.println(Math.log((double)5/2));
		}
		System.out.println("\nFinding the Entropy of the Attribute - Combining entropies of Partitions");
		for(int j=0;j<partitionsize;j++)
		//j=0;
		{
			//System.out.print("\n"+partitionEntropy[j]);
			Entropy=Entropy+((double)partitioncount[j]/sampleNo)*partitionEntropy[j];
		}
		System.out.println("\nEntropy is : "+Entropy);
		System.out.println("\n\n________________________________________________________________________________\n________________________________________________________________________________\n");
		
		return(Entropy);
	}
	

	
	
	
}
