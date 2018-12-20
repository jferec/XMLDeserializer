package lexer;

public enum TokenType {
  OpeningTagBegin, OpeningTagEnd, ClosingTagBegin, ClosingTagEnd, PrologBegin, PrologEnd, Equals, SingleQuotationMark, DoubleQuotationMark, WhiteSpace, Letter, Unknown;
}
