package com.kesdip.business.domain.generated;
// Generated 23 …бн 2009 2:21:24 рм by Hibernate Tools 3.2.0.b9


import java.util.HashSet;
import java.util.Set;

/**
 * 			Domain object for the 'Content' entity. Auto-generated
 * 			code. <strong>Do not modify manually.</strong>
 * 			@author gerogias
 * 		
 */
public class Content  implements java.io.Serializable {


     /**
      * 				Primary, surrogate key.
 * 			
     */
     private Long id;
     /**
      * 				The URL of the content file.
 * 			
     */
     private String url;
     /**
      *         		CRC for the content file.
 *         	
     */
     private String crc;
     /**
      *         		Local file for the content.
 *         	
     */
     private String localFile;
     /**
      * 				The deployments of this content.
 * 			
     */
     private Set<Deployment> deployments = new HashSet<Deployment>(0);

    public Content() {
    }

	
    public Content(String url) {
        this.url = url;
    }
    public Content(String url, String crc, String localFile, Set<Deployment> deployments) {
       this.url = url;
       this.crc = crc;
       this.localFile = localFile;
       this.deployments = deployments;
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
     *      * 				The URL of the content file.
     * 			
     */
    public String getUrl() {
        return this.url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    /**       
     *      *         		CRC for the content file.
     *         	
     */
    public String getCrc() {
        return this.crc;
    }
    
    public void setCrc(String crc) {
        this.crc = crc;
    }
    /**       
     *      *         		Local file for the content.
     *         	
     */
    public String getLocalFile() {
        return this.localFile;
    }
    
    public void setLocalFile(String localFile) {
        this.localFile = localFile;
    }
    /**       
     *      * 				The deployments of this content.
     * 			
     */
    public Set<Deployment> getDeployments() {
        return this.deployments;
    }
    
    public void setDeployments(Set<Deployment> deployments) {
        this.deployments = deployments;
    }

    /**
     * toString
     * @return String
     */
     public String toString() {
	  StringBuffer buffer = new StringBuffer();

      buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
      buffer.append("id").append("='").append(getId()).append("' ");			
      buffer.append("url").append("='").append(getUrl()).append("' ");			
      buffer.append("crc").append("='").append(getCrc()).append("' ");			
      buffer.append("localFile").append("='").append(getLocalFile()).append("' ");			
      buffer.append("]");
      
      return buffer.toString();
     }

   public boolean equals(Object other) {
         if ( (this == other ) ) return true;
		 if ( (other == null ) ) return false;
		 if ( !(other instanceof Content) ) return false;
		 Content castOther = ( Content ) other; 
         
		 return ( (this.getId()==castOther.getId()) || ( this.getId()!=null && castOther.getId()!=null && this.getId().equals(castOther.getId()) ) );
   }
   
   public int hashCode() {
         int result = 17;
         
         result = 37 * result + ( getId() == null ? 0 : this.getId().hashCode() );
         
         
         
         
         return result;
   }   


}


