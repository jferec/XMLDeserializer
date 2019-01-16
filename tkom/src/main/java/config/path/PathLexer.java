package config.path;

import static com.google.common.base.Ascii.EOT;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.Queue;


public class PathLexer {

  private String[] edges;
  private String edge;
  private int tokenIndex = 0;
  private int edgeIndex = 0;
  private Queue<PathToken> tokens = new LinkedList<>();

  public PathLexer(String path) {
    edges = path.split("\\.");
    edge = edges[0];
  }

  public PathLexer tokenize() throws ParseException {
    for (int i = 0; i < edges.length; i++) {
      loadNextToken();
      tokens.add(processToken());
    }
    return this;
  }

  private void loadNextToken() {
    edge = edges[tokenIndex++];
    edgeIndex = 0;
  }

  private PathToken processToken() throws ParseException {
    String charSequence = processCharSequence();
    int nodeIndex = 0;
    if (getCurrChar() == '[') {
      nodeIndex = processNodeIndex(']');
    }
    if (edgeIndex >= edge.length() - 1) {
      return new PathToken()
          .setType(PathTokenType.Node)
          .setNodeName(charSequence)
          .setNodeIndex(nodeIndex);
    }
    if (getCurrChar() == ':') {
      edgeIndex++;
      String name = processCharSequence();
      if (edgeIndex >= edge.length() - 1) {
        return new PathToken()
            .setType(PathTokenType.Attribute)
            .setNodeName(charSequence)
            .setAttributeName(name)
            .setNodeIndex(nodeIndex);
      }
    } else if (getCurrChar() == '(') {
      int valueIndex = processNodeIndex(')');
      if (edgeIndex >= edge.length() - 1) {
        return new PathToken()
            .setType(PathTokenType.Value)
            .setNodeName(charSequence)
            .setNodeIndex(nodeIndex)
            .setValueIndex(valueIndex);
      }
    }
    throw new ParseException("Expected to finish", edgeIndex);
  }

  private int processNodeIndex(char c) throws ParseException {
    edgeIndex++;
    int value = 0;
    while (Character.isDigit(getCurrChar()) && getCurrChar() != c) {
      value *= 10;
      value += Character.getNumericValue(getCurrChar());
      edgeIndex++;
    }
    if (getCurrChar() != c) {
      throw new ParseException("Closing char ']' expected", edgeIndex);
    }
    edgeIndex++;
    return value;
  }

  private String processCharSequence() {
    StringBuilder charSequence = new StringBuilder();
    while (edgeIndex < edge.length() && Character.isLetterOrDigit(getCurrChar())) {
      charSequence.append(getCurrChar());
      edgeIndex++;
    }
    return charSequence.toString();
  }

  private char getCurrChar() {
    if (edge.length() == edgeIndex) {
      return EOT;
    }
    return edge.charAt(edgeIndex);
  }

  public PathToken getNextToken() {
    return tokens.poll();
  }

  public boolean isEmpty() {
    return tokens.isEmpty();
  }
}
