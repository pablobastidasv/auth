package co.pablobastidasv.user.entity;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "tenants")
public class Tenant {
  @Id private String id;

  @Column(length = 200)
  private String name;

  @ManyToMany(mappedBy = "tenants")
  private List<SystemUser> systemUsers;

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

  public List<SystemUser> getSystemUser() {
    return systemUsers;
  }

  public void setSystemUser(List<SystemUser> systemUsers) {
    this.systemUsers = systemUsers;
  }
}
