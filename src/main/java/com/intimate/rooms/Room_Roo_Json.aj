// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.intimate.rooms;

import com.intimate.rooms.Room;
import flexjson.JSONDeserializer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

privileged aspect Room_Roo_Json {
    
    public static Collection<Room> Room.fromJsonArrayToRooms(String json) {
        return new JSONDeserializer<List<Room>>().use(null, ArrayList.class).use("values", Room.class).deserialize(json);
    }
    
}
