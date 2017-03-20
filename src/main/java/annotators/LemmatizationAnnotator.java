package annotators;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.ArraySet;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.ErasureUtils;
import ru.stachek66.nlp.mystem.holding.Factory;
import ru.stachek66.nlp.mystem.holding.MyStem;
import ru.stachek66.nlp.mystem.holding.MyStemApplicationException;
import ru.stachek66.nlp.mystem.holding.Request;
import ru.stachek66.nlp.mystem.model.Info;
import scala.Option;
import scala.collection.JavaConversions;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class LemmatizationAnnotator implements edu.stanford.nlp.pipeline.Annotator {

  private static final Option<String> NULL_OPTION = scala.Option.apply(null);
  private static final MyStem mystemAnalyzer =
      new Factory("-ld --format json").newMyStem("3.0", Option.<File>empty()).get();

  @Override
  public void annotate(Annotation annotation) {
    System.out.println("Adding Lemmatization annotations ... ");

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    for (CoreMap sentence : sentences) {
      List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
      for (CoreLabel token : tokens) {
        if (token.get(LemmatizationAnnotation.class) == null) {
          token.set(LemmatizationAnnotation.class, getLemma(token.originalText()));
        }
      }
    }
    annotation.set(SentAnnotation.class, sentences);
  }

  @Override
  public Set<Class<? extends CoreAnnotation>> requires() {
    return new ArraySet<Class<? extends CoreAnnotation>>(CoreAnnotations.TextAnnotation.class,
        CoreAnnotations.TokensAnnotation.class,
        CoreAnnotations.CharacterOffsetBeginAnnotation.class,
        CoreAnnotations.CharacterOffsetEndAnnotation.class,
        CoreAnnotations.SentencesAnnotation.class);
  }

  private String getLemma(String token) {
    String lemma = "";
    try {
      List<Info> result;

      result = JavaConversions
          .seqAsJavaList(mystemAnalyzer.analyze(Request.apply(token)).info().toSeq());

      if (!result.isEmpty()) {
        Option<String> lex1 = result.get(0).lex();
        if (lex1 != null && lex1 != NULL_OPTION) {
          lemma = lex1.get();
        }
      }
    } catch (MyStemApplicationException e) {
      e.printStackTrace();
    }
    return lemma;
  }

  @Override
  public Set<Class<? extends CoreAnnotation>> requirementsSatisfied() {
    return Collections.emptySet();
  }

  public static class LemmatizationAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
      return String.class;
    }
  }

  public static class SentAnnotation implements CoreAnnotation<List<CoreMap>> {
    @Override
    public Class<List<CoreMap>> getType() {
      return ErasureUtils.uncheckedCast(List.class);
    }
  }
}
