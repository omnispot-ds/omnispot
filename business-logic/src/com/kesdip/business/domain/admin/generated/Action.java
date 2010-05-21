package com.kesdip.business.domain.admin.generated;
// Generated 7 Μαϊ 2010 12:09:18 πμ by Hibernate Tools 3.2.0.b9


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 			Domain object for the 'Action' entity. Auto-generated
 * 			code. <strong>Do not modify manually.</strong>
 * 			@author gerogias
 * 		
 */
public class Action  implements java.io.Serializable {


     /**
      * 				Primary, surrogate key.
 * 			
     */
     private Long id;
     /**
      * 				The id of the action.
 * 			
     */
     private String actionId;
     /**
      *         		The type of the action.
 *         		@see com.kesdip.business.constenum.IActionTypesEnum
 *         	
     */
     private short type;
     /**
      *         		The status of the action.
 *         		@see com.kesdip.business.constenum.IActionStatusEnum
 *         	
     */
     private short status;
     /**
      *         		When was the action added.
 *         	
     */
     private Date dateAdded;
     /**
      * 				The message of the action in case of error.
 * 			
     */
     private String message;
     /**
      * 				The parameters of the action.
 * 			
     */
     private Set<Parameter> parameters = new HashSet<Parameter>(0);
     /**
      * The installation this action belongs to.
     */
     private Installation installation;

    public Action() {
    }

	
    public Action(String actionId, short type, short status, Date dateAdded) {
        this.actionId = actionId;
        this.type = type;
        this.status = status;
        this.dateAdded = dateAdded;
    }
    public Action(String actionId, short type, short status, Date dateAdded, String message, Set<Parameter> parameters, Installation installation) {
       this.actionId = actionId;
       this.type = type;
       this.status = status;
       this.dateAdded = dateAdded;
       this.message = message;
       this.parameters = parameters;
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
     *      * 				The id of the action.
     * 			
     */
    public String getActionId() {
        return this.actionId;
    }
    
    public void setActionId(String actionId) {
        this.actionId = actionId;
    }
    /**       
     *      *         		The type of the action.
     *         		@see com.kesdip.business.constenum.IActionTypesEnum
     *         	
     */
    public short getType() {
        return this.type;
    }
    
    public void setType(short type) {
        this.type = type;
    }
    /**       
     *      *         		The status of the action.
     *         		@see com.kesdip.business.constenum.IActionStatusEnum
     *         	
     */
    public short getStatus() {
        return this.status;
    }
    
    public void setStatus(short status) {
        this.status = status;
    }
    /**       
     *      *         		When was the action added.
     *         	
     */
    public Date getDateAdded() {
        return this.dateAdded;
    }
    
    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }
    /**       
     *      * 				The message of the action in case of error.
     * 			
     */
    public String getMessage() {
        return this.message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    /**       
     *      * 				The parameters of the action.
     * 			
     */
    public Set<Parameter> getParameters() {
        return this.parameters;
    }
    
    public void setParameters(Set<Parameter> parameters) {
        this.parameters = parameters;
    }
    /**       
     *      * The installation this action belongs to.
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
      buffer.append("actionId").append("='").append(getActionId()).append("' ");			
      buffer.append("type").append("='").append(getType()).append("' ");			
      buffer.append("status").append("='").append(getStatus()).append("' ");			
      buffer.append("dateAdded").append("='").append(getDateAdded()).append("' ");			
      buffer.append("message").append("='").append(getMessage()).append("' ");			
      buffer.append("parameters").append("='").append(getParameters()).append("' ");			
      buffer.append("installation").append("='").append(getInstallation()).append("' ");			
      buffer.append("]");
      
      return buffer.toString();
     }

   public boolean equals(Object other) {
         if ( (this == other ) ) return true;
		 if ( (other == null ) ) return false;
		 if ( !(other instanceof Action) ) return false;
		 Action castOther = ( Action ) other; 
         
		 return ( (this.getId()==castOther.getId()) || ( this.getId()!=null && castOther.getId()!=null && this.getId().equals(castOther.getId()) ) );
   }
   
   public int hashCode() {
         int result = 17;
         
         result = 37 * result + ( getId() == null ? 0 : this.getId().hashCode() );
         
         
         
         
         
         
         
         return result;
   }   

  // The following is extra code specified in the hbm.xml files

			
			
		/**
		 * @return String the date, formatted as a string or null
		 * @see gr.panouepe.monitor.common.util.DateUtils#DATE_FORMAT
		 */
		public String getDateString() {
			if (dateAdded == null) {
				return null;
			}
			return new java.text.SimpleDateFormat(
			com.kesdip.common.util.DateUtils.DATE_TIME_FORMAT).format(dateAdded);
		}
		
		
  // end of extra code specified in the hbm.xml files

}


