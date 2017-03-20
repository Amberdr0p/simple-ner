package impl;

import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.LoadType;
import org.aeonbits.owner.Config.Sources;
import org.aeonbits.owner.ConfigFactory;
import org.aeonbits.owner.Mutable;

@LoadPolicy(LoadType.FIRST)
@Sources({"file:/blazegraph/simple-ner/config.properties"})
public interface ServiceConfig extends Mutable {

  public static final ServiceConfig CONFIG = ConfigFactory.create(ServiceConfig.class);

  @DefaultValue("http://winghouse.semiot.ru:3030/blazegraph")
  @Key("services.triplestore.url")
  String storeUrl();

  @DefaultValue("filename.txt")
  @Key("services.readfile.name")
  String readFileName();

  @DefaultValue("0")
  @Key("services.readfile.row")
  int readFileRow();

  @DefaultValue("fileres.txt")
  @Key("services.write.name")
  String writeFileName();

  @DefaultValue("3000")
  @Key("services.window.size")
  int windowSize();

}
