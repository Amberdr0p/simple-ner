package annotators;

import annotators.LemmatizationAnnotator.LemmatizationAnnotation;
import annotators.LemmatizationAnnotator.SentAnnotation;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.ArraySet;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.ErasureUtils;
import store.RDFStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NERAnnotator implements edu.stanford.nlp.pipeline.Annotator {

  @Override
  public void annotate(Annotation annotation) {
    System.out.println("Adding NER annotations ... ");

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    for (CoreMap sentence : sentences) {
      List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
      for (int i = 0; i < tokens.size(); i++) {
        int index1 = getIndexNextNotEmptyLemma(tokens, i);
        if (index1 != -1) {
          String lex1 = tokens.get(index1).get(LemmatizationAnnotation.class);
          int index2 = getIndexNextNotEmptyLemma(tokens, index1 + 1);
          if (index2 != -1) {
            String lex2 = tokens.get(index2).get(LemmatizationAnnotation.class);

            int max = 0;
            List<Integer> maxIndexList = null;
            String maxUri = null;
            Map<String, List<String>> map = RDFStore.selectMapForTwoLemma(lex1, lex2);

            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
              List<String> list = entry.getValue();
              int count = list.size();

              if (count > max) {
                int maxForList = 2;
                List<Integer> indexList = new ArrayList<Integer>();
                indexList.add(index1);
                indexList.add(index2);

                List<String> eqList = new ArrayList<String>(list);
                eqList.remove(lex1);
                eqList.remove(lex2);

                for (int j = index2 + 1; j < tokens.size() && !eqList.isEmpty(); j++) {
                  int indexN = getIndexNextNotEmptyLemma(tokens, j);
                  if (indexN != -1
                      && eqList.contains(tokens.get(indexN).get(LemmatizationAnnotation.class))) {
                    eqList.remove(tokens.get(indexN).get(LemmatizationAnnotation.class));
                    maxForList++;
                    j = indexN;
                    indexList.add(indexN);
                  } else {
                    break;
                  }
                }

                if (((maxForList == count && max <= maxForList)
                    || (maxForList > 3 && maxForList + 1 == count && max < maxForList))
                    && haveUpperCase(tokens, indexList)) { // или +1?
                  max = maxForList;
                  maxIndexList = indexList;
                  maxUri = entry.getKey();
                }
              }
            }

            if (maxIndexList != null) {
              String type = getTypeForUri(maxUri);
              if (type != null) {
                for (int ind : maxIndexList) {
                  tokens.get(ind).set(NERAnnotation.class, type);
                }
              }
              i = maxIndexList.get(maxIndexList.size() - 1);
            } else {
              if (isWordUpperCase(tokens.get(index1).originalText())) {
                findForOneLemma(tokens, index1, lex1);
                i = index1;
              }
            }
          } else {
            if (isWordUpperCase(tokens.get(index1).originalText())) {
              findForOneLemma(tokens, index1, lex1);
            }
            break;
          }
        } else {
          break;
        }
      }
    }
    annotation.set(SentWithNERAnnotation.class, sentences);
  }

  private String getTypeForUri(String uri) {
    return RDFStore.getTypeForUri(uri);
  }

  private void findForOneLemma(List<CoreLabel> tokens, int index, String lemma) { /// надо будет
                                                                                  /// поправить,
                                                                                  /// чтобы искал
                                                                                  /// наиболее
                                                                                  /// подходящие,
                                                                                  /// т.к. там часто
                                                                                  /// бывают леммы и
                                                                                  /// слово не
                                                                                  /// соответствуют
                                                                                  /// вообще
    // return RDFStore.selectOnlyOneLemma(lemma);
    String uri = RDFStore.selectOnlyOneLemma(lemma);
    if (uri != null) {
      String type = getTypeForUri(uri);
      if (type != null) {
        tokens.get(index).set(NERAnnotation.class, type);
      }
    }
  }

  private boolean isWordUpperCase(String initialStr) {
    if (Character.isUpperCase(initialStr.charAt(0))) { // проверка на null не нужна т.к.
      // mystem все отметается
      return true;
    }
    return false;
  }

  private boolean haveUpperCase(List<CoreLabel> tokens, List<Integer> indexList) {
    for (int index : indexList) {
      if (isWordUpperCase(tokens.get(index).originalText())) {
        return true;
      }
    }
    return false;
  }

  private int getIndexNextNotEmptyLemma(List<CoreLabel> tokens, int index) {
    for (int i = index; i < tokens.size(); i++) {
      String str = tokens.get(i).get(LemmatizationAnnotation.class);
      if (str != null && !str.isEmpty()) {
        return i;
      }
    }
    return -1;
  }

  @Override
  public Set<Class<? extends CoreAnnotation>> requires() {
    return new ArraySet<Class<? extends CoreAnnotation>>(CoreAnnotations.TextAnnotation.class,
        CoreAnnotations.TokensAnnotation.class,
        CoreAnnotations.CharacterOffsetBeginAnnotation.class,
        CoreAnnotations.CharacterOffsetEndAnnotation.class,
        CoreAnnotations.SentencesAnnotation.class, SentAnnotation.class);
  }

  @Override
  public Set<Class<? extends CoreAnnotation>> requirementsSatisfied() {
    return Collections.emptySet();
  }

  public static class NERAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  public static class SentWithNERAnnotation implements CoreAnnotation<List<CoreMap>> {
    @Override
    public Class<List<CoreMap>> getType() {
      return ErasureUtils.uncheckedCast(List.class);
    }
  }
}
