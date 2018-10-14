package com.mycompany.ir;

import java.io.Reader;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class tokenizeAnalyser extends StopwordAnalyzerBase {
  private int maxTokenLength = 255;

  public tokenizeAnalyser(CharArraySet stopwords) {
    super(stopwords);
  }
  public CharArraySet getStopwords() {
    return stopwords;
  }
  @Override protected TokenStreamComponents createComponents(final String fieldName) {
    final Tokenizer source;
    StandardTokenizer tokens = new StandardTokenizer();
    tokens.setMaxTokenLength(maxTokenLength);
    source = tokens;
    CharArraySet myStopSet = CharArraySet.copy(StopAnalyzer.ENGLISH_STOP_WORDS_SET);
    TokenStream tok = new StandardFilter(source);
    TokenStream token = new LowerCaseFilter(tok);
    TokenStream tokener = new StopFilter(token, myStopSet);
    tokener = new PorterStemFilter(tokener);
    return new TokenStreamComponents(source, tokener) {
      @Override protected void setReader(final Reader reader) {
        ((StandardTokenizer) source).setMaxTokenLength(256);
        super.setReader(reader);
      }
    };
  }
}
