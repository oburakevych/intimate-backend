package com.intimate.rooms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;

import com.intimate.owners.groups.RoomOwnerGroup;
import com.intimate.rooms.activities.Activity;
import com.intimate.rooms.memberships.Membership;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

@RooJavaBean
@RooToString
@RooJson
@RooWebJson(jsonObject = com.intimate.rooms.Room.class)
@RooJpaActiveRecord
public class Room {
	protected static Logger log = LoggerFactory.getLogger(Room.class);
	
    @Id
    @Column(name = "id")
    private String id;

    private String name;

    @NotNull
    private String code;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private RoomOwnerGroup ownerGroup;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Activity> activities = new ArrayList<Activity>();

    public static Room getRoomForCode(String memberId, String roomCode) {
    	Room room = findRoomsByCodeEquals(roomCode).getSingleResult();
    	
    	if (room != null) {
    		List<Membership> memberships = room.getOwnerGroup().getMembers();
    		
    		if (CollectionUtils.isNotEmpty(memberships)) {
    			for (Membership membership : memberships) {
    				if (memberId.equals(membership.getOwnerId())) {
    					log.info("User {} is a valid member of room with code {}", memberId, roomCode);
    					
    					return room;
    				}
    			}
    		}
    	}
    	
    	return null;
    }
    
    public static TypedQuery<Room> findRoomsByCodeEquals(String code) {
        if (code == null || code.length() == 0) throw new IllegalArgumentException("The code argument is required");
        EntityManager em = entityManager();
        TypedQuery<Room> q = em.createQuery("SELECT o FROM Room AS o WHERE o.code = :code", Room.class);
        q.setParameter("code", code);
        return q;
    }

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
