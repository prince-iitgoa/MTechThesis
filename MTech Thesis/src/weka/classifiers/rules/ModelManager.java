package weka.classifiers.rules;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import weka.core.Instances;



public class ModelManager implements Serializable {
	 static final long serialVersionUID = -3429427003147861443L;
	    private int cells_NR;
	    private DataSet dataSet;
	    Cell[] cells;
	    int partitions[];

	    public Cell[] getCells() 
	    {	//getter method for list of cells
	        return cells;
	    }

	    public int getCells_NR() 
	    {	//getter method for number of cells
	        return cells.length;
	    }

	    public ModelManager(DataSet newDataSet,int cellno)   //constructor
	    {	//constructor
	    	cells_NR=cellno;
	        cells = new Cell[cellno+1];
			dataSet=newDataSet;
			partitions=new int[dataSet.getAttributes_NR()];  // partitions of automata
	    }

	    public void getMetadataInfo() 
	    {		//display metadata information constructed from the data file
	        getDataSet().getInfo();
	    }

	    public void getCellsInfo() 
	    {			//display cell data information
	       System.out.println("Index FixedCell MembershipDegrees Neighbourbood<>");
	        System.out.println("Cells Information:");
	         for (int i=1;i<=cells_NR;i++)
	        {
	            //cells.getCellInfo();
	        	cells[i].getCellInfos();
	        }
	    }

	    public void setCells(Cell[] cells) 
	    {
	        this.cells = cells;
	    }

	    public void createCells() 
	    {		//create list of cells
		
	        //cells_NR = 1;
	    //    Cell[] newCells = new Cell[cells_NR+1];
	        setCells(cells);
	        int k;
	       // float subRangeLow, subRangeUp;
	     //   int dimension;
	       
	        int h=0;
	        System.out.println("Partitions :");
	        for (Attribute attribute : dataSet.getAttributes()) 	
	        {////						// for easier calculation of neighbourhood values
	           // cells_NR *= attribute.getPartitionSize();
	        	if(h!=0)
	        		partitions[h]=attribute.getPartitionSize()*partitions[h-1];
	        	else
	        		partitions[h]=attribute.getPartitionSize();
	        	
	        	System.out.print(partitions[h]);
	        	h++;
	        	
	        }
	        System.out.println(" No: of cells required is : "+cells_NR);
	        int attno=dataSet.getAttributes_NR();
	      //  System.out.println("Cells Information:");
	        //System.out.println("FixedCell MembershipDegrees Neighbourbood<>");
	         for (int i = 1; i <= cells_NR; ++i) 
	        {
	        	 ArrayList<Integer> tempNeighbours=new ArrayList<Integer>();;
	   	      
	            Cell cell = new Cell(dataSet.getClasses_NR());
	            cell.setFixedFlag(false);
	          
	        
	            int tempx=i%partitions[0];
	            if(tempx==0||tempx-1>0)
	            	tempNeighbours.add(i-1);
	            
	            if(tempx+1<=partitions[0] && tempx!=0)
	            	tempNeighbours.add(i+1);
	            
	            for(int j=1;j<partitions.length;j++)
	            {
	            
	            	tempx=i%partitions[j];
	            	if(tempx==0||tempx-partitions[j-1]>0)
	            		tempNeighbours.add(i-partitions[j-1]);
	            	if(tempx+partitions[j-1]<=partitions[j] && tempx!=0)
	            		tempNeighbours.add(i+partitions[j-1]);
	            }
	        //    cell.getCellInfos();
	            cell.addNeighbours(tempNeighbours);
	            cell.setIndex(i);
	            cells[i]=cell;
	          //  cell.getCellInfos();
	        }
	     }
	    
	    public DataSet getDataSet() 
	    {	//getter method for DataSet object
	        return dataSet;
	    }

		public void populateCells(Instances data) {
			// TODO Auto-generated method stub
			
		        double Data[][] = new double[data.numInstances()][data.numAttributes()]; 
		        for (int i = 0; i < data.numInstances(); i++) 
		        { 
		      //  	System.out.println();
		        //	System.out.print(data.instance(i).value(0)+" ");
		        	int tempindex=(int)(data.instance(i).value(0)+1);
		            for (int j = 1; j < data.numAttributes()-1; j++) 
		            { 
		            	tempindex=tempindex+(int)(data.instance(i).value(j))*partitions[j-1];
		                Data[i][j] = data.instance(i).value(j); 
		           //     System.out.print(Data[i][j]+" ");
		              //  System.out.println(tempindex);
		            } //System.out.println(tempindex);
		           // System.out.println((int)data.instance(i).value(data.numAttributes()-1));
		            cells[tempindex].setMembershipDegree((int)data.instance(i).value(data.numAttributes()-1));
		            cells[tempindex].setFixedFlag(true);
		       //     cells[tempindex].setMembershipDegree((int)data.instance(i).value(data.numAttributes()));
		            
		        } 
		    
		   
		}

		public void normalizeCells() 
		{
			// TODO Auto-generated method stub
			for(int i=1;i<=cells_NR;i++)
			{
				cells[i].normalizeMembershipDegree();
			}
			
		}

		public void trainCells() {
			// TODO Auto-generated method stub
		//	Cell[] tempcells=cells;
			ArrayList<Integer> temp= new ArrayList<Integer>();
			ArrayList<ArrayList<Integer>> NeighbourValues= new ArrayList<ArrayList<Integer>>();
			for(int i=1;i<=cells_NR;i++)		// storing neigbour values in an array to avoid repeted computations.
			{
				temp=cells[i].returnNeighbours();
				NeighbourValues.add(temp);     //index from zero
			}
			//System.out.println(NeighbourValues);
			for(int i=1;i<=cells_NR;i++) // no. of diffusion stages - trial
			{
				
					cells=calcAverages(cells,NeighbourValues);
			}
		}

		private Cell[] calcAverages(Cell[] celss,ArrayList<ArrayList<Integer>> NeighbourValues)    // verify code to ensure safety.
		{
			// TODO Auto-generated method stub
			Cell[] tempcel=celss;
			//ArrayList<Integer> tempMemb= new ArrayList<Integer>();
			Float[] tempMemb=new Float[dataSet.getClasses_NR()];  // stores average membership list of a cell
			Float[] singleMemb= new Float[dataSet.getClasses_NR()];
			for(int i=1;i<=cells_NR;i++)  // one step in diffusion
			{
				if(celss[i].isFixedFlag()==false) // no trainig required if true
				{
					//System.out.println();
					for(int j=0;j<tempMemb.length;j++)  //initializing- just to make sure
						tempMemb[j]=(float) 0;
					/*for(int j=0;j<tempMemb.length;j++)
						System.out.print(tempMemb[j]);*/
					for(int k=0;k<NeighbourValues.get(i-1).size();k++) // looping over neighbours
					{
						singleMemb=celss[NeighbourValues.get(i-1).get(k)].getMembershipDegrees(); // taking list of memberships of each neighbours
						for(int l=0;l<tempMemb.length;l++)
						{
							tempMemb[l]=tempMemb[l]+singleMemb[l]; // adding them
						}
					}
					for(int l=0;l<tempMemb.length;l++)
					{
						tempMemb[l]=tempMemb[l]/NeighbourValues.get(i-1).size(); // tempmemb now contains average of neighbour values f all membershps- of a cell
					}
					tempcel[i].updateMembership(tempMemb);
				}
			}
			return tempcel;
			
			
		}
}
