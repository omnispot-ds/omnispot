package com.kesdip.business.domain.admin.generated;
// Generated 7 Μαϊ 2010 12:09:18 πμ by Hibernate Tools 3.2.0.b9


import java.util.HashSet;
import java.util.Set;

/**
 * 			Domain object for the 'Deployment' entity. Auto-generated
 * 			code. <strong>Do not modify manually.</strong>
 * 			@author gerogias
 * 		
 */
public class Deployment  implements java.io.Serializable {


     /**
      * 				Primary, surrogate key.
 * 			
     */
     private Long id;
     /**
      * 				The URL of the deployment file.
 * 			
     */
     private String url;
     /**
      * 				The name of this deployment.
 * 			
     */
     private String name;
     /**
      *         		CRC for the deployment file.
 *         	
     */
     private String crc;
     /**
      *         		Local file for the deployment.
 *         	
     */
     private String localFile;
     /**
      * 				Status of the deployment.
 * 			
     */
     private short status;
     /**
      * 				The number of retries to download this deployment.
 * 			
     */
     private int retries;
     /**
      * 				The content files of this deployment.
 * 			
     */
     private Set<Content> contentFiles = new HashSet<Content>(0);
     /**
      * 				The installation of this deployment.
 * 			
     */
     private Set<Installation> installations = new HashSet<Installation>(0);

    public Deployment() {
    }

	
    public Deployment(String url, String name, short status, int retries) {
        this.url = url;
        this.name = name;
        this.status = status;
        this.retries = retries;
    }
    public Deployment(String url, String name, String crc, String localFile, short status, int retries, Set<Content> contentFiles, Set<Installation> installations) {
       this.url = url;
       this.name = name;
       this.crc = crc;
       this.localFile = localFile;
       this.status = status;
       this.retries = retries;
       this.contentFiles = contentFiles;
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
     *      * 				The URL of the deployment file.
     * 			
     */
    public String getUrl() {
        return this.url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    /**       
     *      * 				The name of this deployment.
     * 			
     */
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    /**       
     *      *         		CRC for the deployment file.
     *         	
     */
    public String getCrc() {
        return this.crc;
    }
    
    public void setCrc(String crc) {
        this.crc = crc;
    }
    /**       
     *      *         		Local file for the deployment.
     *         	
     */
    public String getLocalFile() {
        return this.localFile;
    }
    
    public void setLocalFile(String localFile) {
        this.localFile = localFile;
    }
    /**       
     *      * 				Status of the deployment.
     * 			
     */
    public short getStatus() {
        return this.status;
    }
    
    public void setStatus(short status) {
        this.status = status;
    }
    /**       
     *      * 				The number of retries to download this deployment.
     * 			
     */
    public int getRetries() {
        return this.retries;
    }
    
    public void setRetries(int retries) {
        this.retries = retries;
    }
    /**       
     *      * 				The content files of this deployment.
     * 			
     */
    public Set<Content> getContentFiles() {
        return this.contentFiles;
    }
    
    public void setContentFiles(Set<Content> contentFiles) {
        this.contentFiles = contentFiles;
    }
    /**       
     *      * 				The installation of this deployment.
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
      buffer.append("url").append("='").append(getUrl()).append("' ");			
      buffer.append("name").append("='").append(getName()).append("' ");			
      buffer.append("crc").append("='").append(getCrc()).append("' ");			
      buffer.append("localFile").append("='").append(getLocalFile()).append("' ");			
      buffer.append("status").append("='").append(getStatus()).append("' ");			
      buffer.append("retries").append("='").append(getRetries()).append("' ");			
      buffer.append("]");
      
      return buffer.toString();
     }

   public boolean equals(Object other) {
         if ( (this == other ) ) return true;
		 if ( (other == null ) ) return false;
		 if ( !(other instanceof Deployment) ) return false;
		 Deployment castOther = ( Deployment ) other; 
         
		 return ( (this.getId()==castOther.getId()) || ( this.getId()!=null && castOther.getId()!=null && this.getId().equals(castOther.getId()) ) );
   }
   
   public int hashCode() {
         int result = 17;
         
         result = 37 * result + ( getId() == null ? 0 : this.getId().hashCode() );
         
         
         
         
         
         
         
         
         return result;
   }   


}


