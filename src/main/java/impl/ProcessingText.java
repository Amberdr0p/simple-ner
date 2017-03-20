package impl;

import annotators.LemmatizationAnnotator;
import annotators.NERAnnotator;
import annotators.NERAnnotator.SentWithNERAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.util.List;
import java.util.Properties;

public class ProcessingText {

  private static StanfordCoreNLP pipeline;

  public static void init() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize, ssplit");
    pipeline = new StanfordCoreNLP(props);
    pipeline.addAnnotator(new LemmatizationAnnotator());
    pipeline.addAnnotator(new NERAnnotator());
  }

  public static List<CoreMap> process(String text) {
    Annotation annotation = pipeline.process(text);// LemmaAnnotation
    // String val = annotation.get(LemmatizationAnnotation.class);
    return annotation.get(SentWithNERAnnotation.class);
  }

}
