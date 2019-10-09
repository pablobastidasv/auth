package co.pablobastidasv.user.entity;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "roles")
public class Role {
  @Id private String id;

  @Column(length = 200)
  private String name;

  @ManyToMany(mappedBy = "roles")
  private List<SystemUser> systemUsers;

  public Role() {

  }

  public Role(String id, String name) {
    this.id = id;
    this.name = name;
  }

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

  public List<SystemUser> getSystemUsers() {
    return systemUsers;
  }

  public void setSystemUsers(List<SystemUser> systemUsers) {
    this.systemUsers = systemUsers;
  }
}
