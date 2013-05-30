package com.intimate.owners.groups;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

import com.intimate.rooms.memberships.Membership;

import flexjson.JSONSerializer;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
@RooJson
public class RoomOwnerGroup {

    @Id
    @Column(name = "id")
    private String id;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Membership> members = new ArrayList<Membership>(2);

    public RoomOwnerGroup() {
    }

    public RoomOwnerGroup(String id) {
        this.id = id;
    }
    
    public String toJson() {
        return new JSONSerializer().exclude("*.class").include("members").deepSerialize(this);
    }

}
