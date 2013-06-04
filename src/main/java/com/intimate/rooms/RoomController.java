package com.intimate.rooms;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.NoResultException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.intimate.owners.Owner;
import com.intimate.owners.groups.RoomOwnerGroup;
import com.intimate.rooms.activities.Activity;
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
    	
    	Owner authorisedOwner = null;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        
    	try {
    		authorisedOwner = Owner.getOwner(username, password);
		} catch (NoResultException e) {
			log.error("User {} with password {} cannot be authenticated");
			
			return new ResponseEntity<String>(headers, HttpStatus.UNAUTHORIZED);	
		}

    	Membership ownerMembership = Membership.getMembershipByOwnerId(authorisedOwner.getId());
    	
    	if (ownerMembership == null) {
    		ownerMembership = new Membership(UUID.randomUUID().toString(), authorisedOwner.getId());
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

    @RequestMapping(value = "{roomCode}", headers = "Accept=application/json")
    public ResponseEntity<String> showJson(
    		@RequestParam("username") String username,
    		@RequestParam("password") String password,
    		@PathVariable("roomCode") String roomCode) {
    	
    	log.debug("Opening room for {} with room code {}", username, roomCode);
    	
    	Owner authorisedOwner = null;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        
    	try {
    		authorisedOwner = Owner.getOwner(username, password);
		} catch (NoResultException e) {
			log.error("User {} with password {} cannot be authenticated");
			
			return new ResponseEntity<String>(headers, HttpStatus.UNAUTHORIZED);	
		}
    	
    	Room room = null;  
    	try {
			room = Room.getRoomForCode(authorisedOwner.getId(), roomCode);
		} catch (NoResultException e) {
			log.error("Invalid code {}", roomCode);
			return new ResponseEntity<String>("Invalid room code", headers, HttpStatus.NOT_FOUND);	
		}
        
    	if (room == null) {
    		log.error("User {} is not a member of the room with code {}", authorisedOwner.getId(), roomCode);
    		return new ResponseEntity<String>(headers, HttpStatus.FORBIDDEN);
    	}
    	
        log.debug("Returning: \n{}", room.toJson());

        return new ResponseEntity<String>(room.toJson(), headers, HttpStatus.OK);
    }

    
    @RequestMapping(value="{roomId}/activities", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createActivityFromJson(
    		@RequestParam("username") String username,
    		@RequestParam("password") String password,
    		@PathVariable("roomId") String roomId,
    		@RequestBody String json) {
    	log.debug("Request to create new activity for room {}", roomId);
    	
    	Owner authorisedOwner = null;
    	
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        
    	try {
    		authorisedOwner = Owner.getOwner(username, password);
		} catch (NoResultException e) {
			log.error("User {} with password {} cannot be authenticated", username, password);
			
			return new ResponseEntity<String>(headers, HttpStatus.UNAUTHORIZED);	
		}
    	
    	Activity activity = Activity.fromJsonToActivity(json);
    	
    	if (activity.getId() == null) {
    		activity.setId(UUID.randomUUID().toString());
    	}
    	
    	if (activity.getContent() != null) {
    		if (activity.getContent().getId() == null) {
    			activity.getContent().setId(UUID.randomUUID().toString());
    		}
    	}
    	
    	Room room = Room.findRoom(roomId);
    	
    	if (room == null) {
			log.error("Room {} does not exists", roomId);
			
			return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);    		
    	}
    	
    	log.debug("Found {} activities in the room {}", room.getActivities().size(), roomId);
    	
    	room.getActivities().add(activity);
    	
    	//activity.persist();
    	room.merge();
    	
    	return new ResponseEntity<String>(headers, HttpStatus.CREATED);
	}
    
    @RequestMapping(value="{roomId}/activities", headers = "Accept=application/json")
    public ResponseEntity<String> getActivities(
    		@RequestParam("username") String username,
    		@RequestParam("password") String password,
    		@PathVariable("roomId") String roomId,
    		@RequestParam(value = "fromDate", required = false) Date fromDate,
    		@RequestParam(value = "toDate", required = false) Date toDate) {
    	log.debug("Request to create new activity for room {}", roomId);
    
    	Room room = Room.findRoom(roomId);
    	
    	List<Activity> activities = room.getActivities();
    	
    	log.debug("Found {} activities", activities.size());
    	
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
    	
    	return new ResponseEntity<String>(Activity.toJsonArray(activities), headers, HttpStatus.OK);
    }

    
    private String generateRoomCode() {
    	Calendar  c = Calendar.getInstance();
    	String code = "" + c.get(Calendar.MINUTE) + "-" + c.get(Calendar.MILLISECOND);
    	
    	return code;
    }

}
