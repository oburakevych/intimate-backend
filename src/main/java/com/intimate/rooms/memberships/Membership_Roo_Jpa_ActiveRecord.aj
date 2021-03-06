// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.intimate.rooms.memberships;

import com.intimate.rooms.memberships.Membership;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

privileged aspect Membership_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager Membership.entityManager;
    
    public static final EntityManager Membership.entityManager() {
        EntityManager em = new Membership().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long Membership.countMemberships() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Membership o", Long.class).getSingleResult();
    }
    
    public static List<Membership> Membership.findAllMemberships() {
        return entityManager().createQuery("SELECT o FROM Membership o", Membership.class).getResultList();
    }
    
    public static Membership Membership.findMembership(String id) {
        if (id == null || id.length() == 0) return null;
        return entityManager().find(Membership.class, id);
    }
    
    public static List<Membership> Membership.findMembershipEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Membership o", Membership.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void Membership.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void Membership.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            Membership attached = Membership.findMembership(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void Membership.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void Membership.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public Membership Membership.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Membership merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
