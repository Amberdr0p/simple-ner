package store;

import java.util.List;

public class NamedEntity {
  private String uri;
  private String type;
  private List<String> lemma;
  
  NamedEntity(String uri, String type, List<String> lemma) {
    this.uri = uri;
    this.type = type;
    this.lemma = lemma;
  }
  
  public void addToLemma(String newLemma) {
    lemma.add(newLemma);
  }
}
