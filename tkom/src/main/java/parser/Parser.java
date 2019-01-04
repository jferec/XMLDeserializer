package parser;

import com.google.common.collect.ImmutableMap;

import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import javax.management.modelmbean.XMLParseException;

import lexer.Lexer;
import lexer.Token;
import lexer.TokenType;

public class Parser {
  private static int MAX_SPECIAL_CHAR_LENGTH = 6;
  private static int MIN_SPECIAL_CHAR_LENGTH = 4;
  private static ImmutableMap<String, Character> SPECIAL_CHARS = ImmutableMap.<String, Character>builder()
      .put("&amp;", '&')
      .put("&lt;", '<')
      .put("&gt;", '>')
      .put("&quot;", '"')
      .put("&apos", '\'')
      .build();

  private static ImmutableMap<TagType, Set<TagType>> ALLOWED_TAG_TYPES = ImmutableMap.<TagType, Set<TagType>>builder()
      .put(TagType.Start, Sets.newHashSet(TagType.Open))
      .put(TagType.Open,
          Sets.newHashSet(TagType.SelfClosing, TagType.Value, TagType.Close, TagType.Open))
      .put(TagType.Close, Sets.newHashSet(TagType.SelfClosing, TagType.Open, TagType.Close, TagType.End))
      .put(TagType.SelfClosing, Sets.newHashSet(TagType.Open, TagType.Close, TagType.SelfClosing, TagType.End))
      .put(TagType.Value, Sets.<TagType>newHashSet(TagType.Close))
      .put(TagType.End, Sets.newHashSet())
      .build();


  private final Lexer lexer;
  Stack<XMLNode> nodes = new Stack<>();
  private XMLFile xmlFile = new XMLFile();
  private Token token = null;

  Parser(Lexer lexer) {
    this.lexer = lexer;
  }

  public XMLFile run() throws IOException, XMLParseException {
    return parse();
  }

  private XMLFile parse() throws IOException, XMLParseException {
    parseProlog();
    xmlFile.setRoot(parseNodes());
    return xmlFile;
  }

  private boolean tokenTypeEquals(TokenType type) {
    return token.getType().equals(type);
  }

  private String parseCharSequence() throws IOException, XMLParseException {
    StringBuilder charSequence = new StringBuilder();
    while (tokenTypeEquals(TokenType.Char)) {
      charSequence.append(token.getValue());
      getNextToken();
    }
    if(charSequence.length() == 0){
      throw new XMLParseException("Char sequence not found");
    }
    skipWhiteScapes();
    return charSequence.toString();
  }

  private String parseCharSequenceWithWhiteSpaces() throws IOException, XMLParseException {
    skipWhiteScapes();
    StringBuilder charSequence = new StringBuilder();
    while (tokenTypeEquals(TokenType.Char) || tokenTypeEquals(TokenType.WhiteSpace)) {
      char curr = token.getValue();
      if (curr == '&') {
        charSequence.append(parseSpecialChar());
      } else {
        charSequence.append(token.getValue());
      }
      getNextToken();
    }
    skipWhiteScapes();
    return charSequence.toString().trim();
  }

  /**
   * Called when we encounter '&' in char sequence (beginning of a special character)
   */
  private Character parseSpecialChar() throws IOException, XMLParseException {
    StringBuilder specialChar = new StringBuilder(token.getValue());
    while (specialChar.length() < MAX_SPECIAL_CHAR_LENGTH && !token.getValue().equals(';')) {
      getNextToken();
      specialChar.append(token.getValue());
      if (specialChar.length() >= MIN_SPECIAL_CHAR_LENGTH && SPECIAL_CHARS
          .containsKey(specialChar.toString())) {
        getNextToken();
        skipWhiteScapes();
        return SPECIAL_CHARS.get(specialChar.toString());
      }
    }
    throw new XMLParseException("Failed to parse the special char.");
  }


  private void skipWhiteScapes() throws IOException {
    while (token != null && tokenTypeEquals(TokenType.WhiteSpace)) {
      getNextToken();
    }
  }

  private void getNextToken() throws IOException {
    this.token = lexer.getNextToken();
  }

  private void parseProlog() throws IOException, XMLParseException {
    getNextToken();
    if (!tokenTypeEquals(TokenType.PrologBegin)) {
      throw new XMLParseException("Prolog is missing.");
    }
    getNextToken();
    //Don't skip whitespaces here - nonempty CharSequence expected!
    String xml = parseCharSequence();
    if (!xml.equals("xml")) {
      throw new XMLParseException("XML Prolog has invalid format");
    }
    skipWhiteScapes();
    parseAttributes();
    if (!tokenTypeEquals(TokenType.PrologEnd)) {
      throw new XMLParseException("Prolog is missing closing '?>'");
    }
    getNextToken();
    skipWhiteScapes();
  }

  private List<XMLAttribute> parseAttributes() throws IOException, XMLParseException {
    skipWhiteScapes();
    List<XMLAttribute> attributes = new ArrayList<>();
    while (tokenTypeEquals(TokenType.Char)) {
      attributes.add(parseAttribute());
    }
    return attributes;
  }

  private XMLNode parseNodes() throws IOException, XMLParseException {
    XMLNode root = null;
    TagType lastTag = TagType.Start;
    TagType currTag = null;
    XMLNode parent = null;
    skipWhiteScapes();
    while (token != null) {
      switch (token.getType()) {
        case OpeningTagBegin: {
          XMLNode next = parseOpeningTag();
          if (root == null) {
            root = next;
          }
          currTag = (next.isSelfClosing()) ? TagType.SelfClosing : TagType.Open;
          if (!isTransitionAllowed(lastTag, currTag)) {
            throw new XMLParseException("Transition not allowed");
          }
          parent = nodes.isEmpty() ? null : nodes.peek();
          if (parent != null) {
            parent.addChild(next);
          }
          if (!next.isSelfClosing()) {
            nodes.push(next);
          }
          getNextToken();
          skipWhiteScapes();
        }
        break;
        case ClosingTagBegin: {
          if (nodes.isEmpty()) {
            throw new XMLParseException("Trying to close tag that wasn't open");
          }
          if(nodes.size() == 1){
            currTag = TagType.End;
          } else{
            currTag = TagType.Close;
          }
          if (!isTransitionAllowed(lastTag, currTag)) {
            throw new XMLParseException("Closing tag is not allowed here");
          }
          XMLNode node = nodes.pop();
          parseClosingTag(node);
        }
        break;
        case Char: {
          currTag = TagType.Value;
          if (!isTransitionAllowed(lastTag, currTag)) {
            throw new XMLParseException("Closing tag is not allowed here");
          }
          if (nodes.isEmpty()) {
            throw new XMLParseException("Value is not inside the tag.");
          }
          parent = nodes.peek();
          skipWhiteScapes();
          String value = parseCharSequenceWithWhiteSpaces();
          parent.setValue(value);
        }
        break;
      }
      lastTag = currTag;
    }
    if(!nodes.isEmpty()){
      throw new XMLParseException("One or more tags are not closed.");
    }
    return root;
  }

  /**
   * Current token is '<'
   */
  private XMLNode parseOpeningTag() throws IOException, XMLParseException {
    getNextToken();
    //Don't skip whitespaces here - nonempty CharSequence expected!
    String nodeName = parseCharSequence();
    skipWhiteScapes();
    List<XMLAttribute> nodeAttributes = parseAttributes();
    skipWhiteScapes();
    XMLNode next = new XMLNode()
        .setName(nodeName)
        .setAttributes(nodeAttributes);
    switch (token.getType()) {
      case TagEnd:
        return next;
      case SelfClosingTag:
        return next.setSelfClosing(true);
      default:
        throw new XMLParseException("The opening tag is not closed properly");
    }
  }

  /**
   * Current token is '</'
   */
  private void parseClosingTag(XMLNode node) throws IOException, XMLParseException {
    getNextToken();
    //Don't skip whitespaces here - nonempty CharSequence expected!
    String name = parseCharSequence();
    if (!name.equals(node.getName())) {
      throw new XMLParseException("The closing tag names is not matching opening tag name.");
    }
    skipWhiteScapes();
    if (!tokenTypeEquals(TokenType.TagEnd)) {
      throw new XMLParseException("The closing tag is not closed properly.");
    }
    getNextToken();
    skipWhiteScapes();
  }


  private XMLAttribute parseAttribute() throws IOException, XMLParseException {
    String attributeName = parseCharSequence();
    skipWhiteScapes();
    if (!token.getType().equals(TokenType.Equals)) {
      throw new XMLParseException(
          String.format("Attribute %s has no \'=\' sign after it's name", attributeName));
    }
    getNextToken();
    skipWhiteScapes();
    boolean singleQuotationMark;
    if (tokenTypeEquals(TokenType.SingleQuotationMark)) {
      singleQuotationMark = true;
    } else if (tokenTypeEquals(TokenType.DoubleQuotationMark)) {
      singleQuotationMark = false;
    } else {
      throw new XMLParseException(
          String
              .format("Attribute %s has no quotation mark sign after  \'=\' sign", attributeName));
    }
    getNextToken();
    skipWhiteScapes();
    String attributeValue = parseCharSequenceWithWhiteSpaces();
    if ((singleQuotationMark) ? !tokenTypeEquals(TokenType.SingleQuotationMark)
        : !tokenTypeEquals(TokenType.DoubleQuotationMark)) {
      throw new XMLParseException("Closing quotation mark is expected after attribute's value.");
    }
    getNextToken();
    skipWhiteScapes();
    return new XMLAttribute(attributeName, attributeValue);
  }


  private static boolean isTransitionAllowed(TagType last, TagType curr) {
    return ALLOWED_TAG_TYPES.get(last).contains(curr);
  }
}
