package com.kesdip.common.domain.generated;
// Generated 2 Дек 2008 2:04:59 рм by Hibernate Tools 3.2.0.b9


import java.util.HashSet;
import java.util.Set;

/**
 * 			Domain object for the 'Group' entity. Auto-generated
 * 			code. <strong>Do not modify manually.</strong>
 * 			@author gerogias
 * 		
 */
public class InstallationGroup  implements java.io.Serializable {


     /**
      * 				Primary, surrogate key.
 * 			
     */
     private Long id;
     /**
      * 				The name of the group.
 * 			
     */
     private String name;
     /**
      *         		Comments for this group.
 *         	
     */
     private String comments;
     /**
      * The parent customer.
     */
     private Customer customer;
     /**
      * 				The installations of the group.
 * 			
     */
     private Set<Installation> installations = new HashSet<Installation>(0);

    public InstallationGroup() {
    }

	
    public InstallationGroup(String name, Customer customer) {
        this.name = name;
        this.customer = customer;
    }
    public InstallationGroup(String name, String comments, Customer customer, Set<Installation> installations) {
       this.name = name;
       this.comments = comments;
       this.customer = customer;
       this.installations = installations;
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
     *      * 				The name of the group.
     * 			
     */
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    /**       
     *      *         		Comments for this group.
     *         	
     */
    public String getComments() {
        return this.comments;
    }
    
    public void setComments(String comments) {
        this.comments = comments;
    }
    /**       
     *      * The parent customer.
     */
    public Customer getCustomer() {
        return this.customer;
    }
    
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    /**       
     *      * 				The installations of the group.
     * 			
     */
    public Set<Installation> getInstallations() {
        return this.installations;
    }
    
    public void setInstallations(Set<Installation> installations) {
        this.installations = installations;
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
      buffer.append("customer").append("='").append(getCustomer()).append("' ");			
      buffer.append("]");
      
      return buffer.toString();
     }

   public boolean equals(Object other) {
         if ( (this == other ) ) return true;
		 if ( (other == null ) ) return false;
		 if ( !(other instanceof InstallationGroup) ) return false;
		 InstallationGroup castOther = ( InstallationGroup ) other; 
         
		 return ( (this.getId()==castOther.getId()) || ( this.getId()!=null && castOther.getId()!=null && this.getId().equals(castOther.getId()) ) );
   }
   
   public int hashCode() {
         int result = 17;
         
         result = 37 * result + ( getId() == null ? 0 : this.getId().hashCode() );
         
         
         
         
         return result;
   }   


}


