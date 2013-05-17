package com.intimate.owners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.intimate.security.SecurityService;

@RooWebJson(jsonObject = Owner.class)
@Controller
@RequestMapping("/owners")
public class OwnerController {
	@Autowired
	SecurityService securityService;
	
    @RequestMapping(value = "/signup", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<java.lang.String> signup(@RequestBody String json) {
        Owner owner = Owner.fromJsonToOwner(json);
        owner.persist();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<java.lang.String> login(@RequestBody String json) {
        Owner owner = Owner.fromJsonToOwner(json);
        //Owner existingOwner = Owner.getOwner(owner.getUsername(), owner.getPassword());
        
        System.out.println("HERE I AM");
        
        Owner existingOwner = securityService.getAuthornameisedOwner(owner.getUsername(), owner.getPassword());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (existingOwner == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(existingOwner.toJson(), headers, HttpStatus.OK);
    }
}
