package config;

public interface ConfigField {

  ConfigField addChildField(ConfigField child);

  ConfigFile build();
}
