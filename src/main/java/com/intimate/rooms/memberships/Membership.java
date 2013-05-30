package com.intimate.rooms.memberships;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJson
@RooJpaActiveRecord(finders = { "findMembershipsByOwnerIdEquals" })
public class Membership {
	public Membership() {}
	public Membership(String id, String ownerId) {
		this.id = id;
		this.ownerId = ownerId;
	}

    @Id
    @Column(name = "id")
    private String id;

    @NotNull
    private String ownerId;
    
    public static Membership getMembershipByOwnerId(String ownerId) {
    	 List<Membership> memberships = findMembershipsByOwnerIdEquals(ownerId).getResultList();
    	 
    	 if (memberships.size() > 0) {
    		 return memberships.get(0);
    	 }
    	 
    	 return null;
    }
}
