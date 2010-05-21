package com.kesdip.business.domain.admin.generated;
// Generated 7 Μαϊ 2010 12:09:18 πμ by Hibernate Tools 3.2.0.b9


import java.util.HashSet;
import java.util.Set;

/**
 * 			Domain object for the 'User' entity. Auto-generated code.
 * 			<strong>Do not modify manually.</strong> @author
 * 			gerogias
 * 		
 */
public class User  implements java.io.Serializable {


     /**
      * 				Primary, natural key.
 * 			
     */
     private String username;
     /**
      * 				Tha user's password.
 * 			
     */
     private String password;
     /**
      * 				Tha user's first name.
 * 			
     */
     private String firstName;
     /**
      * 				Tha user's last name.
 * 			
     */
     private String lastName;
     /**
      * The associated customer, if any.
     */
     private Customer affiliation;
     /**
      * 				The rights of this user.
 * 			
     */
     private Set<AccessRight> rights = new HashSet<AccessRight>(0);
     /**
      * 				The acessible installations for this user.
 * 			
     */
     private Set<AccessControl> accessibleInstallations = new HashSet<AccessControl>(0);

    public User() {
    }

	
    public User(String username, String password, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    public User(String username, String password, String firstName, String lastName, Customer affiliation, Set<AccessRight> rights, Set<AccessControl> accessibleInstallations) {
       this.username = username;
       this.password = password;
       this.firstName = firstName;
       this.lastName = lastName;
       this.affiliation = affiliation;
       this.rights = rights;
       this.accessibleInstallations = accessibleInstallations;
    }
   
    /**       
     *      * 				Primary, natural key.
     * 			
     */
    public String getUsername() {
        return this.username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    /**       
     *      * 				Tha user's password.
     * 			
     */
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    /**       
     *      * 				Tha user's first name.
     * 			
     */
    public String getFirstName() {
        return this.firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    /**       
     *      * 				Tha user's last name.
     * 			
     */
    public String getLastName() {
        return this.lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    /**       
     *      * The associated customer, if any.
     */
    public Customer getAffiliation() {
        return this.affiliation;
    }
    
    public void setAffiliation(Customer affiliation) {
        this.affiliation = affiliation;
    }
    /**       
     *      * 				The rights of this user.
     * 			
     */
    public Set<AccessRight> getRights() {
        return this.rights;
    }
    
    public void setRights(Set<AccessRight> rights) {
        this.rights = rights;
    }
    /**       
     *      * 				The acessible installations for this user.
     * 			
     */
    public Set<AccessControl> getAccessibleInstallations() {
        return this.accessibleInstallations;
    }
    
    public void setAccessibleInstallations(Set<AccessControl> accessibleInstallations) {
        this.accessibleInstallations = accessibleInstallations;
    }

    /**
     * toString
     * @return String
     */
     public String toString() {
	  StringBuffer buffer = new StringBuffer();

      buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
      buffer.append("username").append("='").append(getUsername()).append("' ");			
      buffer.append("password").append("='").append(getPassword()).append("' ");			
      buffer.append("firstName").append("='").append(getFirstName()).append("' ");			
      buffer.append("lastName").append("='").append(getLastName()).append("' ");			
      buffer.append("]");
      
      return buffer.toString();
     }

   public boolean equals(Object other) {
         if ( (this == other ) ) return true;
		 if ( (other == null ) ) return false;
		 if ( !(other instanceof User) ) return false;
		 User castOther = ( User ) other; 
         
		 return ( (this.getUsername()==castOther.getUsername()) || ( this.getUsername()!=null && castOther.getUsername()!=null && this.getUsername().equals(castOther.getUsername()) ) );
   }
   
   public int hashCode() {
         int result = 17;
         
         result = 37 * result + ( getUsername() == null ? 0 : this.getUsername().hashCode() );
         
         
         
         
         
         
         return result;
   }   


}


