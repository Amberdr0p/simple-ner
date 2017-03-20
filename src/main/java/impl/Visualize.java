package impl;

import com.chaoticity.dependensee.Main;

public class Visualize {


  // http://nlp.stanford.edu/software/lex-parser.shtml

  // tydevi Typed Dependency Viewer that makes a picture of the Stanford Dependencies analysis of a
  // sentence. By Bernard Bou.
  // DependenSee A Dependency Parse Visualisation Tool that makes pictures of Stanford Dependency
  // output. By Awais Athar.



  public static void main(String[] args) throws Exception {
    String text = "A quick brown fox jumped over the lazy dog.";

    ProcessingText.init();
    ProcessingText.process(text);

    /* TreebankLanguagePack tlp = new PennTreebankLanguagePack();
    GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
    LexicalizedParser lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
    lp.setOptionFlags(new String[]{"-maxLength", "500", "-retainTmpSubcategories"});
    TokenizerFactory<CoreLabel> tokenizerFactory =
            PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
    List<CoreLabel> wordList = tokenizerFactory.getTokenizer(new StringReader(text)).tokenize();
    Tree tree = lp.apply(wordList);    
    
    GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
    Collection<TypedDependency> tdl = gs.typedDependenciesCCprocessed(true);*/ 

    Main.writeImage(text, "image.png", 3);
  }

}
