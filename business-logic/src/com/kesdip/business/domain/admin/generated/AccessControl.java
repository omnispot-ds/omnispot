package com.kesdip.business.domain.admin.generated;
// Generated 7 Μαϊ 2010 12:09:18 πμ by Hibernate Tools 3.2.0.b9



/**
 * 			Domain object for the 'AccessControl' entity. Auto-generated code.
 * 			<strong>Do not modify manually.</strong> @author
 * 			gerogias
 * 		
 */
public class AccessControl  implements java.io.Serializable {


     /**
      * 				Primary, surrogate key.
 * 			
     */
     private Long id;
     /**
      * 				Tha type of the ACL.
 * 			
     */
     private short type;
     /**
      * The parent user.
     */
     private User user;
     /**
      * The installation.
     */
     private Installation installation;

    public AccessControl() {
    }

    public AccessControl(short type, User user, Installation installation) {
       this.type = type;
       this.user = user;
       this.installation = installation;
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
     *      * 				Tha type of the ACL.
     * 			
     */
    public short getType() {
        return this.type;
    }
    
    public void setType(short type) {
        this.type = type;
    }
    /**       
     *      * The parent user.
     */
    public User getUser() {
        return this.user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    /**       
     *      * The installation.
     */
    public Installation getInstallation() {
        return this.installation;
    }
    
    public void setInstallation(Installation installation) {
        this.installation = installation;
    }

    /**
     * toString
     * @return String
     */
     public String toString() {
	  StringBuffer buffer = new StringBuffer();

      buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
      buffer.append("id").append("='").append(getId()).append("' ");			
      buffer.append("type").append("='").append(getType()).append("' ");			
      buffer.append("user").append("='").append(getUser()).append("' ");			
      buffer.append("installation").append("='").append(getInstallation()).append("' ");			
      buffer.append("]");
      
      return buffer.toString();
     }

   public boolean equals(Object other) {
         if ( (this == other ) ) return true;
		 if ( (other == null ) ) return false;
		 if ( !(other instanceof AccessControl) ) return false;
		 AccessControl castOther = ( AccessControl ) other; 
         
		 return ( (this.getId()==castOther.getId()) || ( this.getId()!=null && castOther.getId()!=null && this.getId().equals(castOther.getId()) ) );
   }
   
   public int hashCode() {
         int result = 17;
         
         result = 37 * result + ( getId() == null ? 0 : this.getId().hashCode() );
         
         
         
         return result;
   }   


}


