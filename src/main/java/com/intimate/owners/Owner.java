package com.intimate.owners;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
@RooJson
public class Owner {
	public Owner() {
		this.id = UUID.randomUUID().toString();
	}
	
	public Owner(UUID id) {
		this.id = id.toString();
	}

    @Id
    @Column(name = "id")
    private String id;

    @Column(unique = true)
    @Size(max = 255)
    @NotNull
    private String username;

    @NotNull
    private String password;

    @Size(max = 255)
    private String email;

    public static com.intimate.owners.Owner getOwner(String username, String password) {
        if (username == null || username.length() == 0) throw new IllegalArgumentException("The username argument is required");
        if (password == null || password.length() == 0) throw new IllegalArgumentException("The password argument is required");
        
        return findOwner(username.toLowerCase(), password).getSingleResult();
    }
    
    public static com.intimate.owners.Owner getOwner(String username) {
        if (username == null || username.length() == 0) throw new IllegalArgumentException("The username argument is required");
        Owner owner = findOwnerByUsername(username.toLowerCase()).getSingleResult();
        //owner.setPassword(null);
        //owner.setEmail(null);
        return owner;
    }

    public static TypedQuery<com.intimate.owners.Owner> findOwner(String username, String password) {
        EntityManager em = entityManager();
        TypedQuery<Owner> q = em.createQuery("SELECT o FROM Owner AS o WHERE o.username = :username and o.password = :password", Owner.class);
        q.setParameter("username", username);
        q.setParameter("password", password);
        return q;
    }
    
    public static TypedQuery<com.intimate.owners.Owner> findOwnerByUsername(String username) {
        EntityManager em = entityManager();
        TypedQuery<Owner> q = em.createQuery("SELECT o FROM Owner AS o WHERE o.username = :username", Owner.class);
        q.setParameter("username", username);
        return q;
    }
}
