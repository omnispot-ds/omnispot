package com.kesdip.business.domain.admin.generated;
// Generated 7 Μαϊ 2010 12:09:18 πμ by Hibernate Tools 3.2.0.b9



/**
 * 			Domain object for the 'AccessRight' entity. Auto-generated code.
 * 			<strong>Do not modify manually.</strong> @author
 * 			gerogias
 * 		
 */
public class AccessRight  implements java.io.Serializable {


     /**
      * 				Primary, natural key.
 * 			
     */
     private String name;

    public AccessRight() {
    }

    public AccessRight(String name) {
       this.name = name;
    }
   
    /**       
     *      * 				Primary, natural key.
     * 			
     */
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    /**
     * toString
     * @return String
     */
     public String toString() {
	  StringBuffer buffer = new StringBuffer();

      buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
      buffer.append("name").append("='").append(getName()).append("' ");			
      buffer.append("]");
      
      return buffer.toString();
     }

   public boolean equals(Object other) {
         if ( (this == other ) ) return true;
		 if ( (other == null ) ) return false;
		 if ( !(other instanceof AccessRight) ) return false;
		 AccessRight castOther = ( AccessRight ) other; 
         
		 return ( (this.getName()==castOther.getName()) || ( this.getName()!=null && castOther.getName()!=null && this.getName().equals(castOther.getName()) ) );
   }
   
   public int hashCode() {
         int result = 17;
         
         result = 37 * result + ( getName() == null ? 0 : this.getName().hashCode() );
         return result;
   }   


}


