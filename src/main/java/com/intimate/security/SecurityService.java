package com.intimate.security;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javassist.bytecode.ByteArray;

import org.springframework.stereotype.Service;

import com.intimate.owners.Owner;

@Service
public class SecurityService {
	private static final Map<String, UUID> usernameIdMap = new HashMap<String, UUID>();
	
	static {
		usernameIdMap.put("abu@abu.com", UUID.fromString("2b54cc5c-ce29-4de4-92fa-bc6e848a1629"));
		usernameIdMap.put("gang@gang.com", UUID.fromString("5d3c1b93-b4e2-45f1-bba5-bd17408bbeb8"));
		usernameIdMap.put("test@test.com", UUID.fromString("f65eef8b-5a41-4cdd-a88c-b0e7c97169b6"));
		usernameIdMap.put("test@intimate.com", UUID.fromString("4e197929-0ad0-4a39-a67b-1bb288756d0a"));
	}
	
	public static void main(String[] args) {
		for (Entry<String, UUID> entry : usernameIdMap.entrySet()) {
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}
		
		String s = "afdkalsdflkdaskf;las;lfd;sa.f.,;ld,sflkds;l;adsdkvgds8lf,v  l, lgfkogkrkgdlfsjkjfslk,flds,vfds ddfgg";
		
		byte[] bytes = s.getBytes();
		
		System.out.println("length: " + bytes.length);
	}
	
	public Owner getAuthornameisedOwner(String username, String password) {
		//Owner existingOwner = Owner.getOwner(username, password);
		UUID id = usernameIdMap.get(username);
		
		if (id == null) {
			return null;
		}
		
		Owner owner = new Owner(id);
		owner.setEmail(username);
		owner.setPassword(password);
		owner.setVersion(0);
		owner.setUsername(username);
		
		return owner;
	}
}
