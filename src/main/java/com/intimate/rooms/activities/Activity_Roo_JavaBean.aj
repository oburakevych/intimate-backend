// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.intimate.rooms.activities;

import com.intimate.rooms.activities.Activity;
import com.intimate.rooms.activities.ActivityType;
import java.util.Date;

privileged aspect Activity_Roo_JavaBean {
    
    public String Activity.getId() {
        return this.id;
    }
    
    public void Activity.setId(String id) {
        this.id = id;
    }
    
    public ActivityType Activity.getType() {
        return this.type;
    }
    
    public void Activity.setType(ActivityType type) {
        this.type = type;
    }
    
    public Date Activity.getCreated() {
        return this.created;
    }
    
    public void Activity.setCreated(Date created) {
        this.created = created;
    }
    
    public String Activity.getOwnerId() {
        return this.ownerId;
    }
    
    public void Activity.setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
    
}
