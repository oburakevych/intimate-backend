// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.intimate.rooms.activities.contents;

import com.intimate.rooms.activities.contents.Content;
import java.util.Date;

privileged aspect Content_Roo_JavaBean {
    
    public String Content.getId() {
        return this.id;
    }
    
    public void Content.setId(String id) {
        this.id = id;
    }
    
    public Date Content.getCreated() {
        return this.created;
    }
    
    public void Content.setCreated(Date created) {
        this.created = created;
    }
    
    public String Content.getBody() {
        return this.body;
    }
    
    public void Content.setBody(String body) {
        this.body = body;
    }
    
    public String Content.getDescription() {
        return this.description;
    }
    
    public void Content.setDescription(String description) {
        this.description = description;
    }
    
}