package com.kesdip.business.domain.admin.generated;
// Generated 7 Μαϊ 2010 12:09:18 πμ by Hibernate Tools 3.2.0.b9


import java.util.HashSet;
import java.util.Set;

/**
 * 			Domain object for the 'Installation' entity. Auto-generated code.
 * 			<strong>Do not modify manually.</strong> @author
 * 			gerogias
 * 		
 */
public class Installation  implements java.io.Serializable {


     /**
      * 				Primary, surrogate key.
 * 			
     */
     private Long id;
     /**
      * 				Tha name of the installation.
 * 			
     */
     private String name;
     /**
      * 				Tha UUID of the installation.
 * 			
     */
     private String uuid;
     /**
      * 				The last known IP of the installation.
 * 			
     */
     private String lastKnownIP;
     /**
      * 				Tha type of the screen.
 * 			
     */
     private String screenType;
     /**
      * Comments for this installation.
     */
     private String comments;
     /**
      * 				If the installation active or not.
 * 			
     */
     private boolean active;
     /**
      * 				The current status of the installation.
 * 				@see IInstallationStatus
 * 			
     */
     private short currentStatus;
     /**
      * 				The users able to access this installation via ACLs.
 * 			
     */
     private Set<AccessControl> accessingUsers = new HashSet<AccessControl>(0);
     /**
      * 				The status change history for this installation.
 * 			
     */
     private Set<StatusEntry> statusHistory = new HashSet<StatusEntry>(0);
     /**
      * 				The pending actions for this installation.
 * 			
     */
     private Set<Action> pendingActions = new HashSet<Action>(0);
     /**
      * 				The deployments at this installation.
 * 			
     */
     private Set<Deployment> deployments = new HashSet<Deployment>(0);
     /**
      * The parent site.
     */
     private Site site;
     /**
      * 				The groups of the installation.
 * 			
     */
     private Set<InstallationGroup> groups = new HashSet<InstallationGroup>(0);

    public Installation() {
    }

	
    public Installation(String name, String uuid, boolean active, short currentStatus, Site site) {
        this.name = name;
        this.uuid = uuid;
        this.active = active;
        this.currentStatus = currentStatus;
        this.site = site;
    }
    public Installation(String name, String uuid, String lastKnownIP, String screenType, String comments, boolean active, short currentStatus, Set<AccessControl> accessingUsers, Set<StatusEntry> statusHistory, Set<Action> pendingActions, Set<Deployment> deployments, Site site, Set<InstallationGroup> groups) {
       this.name = name;
       this.uuid = uuid;
       this.lastKnownIP = lastKnownIP;
       this.screenType = screenType;
       this.comments = comments;
       this.active = active;
       this.currentStatus = currentStatus;
       this.accessingUsers = accessingUsers;
       this.statusHistory = statusHistory;
       this.pendingActions = pendingActions;
       this.deployments = deployments;
       this.site = site;
       this.groups = groups;
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
     *      * 				Tha name of the installation.
     * 			
     */
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    /**       
     *      * 				Tha UUID of the installation.
     * 			
     */
    public String getUuid() {
        return this.uuid;
    }
    
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    /**       
     *      * 				The last known IP of the installation.
     * 			
     */
    public String getLastKnownIP() {
        return this.lastKnownIP;
    }
    
    public void setLastKnownIP(String lastKnownIP) {
        this.lastKnownIP = lastKnownIP;
    }
    /**       
     *      * 				Tha type of the screen.
     * 			
     */
    public String getScreenType() {
        return this.screenType;
    }
    
    public void setScreenType(String screenType) {
        this.screenType = screenType;
    }
    /**       
     *      * Comments for this installation.
     */
    public String getComments() {
        return this.comments;
    }
    
    public void setComments(String comments) {
        this.comments = comments;
    }
    /**       
     *      * 				If the installation active or not.
     * 			
     */
    public boolean isActive() {
        return this.active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    /**       
     *      * 				The current status of the installation.
     * 				@see IInstallationStatus
     * 			
     */
    public short getCurrentStatus() {
        return this.currentStatus;
    }
    
    public void setCurrentStatus(short currentStatus) {
        this.currentStatus = currentStatus;
    }
    /**       
     *      * 				The users able to access this installation via ACLs.
     * 			
     */
    public Set<AccessControl> getAccessingUsers() {
        return this.accessingUsers;
    }
    
    public void setAccessingUsers(Set<AccessControl> accessingUsers) {
        this.accessingUsers = accessingUsers;
    }
    /**       
     *      * 				The status change history for this installation.
     * 			
     */
    public Set<StatusEntry> getStatusHistory() {
        return this.statusHistory;
    }
    
    public void setStatusHistory(Set<StatusEntry> statusHistory) {
        this.statusHistory = statusHistory;
    }
    /**       
     *      * 				The pending actions for this installation.
     * 			
     */
    public Set<Action> getPendingActions() {
        return this.pendingActions;
    }
    
    public void setPendingActions(Set<Action> pendingActions) {
        this.pendingActions = pendingActions;
    }
    /**       
     *      * 				The deployments at this installation.
     * 			
     */
    public Set<Deployment> getDeployments() {
        return this.deployments;
    }
    
    public void setDeployments(Set<Deployment> deployments) {
        this.deployments = deployments;
    }
    /**       
     *      * The parent site.
     */
    public Site getSite() {
        return this.site;
    }
    
    public void setSite(Site site) {
        this.site = site;
    }
    /**       
     *      * 				The groups of the installation.
     * 			
     */
    public Set<InstallationGroup> getGroups() {
        return this.groups;
    }
    
    public void setGroups(Set<InstallationGroup> groups) {
        this.groups = groups;
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
      buffer.append("lastKnownIP").append("='").append(getLastKnownIP()).append("' ");			
      buffer.append("screenType").append("='").append(getScreenType()).append("' ");			
      buffer.append("comments").append("='").append(getComments()).append("' ");			
      buffer.append("active").append("='").append(isActive()).append("' ");			
      buffer.append("currentStatus").append("='").append(getCurrentStatus()).append("' ");			
      buffer.append("site").append("='").append(getSite()).append("' ");			
      buffer.append("]");
      
      return buffer.toString();
     }

   public boolean equals(Object other) {
         if ( (this == other ) ) return true;
		 if ( (other == null ) ) return false;
		 if ( !(other instanceof Installation) ) return false;
		 Installation castOther = ( Installation ) other; 
         
		 return ( (this.getId()==castOther.getId()) || ( this.getId()!=null && castOther.getId()!=null && this.getId().equals(castOther.getId()) ) );
   }
   
   public int hashCode() {
         int result = 17;
         
         result = 37 * result + ( getId() == null ? 0 : this.getId().hashCode() );
         
         
         
         
         
         
         
         
         
         
         
         
         
         return result;
   }   

  // The following is extra code specified in the hbm.xml files

			
		/**
		 * Latest printscreen.
		 */
		private transient com.kesdip.business.beans.PrintScreen printScreen = null;
		
		public void setPrintScreen(com.kesdip.business.beans.PrintScreen printScreen) {
			this.printScreen = printScreen;
		}

		public com.kesdip.business.beans.PrintScreen getPrintScreen() {
			return this.printScreen;
		}
		
		
  // end of extra code specified in the hbm.xml files

}


