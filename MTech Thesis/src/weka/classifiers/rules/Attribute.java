/*
 * Encapsulation of attributes or features
 */
package weka.classifiers.rules;

import java.io.Serializable;
/**
 *
 * @author Jerry
 */
public class Attribute implements Serializable{

  static final long serialVersionUID = -3459427103147861443L;
    private String name;
   // private float rangeLower;
 //   private float rangeUpper;
    private int partitionSize;//one cell width-should not contain two classes in same cell

    public void getInfo() {
        System.out.println("name: " + getName());
     //   System.out.println("range: " + getRangeLower() + "-" + getRangeUpper());
        System.out.println("partition size: " + getPartitionSize());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPartitionSize() {
        return partitionSize;
    }

    public void setPartitionSize(int partitionSize) {
        this.partitionSize = partitionSize;
    }

 /*   public float getRangeLower() {  // Not required
        return rangeLower;
    }

    public void setRangeLower(float rangeLower) {  // Not required
        this.rangeLower = rangeLower;
    }

    public float getRangeUpper() {
        return rangeUpper;
    }

    public void setRangeUpper(float rangeUpper) {
        this.rangeUpper = rangeUpper;
    }*/
}
