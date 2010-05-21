package com.kesdip.business.domain.license.generated;
// Generated 7 Μαϊ 2010 12:09:19 πμ by Hibernate Tools 3.2.0.b9


import java.util.Date;

/**
 * 			Domain object for the 'Customer' entity.
 * 			Auto-generated
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
      * 				The UUID of the customer. Used in all communication from the installations.
 * 			
     */
     private String uuid;
     /**
      *         		Comments for this customer.
 *         	
     */
     private String comments;
     /**
      * 				If the customer is active or not.
 * 			
     */
     private boolean active;
     /**
      * 				How many admin console licenses have been purchased.
 * 			
     */
     private int adminLicensesPurchased;
     /**
      * 				How many player licenses have been purchased.
 * 			
     */
     private int playerLicensesPurchased;
     /**
      * 				The date when the current support contract, including updates, ends.
 * 			
     */
     private Date supportExpiryDate;

    public Customer() {
    }

	
    public Customer(String name, String uuid, boolean active, int adminLicensesPurchased, int playerLicensesPurchased, Date supportExpiryDate) {
        this.name = name;
        this.uuid = uuid;
        this.active = active;
        this.adminLicensesPurchased = adminLicensesPurchased;
        this.playerLicensesPurchased = playerLicensesPurchased;
        this.supportExpiryDate = supportExpiryDate;
    }
    public Customer(String name, String uuid, String comments, boolean active, int adminLicensesPurchased, int playerLicensesPurchased, Date supportExpiryDate) {
       this.name = name;
       this.uuid = uuid;
       this.comments = comments;
       this.active = active;
       this.adminLicensesPurchased = adminLicensesPurchased;
       this.playerLicensesPurchased = playerLicensesPurchased;
       this.supportExpiryDate = supportExpiryDate;
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
     *      * 				The UUID of the customer. Used in all communication from the installations.
     * 			
     */
    public String getUuid() {
        return this.uuid;
    }
    
    public void setUuid(String uuid) {
        this.uuid = uuid;
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
     *      * 				If the customer is active or not.
     * 			
     */
    public boolean isActive() {
        return this.active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    /**       
     *      * 				How many admin console licenses have been purchased.
     * 			
     */
    public int getAdminLicensesPurchased() {
        return this.adminLicensesPurchased;
    }
    
    public void setAdminLicensesPurchased(int adminLicensesPurchased) {
        this.adminLicensesPurchased = adminLicensesPurchased;
    }
    /**       
     *      * 				How many player licenses have been purchased.
     * 			
     */
    public int getPlayerLicensesPurchased() {
        return this.playerLicensesPurchased;
    }
    
    public void setPlayerLicensesPurchased(int playerLicensesPurchased) {
        this.playerLicensesPurchased = playerLicensesPurchased;
    }
    /**       
     *      * 				The date when the current support contract, including updates, ends.
     * 			
     */
    public Date getSupportExpiryDate() {
        return this.supportExpiryDate;
    }
    
    public void setSupportExpiryDate(Date supportExpiryDate) {
        this.supportExpiryDate = supportExpiryDate;
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
      buffer.append("uuid").append("='").append(getUuid()).append("' ");			
      buffer.append("comments").append("='").append(getComments()).append("' ");			
      buffer.append("active").append("='").append(isActive()).append("' ");			
      buffer.append("adminLicensesPurchased").append("='").append(getAdminLicensesPurchased()).append("' ");			
      buffer.append("playerLicensesPurchased").append("='").append(getPlayerLicensesPurchased()).append("' ");			
      buffer.append("supportExpiryDate").append("='").append(getSupportExpiryDate()).append("' ");			
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


