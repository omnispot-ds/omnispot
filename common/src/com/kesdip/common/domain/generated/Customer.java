package com.kesdip.common.domain.generated;
// Generated 2 Дек 2008 2:04:59 рм by Hibernate Tools 3.2.0.b9


import java.util.HashSet;
import java.util.Set;

/**
 * 			Domain object for the 'Customer' entity. Auto-generated
 * 			code. <strong>Do not modify manually.</strong>
 * 			@author gerogias
 * 		
 */
public class Customer  implements java.io.Serializable {


     /**
      * 				Primary, surrogate key.
 * 			
     */
     private Long id;
     /**
      * 				The name of the customer.
 * 			
     */
     private String name;
     /**
      *         		Comments for this customer.
 *         	
     */
     private String comments;
     /**
      * 				If the customer active or not.
 * 			
     */
     private boolean active;
     /**
      * 				The parameters of the action.
 * 			
     */
     private Set<Parameter> sites = new HashSet<Parameter>(0);

    public Customer() {
    }

	
    public Customer(String name, boolean active) {
        this.name = name;
        this.active = active;
    }
    public Customer(String name, String comments, boolean active, Set<Parameter> sites) {
       this.name = name;
       this.comments = comments;
       this.active = active;
       this.sites = sites;
    }
   
    /**       
     *      * 				Primary, surrogate key.
     * 			
     */
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    /**       
     *      * 				The name of the customer.
     * 			
     */
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    /**       
     *      *         		Comments for this customer.
     *         	
     */
    public String getComments() {
        return this.comments;
    }
    
    public void setComments(String comments) {
        this.comments = comments;
    }
    /**       
     *      * 				If the customer active or not.
     * 			
     */
    public boolean isActive() {
        return this.active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    /**       
     *      * 				The parameters of the action.
     * 			
     */
    public Set<Parameter> getSites() {
        return this.sites;
    }
    
    public void setSites(Set<Parameter> sites) {
        this.sites = sites;
    }

    /**
     * toString
     * @return String
     */
     public String toString() {
	  StringBuffer buffer = new StringBuffer();

      buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
      buffer.append("id").append("='").append(getId()).append("' ");			
      buffer.append("name").append("='").append(getName()).append("' ");			
      buffer.append("comments").append("='").append(getComments()).append("' ");			
      buffer.append("active").append("='").append(isActive()).append("' ");			
      buffer.append("]");
      
      return buffer.toString();
     }

   public boolean equals(Object other) {
         if ( (this == other ) ) return true;
		 if ( (other == null ) ) return false;
		 if ( !(other instanceof Customer) ) return false;
		 Customer castOther = ( Customer ) other; 
         
		 return ( (this.getId()==castOther.getId()) || ( this.getId()!=null && castOther.getId()!=null && this.getId().equals(castOther.getId()) ) );
   }
   
   public int hashCode() {
         int result = 17;
         
         result = 37 * result + ( getId() == null ? 0 : this.getId().hashCode() );
         
         
         
         
         return result;
   }   


}


