package store;

import com.bigdata.rdf.sail.webapp.client.IPreparedTupleQuery;
import com.bigdata.rdf.sail.webapp.client.RemoteRepository;
import com.bigdata.rdf.sail.webapp.client.RemoteRepositoryManager;
import impl.ServiceConfig;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RDFStore {
  private static final String QUERY_SELECT_TWO_LEMMA =
      "select ?uri ?x { ?uri <http://www.custom-ontology.org/ner#lemma> ?x. "
          + "?uri <http://www.custom-ontology.org/ner#lemma> \"${LEMMA1}\". "
          + "?uri <http://www.custom-ontology.org/ner#lemma> \"${LEMMA2}\"}";
  private static final String QUERY_SELECT_ONLY_ONE_LEMMA =
      "select ?uri (count(?i) as ?count) where {?uri <http://www.custom-ontology.org/ner#lemma> ?i."
          + " ?uri <http://www.custom-ontology.org/ner#lemma> \"${LEMMA1}\"} "
          + "group by ?uri HAVING (?count = 1)";
  private static final String QUERY_SELECT_TYPES_FOR_URI =
      "select ?type " + "{ <${URI}> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?type.} ";

  private static final String VAR_LEMMA1 = "${LEMMA1}";
  private static final String VAR_LEMMA2 = "${LEMMA2}";
  private static final String VAR_URI = "${URI}";

  private static final long maxQueryMs = 10000;

  private static final RemoteRepositoryManager repo =
      new RemoteRepositoryManager(ServiceConfig.CONFIG.storeUrl());
  private static final RemoteRepository rr = repo.getRepositoryForDefaultNamespace();



  ////////////////////////////////////////////////////

  public static TupleQueryResult select(String query) {
    TupleQueryResult result = null;
    try {
      IPreparedTupleQuery iprepQuery = rr.prepareTupleQuery(query);
      iprepQuery.setMaxQueryMillis(maxQueryMs);
      result = iprepQuery.evaluate();
    } catch (Exception e) {
      e.printStackTrace();
      if (result != null) {
        try {
          result.close();
        } catch (QueryEvaluationException ex) {
          ex.printStackTrace();
        }
      }
    }
    return result;
  }

  public static Map<String, NamedEntity> selectMapQuery(String query) {
    Map<String, NamedEntity> map = new HashMap<String, NamedEntity>();
    TupleQueryResult result = select(query);

    try {
      while (result != null && result.hasNext()) {
        BindingSet bs = result.next();
        String label = bs.getValue("x").stringValue();
        String uri = bs.getValue("uri").stringValue();

        if (map.containsKey(uri)) {
          map.get(uri).addToLemma(label);
        } else {
          List<String> list = new ArrayList<String>();
          list.add(label);
          map.put(uri, new NamedEntity(uri, "type", list));
        }
        // System.out.println(bs);
      }
    } catch (QueryEvaluationException e) {
      e.printStackTrace();
    } finally {
      if (result != null) {
        try {
          result.close();
        } catch (QueryEvaluationException e) {
          e.printStackTrace();
        }
      }
    }

    return map;
  }

  //

  public static String getTypeForUri(String uri) {
    TupleQueryResult result = select(QUERY_SELECT_TYPES_FOR_URI.replace(VAR_URI, uri));
    List<String> types = new ArrayList<String>();
    try {
      while (result != null && result.hasNext()) {
        types.add(result.next().getValue("type").stringValue());
      }
    } catch (QueryEvaluationException e) {
      e.printStackTrace();
    } finally {
      if (result != null) {
        try {
          result.close();
        } catch (QueryEvaluationException e) {
          e.printStackTrace();
        }
      }
    }
    if (types.contains("http://nerd.eurecom.fr/ontology#Animal")) {
      return "Animal";
    } else if (types.contains("http://nerd.eurecom.fr/ontology#Person")) {
      return "Person";
    } else if (types.contains("http://nerd.eurecom.fr/ontology#Location")) {
      return "Location";
    } else if (types.contains("http://nerd.eurecom.fr/ontology#Organisation")) {
      return "Organisation";
    } else if (types.contains("http://nerd.eurecom.fr/ontology#Thing")) {
      return "Thing";
    }
    return null;
  }

  public static String selectOnlyOneLemma(String lemma1) {
    TupleQueryResult result = select(QUERY_SELECT_ONLY_ONE_LEMMA.replace(VAR_LEMMA1, lemma1));
    String res = null;
    try {
      while (result != null && result.hasNext()) {
        res = result.next().getValue("uri").stringValue();
        break; // ????????? тут можно добавить разные проверки, а не брать чисто 1-й
      }
    } catch (QueryEvaluationException e) {
      e.printStackTrace();
    } finally {
      if (result != null) {
        try {
          result.close();
        } catch (QueryEvaluationException e) {
          e.printStackTrace();
        }
      }
    }
    return res;
  }

  public static Map<String, List<String>> selectMapForTwoLemma(String lemma1, String lemma2) {
    return selectMap(
        QUERY_SELECT_TWO_LEMMA.replace(VAR_LEMMA1, lemma1).replace(VAR_LEMMA2, lemma2));
  }

  public static Map<String, List<String>> selectMap(String query) {
    Map<String, List<String>> map = new HashMap<String, List<String>>();
    // long millis = System.currentTimeMillis();

    TupleQueryResult result = null;
    // result processing
    try {
      IPreparedTupleQuery iprepQuery = rr.prepareTupleQuery(query);
      iprepQuery.setMaxQueryMillis(maxQueryMs);
      result = iprepQuery.evaluate();
      while (result != null && result.hasNext()) {
        BindingSet bs = result.next();
        String label = bs.getValue("x").stringValue();
        String uri = bs.getValue("uri").stringValue();
        if (map.containsKey(uri)) {
          map.get(uri).add(label);
        } else {
          List<String> list = new ArrayList<String>();
          list.add(label);
          map.put(uri, list);
        }
        // System.out.println(bs);
      }
      // System.out.println("AllTime: " + String.valueOf(System.currentTimeMillis() - millis));
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (result != null) {
        try {
          result.close();
        } catch (QueryEvaluationException e) {
          e.printStackTrace();
        }
      }
    }

    return map;
  }

}
