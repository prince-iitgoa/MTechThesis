/*
 * Representation of each cell
 */
package weka.classifiers.rules;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
/**
 *
 * @author Jerry
 */
public class Cell implements Serializable{

  static final long serialVersionUID = -3459427013147861443L;
    private boolean fixedFlag;
  //  private List<CellAttribute> cellAttributes;
    private Float[] membershipDegrees;
    private ArrayList<Integer> Neighbours;
  
    private int index;
    

    public Cell(int siz)
    {
    	this.membershipDegrees= new Float[siz];
    	for(int i=0;i<siz;i++)
    		membershipDegrees[i]=(float) 0;
    	//membershipDegrees[]={0};
    }
    public void getCellInfos() {
        System.out.println(getIndex()+" "+isFixedFlag() + " " + showMembershipDegrees()+" "+getNeighbours());
           }

    public void addNeighbours(ArrayList<Integer> tempneigh)
    {
    //	System.out.println(loc);
    	this.Neighbours= tempneigh;
   // 	this.Neighbours.add(loc);
    }
    public List<Integer> getNeighbours()
    {
    	return Neighbours;
    }
    public int getIndex() {
        return index;
    }
  

    public void setIndex(int index) {
        this.index = index;
    }
/*
    public List<CellAttribute> getCellAttributes() {
        return cellAttributes;
    }

    public void setCellAttributes(List<CellAttribute> cellAttributes) {
        this.cellAttributes = cellAttributes;
    }
*/
    public boolean isFixedFlag() {
        return fixedFlag;
    }

    public void setFixedFlag(boolean fixedFlag) {
        this.fixedFlag = fixedFlag;
    }

    public Float[] getMembershipDegrees() {
        return membershipDegrees;
    }

    public String showMembershipDegrees() {
    	String temp="";
       for(int i=0;i<this.membershipDegrees.length;i++)
    	   temp=temp+this.membershipDegrees[i]+" ";
       return temp;
    }
    public void setMembershipDegree(int classoffset) {
        this.membershipDegrees[classoffset]+=1;
    }
    public void updateMembership(Float[] tempMemb)
    {
    	this.membershipDegrees=tempMemb;
    }
    public void normalizeMembershipDegree()
    {	
    	float tempsum=0;
    	for(int i=0;i<this.membershipDegrees.length;i++)
    		tempsum=tempsum+this.membershipDegrees[i];
    	for(int i=0;i<this.membershipDegrees.length;i++)
    	{
    		if(tempsum!=0)  // otherwise becomes NaN - divide by zero
    			this.membershipDegrees[i]=(float)this.membershipDegrees[i]/tempsum;
    	}
    }
   public ArrayList<Integer> returnNeighbours()
   {
	   return Neighbours;
   }
}
