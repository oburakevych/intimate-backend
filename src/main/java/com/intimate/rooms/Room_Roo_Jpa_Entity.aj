// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.intimate.rooms;

import com.intimate.rooms.Room;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Version;

privileged aspect Room_Roo_Jpa_Entity {
    
    declare @type: Room: @Entity;
    
    @Version
    @Column(name = "version")
    private Integer Room.version;
    
    public Integer Room.getVersion() {
        return this.version;
    }
    
    public void Room.setVersion(Integer version) {
        this.version = version;
    }
    
}
