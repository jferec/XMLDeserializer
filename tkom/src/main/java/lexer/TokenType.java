package lexer;

public enum TokenType {
  OpeningTagBegin, TagEnd, SelfClosingTag, ClosingTagBegin, PrologBegin, PrologEnd, Equals, WhiteSpace, CharSequence, String, EOT;
}
