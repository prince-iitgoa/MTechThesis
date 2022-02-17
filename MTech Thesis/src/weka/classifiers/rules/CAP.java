/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 *    OneR.java
 *    Copyright (C) 1999-2012 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.classifiers.rules;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.ListIterator;
import java.util.LinkedList;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.Sourcable;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionHandler;
import weka.core.RevisionUtils;
import weka.core.TechnicalInformation;
import weka.core.TechnicalInformation.Field;
import weka.core.TechnicalInformation.Type;
import weka.core.TechnicalInformationHandler;
import weka.core.Utils;
import weka.core.WekaException;

/**
 <!-- globalinfo-start -->
 * Class for building and using a 1R classifier; in other words, uses the minimum-error attribute for prediction, discretizing numeric attributes. For more information, see:<br/>
 * <br/>
 * R.C. Holte (1993). Very simple classification rules perform well on most commonly used datasets. Machine Learning. 11:63-91.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * BibTeX:
 * <pre>
 * &#64;article{Holte1993,
 *    author = {R.C. Holte},
 *    journal = {Machine Learning},
 *    pages = {63-91},
 *    title = {Very simple classification rules perform well on most commonly used datasets},
 *    volume = {11},
 *    year = {1993}
 * }
 * </pre>
 * <p/>
 <!-- technical-bibtex-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre> -B &lt;minimum bucket size&gt;
 *  The minimum number of objects in a bucket (default: 6).</pre>
 * 
 <!-- options-end -->
 * 
 * @author Ian H. Witten (ihw@cs.waikato.ac.nz)
 * @author Prince Mathew (princemathew07@gmail.com)
 * @version Beta
*/
public class CAP    // abstract was not before , included since build classifier takes more parameters
  extends AbstractClassifier 
  implements TechnicalInformationHandler, Sourcable {
    
	private ModelManager model;//CERATES A PREDICTION MODEL HERE private PredictionModel model;
  /** for serialization */
  static final long serialVersionUID = -3459427003147861443L;
  
  /**
   * Returns a string describing classifier
   * @return a description suitable for
   * displaying in the explorer/experimenter gui
   */
  public String globalInfo() {

    return "Class for building and using a 1R classifier; in other words, uses "
      + "the minimum-error attribute for prediction, discretizing numeric "
      + "attributes. For more information, see:\n\n"
      + getTechnicalInformation().toString();
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing 
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   * 
   * @return the technical information about this class
   */
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation 	result;
    
    result = new TechnicalInformation(Type.ARTICLE);
    result.setValue(Field.AUTHOR, "R.C. Holte");
    result.setValue(Field.YEAR, "1993");
    result.setValue(Field.TITLE, "Very simple classification rules perform well on most commonly used datasets");
    result.setValue(Field.JOURNAL, "Machine Learning");
    result.setValue(Field.VOLUME, "11");
    result.setValue(Field.PAGES, "63-91");
    
    return result;
  }

  /**
   * Class for storing store a 1R rule.
   */
  private class OneRRule implements Serializable, RevisionHandler {
    
    /** for serialization */
    static final long serialVersionUID = 2252814630957092281L;

    /** The class attribute. */
    private Attribute m_class;

    /** The number of instances used for building the rule. */
    private int m_numInst;

    /** Attribute to test */
    private Attribute m_attr; 

    /** Training set examples this rule gets right */
    private int m_correct; 

    /** Predicted class for each value of attr */
    private int[] m_classifications; 

    /** Predicted class for missing values */
    private int m_missingValueClass = -1; 

    /** Breakpoints (numeric attributes only) */
    private double[] m_breakpoints; 
  
    /**
     * Constructor for nominal attribute.
     * 
     * @param data the data to work with
     * @param attribute the attribute to use
     * @throws Exception if something goes wrong
     */
    public OneRRule(Instances data, Attribute attribute) throws Exception {

      m_class = data.classAttribute();
      m_numInst = data.numInstances();
      m_attr = attribute;
      m_correct = 0;
      m_classifications = new int[m_attr.numValues()];
    }

    /**
     * Constructor for numeric attribute.
     * 
     * @param data the data to work with
     * @param attribute the attribute to use
     * @param nBreaks the break point
     * @throws Exception if something goes wrong
     */
    public OneRRule(Instances data, Attribute attribute, int nBreaks) throws Exception {

      m_class = data.classAttribute();
      m_numInst = data.numInstances();
      m_attr = attribute;
      m_correct = 0;
      m_classifications = new int[nBreaks];
      m_breakpoints = new double[nBreaks - 1]; // last breakpoint is infinity
    }
    
    /**
     * Returns a description of the rule.
     * 
     * @return a string representation of the rule
     */
    public String toString() {

      try {
	StringBuffer text = new StringBuffer();
	text.append(m_attr.name() + ":\n");
	for (int v = 0; v < m_classifications.length; v++) {
	  text.append("\t");
	  if (m_attr.isNominal()) {
	    text.append(m_attr.value(v));
	  } else if (v < m_breakpoints.length) {
	    text.append("< " + m_breakpoints[v]);
	  } else if (v > 0) {
	    text.append(">= " + m_breakpoints[v - 1]);
	  } else {
	    text.append("not ?");
	  }
	  text.append("\t-> " + m_class.value(m_classifications[v]) + "\n");
	}
	if (m_missingValueClass != -1) {
	  text.append("\t?\t-> " + m_class.value(m_missingValueClass) + "\n");
	}
	text.append("(" + m_correct + "/" + m_numInst + " instances correct)\n");
	return text.toString();
      } catch (Exception e) {
	return "Can't print OneR classifier!";
      }
    }
    
    /**
     * Returns the revision string.
     * 
     * @return		the revision
     */
    public String getRevision() {
      return RevisionUtils.extract("$Revision: 9918 $");
    }
  }
  
  /** A 1-R rule */
  private OneRRule m_rule;

  /** The minimum bucket size */
  private int m_minBucketSize = 6;
  private List<Float> attributesMaxValue;  //REMOVE THESE AFTER CAREFULL EXAMINATION  - no need will represent partition values
  private List<Float> attributesMinValue;

  /** a ZeroR model in case no model can be built from the data */
  private Classifier m_ZeroR;
    
  /**
   * Classifies a given instance.
   *
   * @param inst the instance to be classified
   * @return the classification of the instance
   */// CHANGES HERE 1
  @SuppressWarnings("null")
public double classifyInstance(Instance data) throws Exception {

	  double index;
	 // System.out.println(data);
  	  int tempindex=(int)(data.value(0)+1);

	  //System.out.println(tempindex);
          for (int j = 1; j < data.numAttributes()-1; j++) 
          { 
          	tempindex=tempindex+(int)(data.value(j))*model.partitions[j-1]; // index in automata
          } 
         Float[] MemDeg=model.cells[tempindex].getMembershipDegrees();
     //    System.out.println(MemDeg);
         float large=MemDeg[0];
         index=0;
         for(int i=1;i<MemDeg.length;i++)
         {
        	 if(MemDeg[i]>large)
        	 {
        		 large=MemDeg[i];
        		 index=i;
        	 }
         }
          return(index);
  
  }

  /**
   * Returns default capabilities of the classifier.
   *
   * @return      the capabilities of this classifier
   */
  public Capabilities getCapabilities() {
    Capabilities result = super.getCapabilities();
    result.disableAll();

    // attributes
    result.enable(Capability.NOMINAL_ATTRIBUTES);
    result.enable(Capability.NUMERIC_ATTRIBUTES);
    result.enable(Capability.DATE_ATTRIBUTES);
    result.enable(Capability.MISSING_VALUES);

    // class
    result.enable(Capability.NOMINAL_CLASS);
    result.enable(Capability.MISSING_CLASS_VALUES);

    return result;
  }

  /**
   * Generates the classifier.
   *
   * @param instances the instances to be used for building the classifier
   * @throws Exception if the classifier can't be built successfully
   *///CHANGES HERE 2
  public void buildClassifier(Instances instances) throws Exception   // CHANGE -------------------- No:ONE 1
  {
	  DataSet dataSet=new DataSet();//create data set object
	  String message = null;
	  int totalparts=1;
	  CAP CAPP= new CAP();
	  dataSet.setClasses_NR(instances.numClasses());		//NUMBER OF CLASSES
	  dataSet.setDataPoints_NR((instances).numInstances());			//NUMBER OF INSTANCES
	  //We avoid finding max and min values of attributes here
	  System.out.println("\n__________________________________________");
	  message="\n_________________________________________________";
	  System.out.println("\n______ Attribute Names inside classifier ________");
	  message=message+"\n______ Attribute Names inside classifier ________";
	  message=message+"\nAttribute Name \t Number of Partitions";
	  for(int i=0;i<instances.numAttributes()-1;++i)
	  {
		  weka.classifiers.rules.Attribute attribute=new weka.classifiers.rules.Attribute();//create attribute objects	
		  attribute.setName(instances.attribute(i).name());
		  System.out.print(instances.attribute(i).name());
		  message=message+"\n"+instances.attribute(i).name();
		  
		  
		  if(CAPP.attributesMaxValue==null){//find min and max of each attribute  //AVOID THIS STEPv -no need now these are partition values (max)
			//	CAPP.attributesMinValue=new ArrayList<Float>();  //INITIALIZATION
				CAPP.attributesMaxValue=new ArrayList<Float>();
			}
			/*fINDING MAX AND MIN VALUES */  // For us it is partition values - instead of passing we find the no partitions here indirectly
			if(CAPP.attributesMaxValue.size()<instances.numAttributes()-1){		 
				CAPP.attributesMaxValue.add(i,(Float)((float)instances.attributeStats(i).numericStats.max));
				//CAPP.attributesMinValue.add(i,(Float)((float)instances.attributeStats(i).numericStats.min));
			}
			if((((float)instances.attributeStats(i).numericStats.max))>(CAPP.attributesMaxValue.get(i).floatValue())){
				CAPP.attributesMaxValue.set(i,(Float)((float)instances.attributeStats(i).numericStats.max));
			}
		/*	if(((float)instances.attributeStats(i).numericStats.min)<(CAPP.attributesMinValue.get(i).floatValue())){
				CAPP.attributesMinValue.set(i,(Float)((float)instances.attributeStats(i).numericStats.min));
			}*/
			//////////////////////
		//	attribute.setRangeUpper(CAPP.attributesMaxValue.get(i).floatValue());  //SETTING BOUNDS
		//	attribute.setRangeLower(CAPP.attributesMinValue.get(i).floatValue());
			System.out.print("  max= "+CAPP.attributesMaxValue.get(i).floatValue());  // displays no of partitions now.
			 message=message+"\t"+CAPP.attributesMaxValue.get(i).floatValue();
		//	System.out.println(" min= "+CAPP.attributesMinValue.get(i).floatValue());
			
		  
		/*  attribute.setRangeUpper(CA.attributesMaxValue.get(i).floatValue());  //SETTING BOUNDS
		  attribute.setRangeLower(CA.attributesMinValue.get(i).floatValue());*/ //Avoid this by passing a structure from pre-processing if required
		  //System.out.println("max="+attribute.getRangeUpper());
		  //System.out.println("min="+attribute.getRangeLower());
		  /*___ Set partition size based on the number of partitions defined during pre-processing__*/
		  attribute.setPartitionSize(CAPP.attributesMaxValue.get(i).intValue()+1);//partition each dimension into n  //BASED ON THE NUMBER OF PARTITIONS DEFINED
		  dataSet.getAttributes().add(attribute);
		  totalparts=(int) (totalparts*(CAPP.attributesMaxValue.get(i).intValue()+1));
	  }
	  message= message+"\n Total Number of cells Required = "+ totalparts;
	  model = new ModelManager(dataSet,totalparts);
//	
	  model.createCells();//create set of cells
	 // model.getCellsInfo();
	  model.populateCells(instances);//populate cells with info from training data
	//  model.getCellsInfo();
	  model.normalizeCells();//sum of membership degrees is made unity
	//  model.getCellsInfo();//display information about each cell
	  model.trainCells();//diffusion and training
	  model.getCellsInfo();//display information about each cell after the classifier has been built
	  System.out.println("model has been built");
	 // message=message+"\n Total Number of Cells Required = "+totalparts;
	//  gui.NewJFrame.jTextArea2.setText(null);
	// gui.NewJFrame.jTextArea2.setText(message);
	 // return message;
	 instances= null;
	  
  }


  /**
   * Create a rule branching on this attribute.
   *
   * @param attr the attribute to branch on
   * @param data the data to be used for creating the rule
   * @return the generated rule
   * @throws Exception if the rule can't be built successfully
   */
  public OneRRule newRule(Attribute attr, Instances data) throws Exception {

    OneRRule r;

    // ... create array to hold the missing value counts
    int[] missingValueCounts =
      new int [data.classAttribute().numValues()];
    
    if (attr.isNominal()) {
      r = newNominalRule(attr, data, missingValueCounts);
    } else {
      r = newNumericRule(attr, data, missingValueCounts);
    }
    r.m_missingValueClass = Utils.maxIndex(missingValueCounts);
    if (missingValueCounts[r.m_missingValueClass] == 0) {
      r.m_missingValueClass = -1; // signal for no missing value class
    } else {
      r.m_correct += missingValueCounts[r.m_missingValueClass];
    }
    return r;
  }

  /**
   * Create a rule branching on this nominal attribute.
   *
   * @param attr the attribute to branch on
   * @param data the data to be used for creating the rule
   * @param missingValueCounts to be filled in
   * @return the generated rule
   * @throws Exception if the rule can't be built successfully
   */
  public OneRRule newNominalRule(Attribute attr, Instances data,
                                 int[] missingValueCounts) throws Exception {

    // ... create arrays to hold the counts
    int[][] counts = new int [attr.numValues()]
                             [data.classAttribute().numValues()];
      
    // ... calculate the counts
    Enumeration enu = data.enumerateInstances();
    while (enu.hasMoreElements()) {
      Instance i = (Instance) enu.nextElement();
      if (i.isMissing(attr)) {
	missingValueCounts[(int) i.classValue()]++; 
      } else {
	counts[(int) i.value(attr)][(int) i.classValue()]++;
      }
    }

    OneRRule r = new OneRRule(data, attr); // create a new rule
    for (int value = 0; value < attr.numValues(); value++) {
      int best = Utils.maxIndex(counts[value]);
      r.m_classifications[value] = best;
      r.m_correct += counts[value][best];
    }
    return r;
  }

  /**
   * Create a rule branching on this numeric attribute
   *
   * @param attr the attribute to branch on
   * @param data the data to be used for creating the rule
   * @param missingValueCounts to be filled in
   * @return the generated rule
   * @throws Exception if the rule can't be built successfully
   */
  public OneRRule newNumericRule(Attribute attr, Instances data,
                             int[] missingValueCounts) throws Exception {

    // make a copy before sorting so that ties are treated consistently
    // and aren't affected by sorting performed for any numeric
    // attributes processed before this one
    data = new Instances(data);
    
    int lastInstance = data.numInstances();

    // missing values get sorted to the end of the instances
    data.sort(attr);
    while (lastInstance > 0 && 
           data.instance(lastInstance-1).isMissing(attr)) {
      lastInstance--;
      missingValueCounts[(int) data.instance(lastInstance).
                         classValue()]++; 
    }
    if (lastInstance == 0) {
      throw new Exception("Only missing values in the training data!");
    }

    // gather class distributions for all values
    double lastValue = 0;
    LinkedList<int[]> distributions = new LinkedList<int[]>();
    LinkedList<Double> values = new LinkedList<Double>();
    int[] distribution = null;
    for (int i = 0; i < lastInstance; i++) {

      // new value?
      if ((i == 0) || (data.instance(i).value(attr) > lastValue)) {
        if (i != 0) {
          values.add((lastValue + data.instance(i).value(attr)) / 2.0);
        }
        lastValue = data.instance(i).value(attr);
        distribution = new int[data.numClasses()];
        distributions.add(distribution);
      }
      distribution[(int)data.instance(i).classValue()]++;
    }
    values.add(Double.MAX_VALUE);

    // create iterator to go through list
    ListIterator<int[]> it = distributions.listIterator();
    ListIterator<Double> itVals = values.listIterator();
    int[] oldDist = null;
    while (it.hasNext()) {
      
      // grab next trivial bucket and iterate to next value as well
      int[] newDist = it.next();
      double val = itVals.next();

      // should we merge the two buckets?
      if ((oldDist != null) && 

          // classes the same?
          ((Utils.maxIndex(newDist) == Utils.maxIndex(oldDist)) ||

           // bucket not large enough?
           (oldDist[Utils.maxIndex(oldDist)] < m_minBucketSize))) {
        
        // add counts
        for (int j = 0; j < oldDist.length; j++) {
          newDist[j] += oldDist[j];
        }
        
        // remove distribution
        it.previous(); // element just visited
        it.previous(); // previous element we want to remove
        it.remove();
        it.next(); // back to element just visited

        // remove value
        itVals.previous(); // element just visited
        itVals.previous(); // previous element we want to remove
        itVals.remove();
        itVals.next(); // back to element just visited
      }
     
      // make progress
      oldDist = newDist;
    }

    // last scan, merge adjacent intervals with same class and calculate correct classifications
    int numCorrect = 0;
    it = distributions.listIterator();
    itVals = values.listIterator();
    oldDist = null;
    while (it.hasNext()) {
      
      // grab next trivial bucket and iterate to next value as well
      int[] newDist = it.next();
      double val = itVals.next();

      // number of correct classifications does not change by merging
      numCorrect += newDist[Utils.maxIndex(newDist)];

      // should we merge the two buckets?
      if ((oldDist != null) && 

          // classes the same?
          (Utils.maxIndex(newDist) == Utils.maxIndex(oldDist))) {
        
        // add counts
        for (int j = 0; j < oldDist.length; j++) {
          newDist[j] += oldDist[j];
        }
        
        // remove distribution
        it.previous(); // element just visited
        it.previous(); // previous element we want to remove
        it.remove();
        it.next(); // back to element just visited

        // remove value
        itVals.previous(); // element just visited
        itVals.previous(); // previous element we want to remove
        itVals.remove();
        itVals.next(); // back to element just visited
      }
     
      // make progress
      oldDist = newDist;
    }

    OneRRule r = new OneRRule(data, attr, distributions.size()); // new rule with cl branches
    r.m_correct = numCorrect;
    it = distributions.listIterator();
    itVals = values.listIterator();
    int v = 0;
    while (it.hasNext()) {
      r.m_classifications[v] = Utils.maxIndex(it.next());
      double splitPoint = itVals.next();
      if (itVals.hasNext()) {
	r.m_breakpoints[v] = splitPoint;
      }
      v++;
    }

    return r;
  }

  /**
   * Returns an enumeration describing the available options..
   *
   * @return an enumeration of all the available options.
   */
  public Enumeration listOptions() {

    String string = "\tThe minimum number of objects in a bucket (default: 6).";

    Vector newVector = new Vector(1);

    newVector.addElement(new Option(string, "B", 1, 
				    "-B <minimum bucket size>"));

    return newVector.elements();
  }

  /**
   * Parses a given list of options. <p/>
   *
   <!-- options-start -->
   * Valid options are: <p/>
   * 
   * <pre> -B &lt;minimum bucket size&gt;
   *  The minimum number of objects in a bucket (default: 6).</pre>
   * 
   <!-- options-end -->
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  public void setOptions(String[] options) throws Exception {
    
    String bucketSizeString = Utils.getOption('B', options);
    if (bucketSizeString.length() != 0) {
      m_minBucketSize = Integer.parseInt(bucketSizeString);
    } else {
      m_minBucketSize = 6;
    }
    //SET OPTIONS GIVEN HERE  super.setOptions(options);
  }

  /**
   * Gets the current settings of the OneR classifier.
   *
   * @return an array of strings suitable for passing to setOptions
   */
  public String [] getOptions() { //CHANGES HERE

    String [] options = new String [2];
    int current = 0;

    options[current++] = "-B"; options[current++] = "" + m_minBucketSize;

    while (current < options.length) {
      options[current++] = "";
    }
    return options;
  }

  /**
   * Returns a string that describes the classifier as source. The
   * classifier will be contained in a class with the given name (there may
   * be auxiliary classes),
   * and will contain a method with the signature:
   * <pre><code>
   * public static double classify(Object[] i);
   * </code></pre>
   * where the array <code>i</code> contains elements that are either
   * Double, String, with missing values represented as null. The generated
   * code is public domain and comes with no warranty.
   *
   * @param className the name that should be given to the source class.
   * @return the object source described by a string
   * @throws Exception if the souce can't be computed
   */
  public String toSource(String className) throws Exception {
    StringBuffer        result;
    int                 i;
    
    result = new StringBuffer();
    
    if (m_ZeroR != null) {
      result.append(((ZeroR) m_ZeroR).toSource(className));
    }
    else {
      result.append("class " + className + " {\n");
      result.append("  public static double classify(Object[] i) {\n");
      result.append("    // chosen attribute: " + m_rule.m_attr.name() + " (" + m_rule.m_attr.index() + ")\n");
      result.append("\n");
      // missing values
      result.append("    // missing value?\n");
      result.append("    if (i[" + m_rule.m_attr.index() + "] == null)\n");
      if (m_rule.m_missingValueClass != -1)
        result.append("      return Double.NaN;\n");
      else
        result.append("      return 0;\n");
      result.append("\n");
      
      // actual prediction
      result.append("    // prediction\n");
      result.append("    double v = 0;\n");
      result.append("    double[] classifications = new double[]{" + Utils.arrayToString(m_rule.m_classifications) + "};");
      result.append(" // ");
      for (i = 0; i < m_rule.m_classifications.length; i++) {
        if (i > 0)
          result.append(", ");
        result.append(m_rule.m_class.value(m_rule.m_classifications[i]));
      }
      result.append("\n");
      if (m_rule.m_attr.isNominal()) {
        for (i = 0; i < m_rule.m_attr.numValues(); i++) {
          result.append("    ");
          if (i > 0)
            result.append("else ");
          result.append("if (((String) i[" + m_rule.m_attr.index() + "]).equals(\"" + m_rule.m_attr.value(i) + "\"))\n");
          result.append("      v = " + i + "; // " + m_rule.m_class.value(m_rule.m_classifications[i]) + "\n");
        }
      }
      else {
        result.append("    double[] breakpoints = new double[]{" + Utils.arrayToString(m_rule.m_breakpoints) + "};\n");
        result.append("    while (v < breakpoints.length && \n");
        result.append("           ((Double) i[" + m_rule.m_attr.index() + "]) >= breakpoints[(int) v]) {\n");
        result.append("      v++;\n");
        result.append("    }\n");
      }
      result.append("    return classifications[(int) v];\n");
      
      result.append("  }\n");
      result.append("}\n");
    }
    
    return result.toString();
  }

  /**
   * Returns a description of the classifier
   * 
   * @return a string representation of the classifier
   */
  public String toString() {

    // only ZeroR model?
    if (m_ZeroR != null) {
      StringBuffer buf = new StringBuffer();
      buf.append(this.getClass().getName().replaceAll(".*\\.", "") + "\n");
      buf.append(this.getClass().getName().replaceAll(".*\\.", "").replaceAll(".", "=") + "\n\n");
      buf.append("Warning: No model could be built, hence ZeroR model is used:\n\n");
      buf.append(m_ZeroR.toString());
      return buf.toString();
    }
    
    if (m_rule == null) {
      return "OneR: No model built yet.";
    }
    return m_rule.toString();
  }

  /**
   * Returns the tip text for this property
   * @return tip text for this property suitable for
   * displaying in the explorer/experimenter gui
   */
  public String minBucketSizeTipText() {
    return "The minimum bucket size used for discretizing numeric "
      + "attributes.";
  }
  
  /**
   * Get the value of minBucketSize.
   * @return Value of minBucketSize.
   */
  public int getMinBucketSize() {
    
    return m_minBucketSize;
  }
  
  /**
   * Set the value of minBucketSize.
   * @param v  Value to assign to minBucketSize.
   */
  public void setMinBucketSize(int v) {
    
    m_minBucketSize = v;
  }
  
  /**
   * Returns the revision string.
   * 
   * @return		the revision
   */
  public String getRevision() {
    return RevisionUtils.extract("$Revision: 9918 $");
  }
  
  /**
   * Main method for testing this class
   * 
   * @param argv the commandline options
   */
  public static void main(String [] argv) {
    runClassifier(new OneR(), argv);
  }
}
