package co.pablobastidasv;

public interface ConfigurationConstants {
  String JWT_PRIVATE_KEY = "co.pablobastidasv.auth.private-key";
  String JWT_KEY_ID = "co.pablobastidasv.auth.jwt.key-id";
  String JWT_AUD = "co.pablobastidasv.auth.jwt.aud";
  String JWT_EXPIRES_IN = "co.pablobastidasv.jwt.expires-in";
  String JWT_EXPIRES_IN_DEFAULT = "300"; // 5 minutes

  String MP_JWT_PUBLIC_KEY = "mp.jwt.verify.publickey";
}
