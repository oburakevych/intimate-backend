package com.intimate.rooms;

import com.intimate.owners.groups.RoomOwnerGroup;
import com.intimate.rooms.activities.Activity;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;

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

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Activity> activities = new HashSet<Activity>();

    public String toJson() {
        return new JSONSerializer().exclude("*.class").include("ownerGroup", "activities").deepSerialize(this);
    }

    public static com.intimate.rooms.Room fromJsonToRoom(String json) {
        return new JSONDeserializer<Room>().use(null, Room.class).use("ownerGroup", RoomOwnerGroup.class).deserialize(json);
    }

    public static String toJsonArray(Collection<com.intimate.rooms.Room> collection) {
        return new JSONSerializer().exclude("*.class").include("ownerGroup").deepSerialize(collection);
    }
}
