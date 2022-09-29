package uy.com.md.common;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MaskingPatternLayout extends PatternLayout {
  private String lastLoadedRegex;
  private final List<Pair<Pattern, String>> patterns = new ArrayList<>();

  public void setRegex(String value){
    this.lastLoadedRegex = value;
  }

  public void setReplacement(String value){
    patterns.add(Pair.of(Pattern.compile(lastLoadedRegex), value));
  }

  @Override
  public String doLayout(ILoggingEvent iLoggingEvent) {
    String msg = super.doLayout(iLoggingEvent);
    for(Pair<Pattern, String> p: this.patterns){
      Matcher m = p.getKey().matcher(msg);
      msg = m.find() ? m.replaceAll(p.getValue()): msg;
    }

    return msg;
  }
}
