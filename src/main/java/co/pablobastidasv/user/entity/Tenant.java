package co.pablobastidasv.user.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "tenant")
public class Tenant {
    @Id
    private String id;
    @Column(length = 200)
    private String name;

    @ManyToMany(mappedBy = "tenants")
    private List<User> user;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getUser() {
        return user;
    }

    public void setUser(List<User> user) {
        this.user = user;
    }
}
