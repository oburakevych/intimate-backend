package com.intimate.files;

import com.intimate.owners.Owner;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
@RooJson
public class File {

    @Id
    @Column(name = "id")
    private String id = UUID.randomUUID().toString();

    @ManyToOne
    private Owner owner;

    private String name;
}
