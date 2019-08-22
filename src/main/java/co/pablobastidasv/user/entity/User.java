package co.pablobastidasv.user.entity;

import static co.pablobastidasv.user.entity.User.FIND_BY_USERNAME_AND_TENANT;
import static co.pablobastidasv.user.entity.User.TENANT_FIELD;
import static co.pablobastidasv.user.entity.User.USERNAME_FIELD;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Entity
@NamedQueries({
    @NamedQuery(name = FIND_BY_USERNAME_AND_TENANT,
        query = "SELECT u FROM User u JOIN u.tenants t LEFT JOIN u.roles g"
              + " WHERE u.username = :" + USERNAME_FIELD
              + " AND t.id = :" + TENANT_FIELD)
})
@Table(name = "users")
public class User {

  public static final String FIND_BY_USERNAME_AND_TENANT = "User.findByUsernameAndTenant";

  public static final String TENANT_FIELD = "tenantId";
  public static final String USERNAME_FIELD = "username";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Size(max = 200, min = 3)
  @Column(length = 200, unique = true)
  @Email
  private String username;
  @Size(max = 1000)
  @Column(name = "enc_key", length = 1000)
  private String key;
  @Size(max = 1000)
  @Column(length = 1000)
  private String salt;

  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(name = "user_x_tenant",
      joinColumns = @JoinColumn(name = "user_id", nullable = false),
      inverseJoinColumns = @JoinColumn(name = "tenant_id", nullable = false),
      uniqueConstraints = @UniqueConstraint(
                                             name = "unq_user_tenant",
                                             columnNames = {"user_id", "tenant_id"}
                                           )
  )
  private List<Tenant> tenants;

  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(name = "user_x_role",
      joinColumns = @JoinColumn(name = "user_id", nullable = false),
      inverseJoinColumns = @JoinColumn(name = "role_id", nullable = false),
      uniqueConstraints = @UniqueConstraint(
                                             name = "unq_user_role",
                                             columnNames = {"user_id", "role_id"}
                                           )
  )
  private List<Role> roles;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getSalt() {
    return salt;
  }

  public void setSalt(String salt) {
    this.salt = salt;
  }

  public List<Tenant> getTenants() {
    return tenants;
  }

  public void setTenants(List<Tenant> tenants) {
    this.tenants = tenants;
  }

  public List<Role> getRoles() {
    return roles;
  }

  public void setRoles(List<Role> roles) {
    this.roles = roles;
  }
}
