package com.intimate.rooms;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;

import com.intimate.owners.groups.RoomOwnerGroup;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

@RooJavaBean
@RooToString
@RooJson
@RooJpaActiveRecord
@RooWebJson(jsonObject = com.intimate.rooms.Room.class)
public class Room {
    @Id
    @Column(name = "id")
    private String id;

    private String name;

    @NotNull
    private String code;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private RoomOwnerGroup ownerGroup;
    
    public String toJson() {
        return new JSONSerializer().exclude("*.class").include("ownerGroup").deepSerialize(this);
    }

    public static com.intimate.rooms.Room fromJsonToRoom(String json) {
        return new JSONDeserializer<Room>().use(null, Room.class).use("ownerGroup", RoomOwnerGroup.class).deserialize(json);
    }

    public static String toJsonArray(Collection<com.intimate.rooms.Room> collection) {
        return new JSONSerializer().exclude("*.class").include("ownerGroup").deepSerialize(collection);
    }
}
