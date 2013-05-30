// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.intimate.rooms.memberships;

import com.intimate.rooms.memberships.Membership;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

privileged aspect Membership_Roo_Json {
    
    public String Membership.toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }
    
    public static Membership Membership.fromJsonToMembership(String json) {
        return new JSONDeserializer<Membership>().use(null, Membership.class).deserialize(json);
    }
    
    public static String Membership.toJsonArray(Collection<Membership> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }
    
    public static Collection<Membership> Membership.fromJsonArrayToMemberships(String json) {
        return new JSONDeserializer<List<Membership>>().use(null, ArrayList.class).use("values", Membership.class).deserialize(json);
    }
    
}