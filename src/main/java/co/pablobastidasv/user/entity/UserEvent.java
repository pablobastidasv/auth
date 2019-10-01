package co.pablobastidasv.user.entity;

import java.util.List;
import javax.json.bind.annotation.JsonbNillable;

@JsonbNillable
public class UserEvent {

  public Long userId;
  public List<Email> emails;
  public boolean hasUser;

  @Override
  public String toString() {
    return "UserEvent{"
        + "userId='" + userId + "'"
        + ", emails=" + emails
        + ", hasUser=" + hasUser
        + '}';
  }

  public static class Email {
    public String email;
    public String type;

    @Override
    public String toString() {
      return "Email{email='" + email + "', type='" + type + "'}";
    }
  }
}
