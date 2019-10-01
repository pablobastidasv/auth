package co.pablobastidasv.user.entity;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
public class PasswordToken {

  @Id
  private Long id;

  @NotNull
  @Column(name="password_token", length = 200)
  private String token;
  @NotNull
  @Temporal(TemporalType.TIMESTAMP)
  private Date expirationTime;
  @ManyToOne
  @JoinColumn(name = "user_id")
  private User userId;

  public PasswordToken() {
  }

  public Long getId() {
    return id;
  }

  public String getToken() {
    return token;
  }

  public Date getExpirationTime() {
    return expirationTime;
  }

  public User getUserId() {
    return userId;
  }

  public static class Builder{

    private final PasswordToken passwordToken;

    public Builder(){
      this.passwordToken = new PasswordToken();
      this.passwordToken.token = UUID.randomUUID().toString();

      Instant expirationInstant = Instant.now(Clock.systemUTC())
          .plus(15, ChronoUnit.MINUTES);


      this.passwordToken.expirationTime = Date.from(expirationInstant);
    }

    public Builder setId(Long id) {
      this.passwordToken.id = id;
      return this;
    }

    public Builder setUserId(User userId) {
      this.passwordToken.userId = userId;
      return this;
    }

    public PasswordToken build(){
      Objects.requireNonNull(this.passwordToken.userId);
      return this.passwordToken;
    }

  }

}
