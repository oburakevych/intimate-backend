// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.intimate.rooms;

import com.intimate.owners.groups.RoomOwnerGroup;
import com.intimate.rooms.Room;
import com.intimate.rooms.activities.Activity;
import java.util.Set;

privileged aspect Room_Roo_JavaBean {
    
    public String Room.getId() {
        return this.id;
    }
    
    public void Room.setId(String id) {
        this.id = id;
    }
    
    public String Room.getName() {
        return this.name;
    }
    
    public void Room.setName(String name) {
        this.name = name;
    }
    
    public String Room.getCode() {
        return this.code;
    }
    
    public void Room.setCode(String code) {
        this.code = code;
    }
    
    public RoomOwnerGroup Room.getOwnerGroup() {
        return this.ownerGroup;
    }
    
    public void Room.setOwnerGroup(RoomOwnerGroup ownerGroup) {
        this.ownerGroup = ownerGroup;
    }
    
    public Set<Activity> Room.getActivities() {
        return this.activities;
    }
    
    public void Room.setActivities(Set<Activity> activities) {
        this.activities = activities;
    }
    
}
