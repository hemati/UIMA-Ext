

/* First created by JCasGen Thu Aug 02 21:51:08 MSK 2012 */
package ru.kfu.cll.uima.tokenizer.fstype;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Thu Aug 02 21:51:08 MSK 2012
 * XML source: D:/projects/uima-ext/UIMA.Ext.Tokenizer/src/main/resources/ru/ksu/niimm/cll/uima/tokenizer/tokenizer-TypeSystem.xml
 * @generated */
public class QUESTION extends PM {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(QUESTION.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated  */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected QUESTION() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public QUESTION(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public QUESTION(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public QUESTION(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {/*default - does nothing empty block */}
     
}

    