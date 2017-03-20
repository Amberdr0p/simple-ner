package impl;

import annotators.LemmatizationAnnotator.LemmatizationAnnotation;
import annotators.NERAnnotator.NERAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;
import ru.stachek66.nlp.mystem.holding.Factory;
import ru.stachek66.nlp.mystem.holding.MyStem;
import ru.stachek66.nlp.mystem.holding.MyStemApplicationException;
import ru.stachek66.nlp.mystem.model.Info;
import scala.Option;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Launcher {

  private static final MyStem mystemAnalyzer =
      new Factory("-ld --format json").newMyStem("3.0", Option.<File>empty()).get();
  private static final Option<String> nullOption = scala.Option.apply(null);

  private static ExecutorService executor = new ThreadPoolExecutor(10, 10, 0L,
      TimeUnit.MILLISECONDS, new SynchronousQueue(), new ThreadPoolExecutor.CallerRunsPolicy());

  public static void main(String[] args) {
    try {
      init();
      List<String> listLine = ProcessingFile.nextWindow(ServiceConfig.CONFIG.windowSize()); // ����
                                                                                            // �������
                                                                                            // ����
      processingWindow(listLine);

    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  private static void init() throws IOException {
    ProcessingFile.readFile(ServiceConfig.CONFIG.readFileName(),
        ServiceConfig.CONFIG.readFileRow());
    ProcessingText.init();
  }

  private static void processingWindow(List<String> listLine) throws MyStemApplicationException {
    List<Info> result;
    for (String line : listLine) {
      List<CoreMap> lcm = ProcessingText.process(line);
      String resLine = new String(line);
      for (CoreMap cm : lcm) {
        System.out.println(cm.toString());

        for (CoreLabel token : cm.get(CoreAnnotations.TokensAnnotation.class)) {
          // System.out.println(token.originalText() + "\t|\t" + token.get(NERAnnotation.class));
          System.out.printf("%-25s%20s%15s%n", token.originalText(),
              token.get(LemmatizationAnnotation.class), token.get(NERAnnotation.class));
        }
      }
    }
  }

}
