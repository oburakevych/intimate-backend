package com.intimate.rooms;

import java.util.Calendar;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.intimate.owners.Owner;
import com.intimate.owners.groups.RoomOwnerGroup;
import com.intimate.rooms.memberships.Membership;

@RooWebJson(jsonObject = Room.class)
@Controller
@RequestMapping("/rooms")
public class RoomController {
	protected Logger log = LoggerFactory.getLogger(getClass());
	
    @RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJson(
    		@RequestParam("username") String username,
    		@RequestParam("password") String password,
    		@RequestParam("invitedUser") String invitedUsername,
    		@RequestBody String json) {
    	
    	log.debug("Creating a new room for {}", username);
    	
    	Owner existingOwner = Owner.getOwner(username, password);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        
    	if (existingOwner == null) {
    		return new ResponseEntity<String>(headers, HttpStatus.UNAUTHORIZED);	
    	}

    	Membership ownerMembership = Membership.getMembershipByOwnerId(existingOwner.getId());
    	
    	if (ownerMembership == null) {
    		ownerMembership = new Membership(UUID.randomUUID().toString(), existingOwner.getId());
    		ownerMembership.persist();
    	}
    	
        RoomOwnerGroup roomGroup = new RoomOwnerGroup(UUID.randomUUID().toString());
        roomGroup.getMembers().add(ownerMembership);
        
        Owner existingCoowner = Owner.getOwner(invitedUsername);
    	
    	if (existingCoowner == null) {
    		//TODO: sent invitation
    	} else {
        	Membership coownerMembership = Membership.getMembershipByOwnerId(existingCoowner.getId());
        	
        	if (coownerMembership == null) {
        		coownerMembership = new Membership(UUID.randomUUID().toString(), existingCoowner.getId());
        		coownerMembership.persist();
        	}
        	
        	roomGroup.getMembers().add(coownerMembership);
    	}
    	
        Room room = Room.fromJsonToRoom(json);
        
        if (room.getId() == null) {
        	room.setId(UUID.randomUUID().toString());
        }
    	if (StringUtils.isBlank(room.getCode())) {
        	room.setCode(generateRoomCode());
        }
        
        room.setOwnerGroup(roomGroup);
        room.persist();
        
        log.debug("Returning: \n{}", room.toJson());

        return new ResponseEntity<String>(room.toJson(), headers, HttpStatus.CREATED);
    }
    
    private String generateRoomCode() {
    	Calendar  c = Calendar.getInstance();
    	String code = "" + c.get(Calendar.MINUTE) + "-" + c.get(Calendar.MILLISECOND);
    	
    	return code;
    }

}
