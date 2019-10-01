package co.pablobastidasv.user.entity;

import static co.pablobastidasv.user.entity.User.FIND_BY_USERNAME_AND_TENANT;
import static co.pablobastidasv.user.entity.User.TENANT_FIELD;
import static co.pablobastidasv.user.entity.User.USERNAME_FIELD;

import co.pablobastidasv.user.boundary.PasswordTools;
import java.util.List;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@NamedQueries({
    @NamedQuery(
      name = FIND_BY_USERNAME_AND_TENANT,
      query =
          "SELECT u FROM User u JOIN u.tenants t LEFT JOIN u.roles g"
              + " WHERE u.username = :"
              + USERNAME_FIELD
              + " AND t.id = :"
              + TENANT_FIELD)
})
@Table(name = "users")
public class User {

  public static final String FIND_BY_USERNAME_AND_TENANT = "User.findByUsernameAndTenant";

  public static final String TENANT_FIELD = "tenantId";
  public static final String USERNAME_FIELD = "username";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonbTransient
  private Long id;

  @NotNull
  @Column(name = "system_user_id")
  @JsonbTransient
  private Long userId;

  @Size(max = 200, min = 3)
  @Column(length = 200, unique = true)
  @Email
  private String username;

  @Size(max = 1000)
  @Column(name = "enc_key", length = 1000)
  @JsonbTransient
  private String key;

  @Size(max = 1000)
  @Column(length = 1000)
  @JsonbTransient
  private String salt;

  @Enumerated(EnumType.STRING)
  private State state = State.CREATED;

  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(
      name = "user_x_tenant",
      joinColumns = @JoinColumn(name = "user_id", nullable = false),
      inverseJoinColumns = @JoinColumn(name = "tenant_id", nullable = false),
      uniqueConstraints =
          @UniqueConstraint(
              name = "unq_user_tenant",
              columnNames = {"user_id", "tenant_id"}))
  private List<Tenant> tenants;

  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(
      name = "user_x_role",
      joinColumns = @JoinColumn(name = "user_id", nullable = false),
      inverseJoinColumns = @JoinColumn(name = "role_id", nullable = false),
      uniqueConstraints =
          @UniqueConstraint(
              name = "unq_user_role",
              columnNames = {"user_id", "role_id"}))
  private List<Role> roles;
  @OneToMany(mappedBy = "userId")
  private List<PasswordToken> tokens;

  @Transient
  private PasswordTools passwordTools;

  public User() {
  }

  public User(PasswordTools passwordTools) {
    this.passwordTools = passwordTools;
  }

  /**
   * Creates a random password and <salt>salt</salt> and generate the <code>key</code>.
   *
   * <p>This method will also change the user status to <code>CHANGE_PWD</code> leaving the user
   * ready to reassign a new password.
   *
   * @return this user.
   */
  public User resetPassword() {
    String password = this.passwordTools.generateRandomPassword();
    return resetPassword(password);
  }

  /**
   * Create a random salt store them in the entity and with these generate the key.
   *
   * @param password Password to assign to the actual User.
   * @return this user.
   */
  public User resetPassword(String password) {
    this.salt = passwordTools.generateSalt();
    this.key = passwordTools.hashPassword(password, salt);

    this.state = State.CHANGE_PWD;

    // TODO: Create token

    return this;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }

  public User setUserId(Long userId) {
    this.userId = userId;
    return this;
  }

  public String getUsername() {
    return username;
  }

  public User setUsername(String username) {
    this.username = username;
    return this;
  }

  public String getKey() {
    return key;
  }

  public User setKey(String key) {
    this.key = key;
    return this;
  }

  public String getSalt() {
    return salt;
  }

  public User setSalt(String salt) {
    this.salt = salt;
    return this;
  }

  public List<PasswordToken> getTokens() {
    return tokens;
  }

  public void setTokens(List<PasswordToken> tokens) {
    this.tokens = tokens;
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

  public State getState() {
    return state;
  }

  public void setState(State state) {
    this.state = state;
  }
}
