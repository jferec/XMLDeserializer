package config;

import com.google.common.collect.ImmutableSet;
import config.model.Config;
import config.model.ConfigFactory;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import javax.management.modelmbean.XMLParseException;
import lexer.Lexer;
import org.apache.commons.lang3.reflect.FieldUtils;
import parser.Parser;
import parser.XMLFile;
import parser.XMLNode;

public class ConfigMaker {

  static private final String TYPE = "type";
  static private final String VALUE_TYPE = "value";
  static private final String OBJECT_TYPE = "object";
  static private final String ARRAY_TYPE = "array";
  static private final String ARRAY_ELEMENT = "element";
  static private final ImmutableSet<String> VALUE_TYPES = ImmutableSet.<String>builder()
      .add("int").add("Integer")
      .add("char").add("Character")
      .add("bool").add("Boolean")
      .add("String")
      .add("float").add("Float")
      .add("double").add("Double")
      .add("long").add("Long")
      .build();

  XMLFile inputFile;
  XMLFile mapperFile;
  ConfigFactory factory;

  public ConfigMaker(String inputPath, String mapperPath, ConfigFactory factory)
      throws IOException, XMLParseException {
    inputFile = new Parser(Lexer.of(inputPath)).run();
    mapperFile = new Parser(Lexer.of(mapperPath)).run();
    this.factory = factory;
  }

  public Object getConfig()
      throws Exception {
    Object output = createOutputObject();
    XMLNode root = mapperFile.getRoot();
    traverse(output, root);
    return output;
  }

  private Object traverse(Object object, XMLNode node) throws Exception {
    for (XMLNode child : node.getAllChildren()) {
      switch (child.getAttribute(TYPE).getValue()) {
        case VALUE_TYPE:
          setValue(object, child);
          break;
        case OBJECT_TYPE:
          Object instance = createAndAssignObject(object, child);
          traverse(instance, node.getChildren(child.getName()).get(0));
          break;
        case ARRAY_TYPE:
          List<XMLNode> children = child.getChildren(ARRAY_ELEMENT);
          Optional<Field> arrayFiled = getField(object, child.getName());
          if (!arrayFiled.isPresent()) {
            throw new NoSuchFieldException(String
                .format("Array field %s was not found in %s class", child.getName(),
                    object.getClass().getSimpleName()));
          }
          Object array = Array
              .newInstance(arrayFiled.get().getType().getComponentType(), children.size());
          writeField(object, child.getName(), array);
          if (VALUE_TYPES.contains(array.getClass().getComponentType().getSimpleName())) {
            addValuesToArray(array, children);
          } else {
            addObjectsToArray(array, children);
          }
          break;
      }
    }
    return object;
  }

  private void addObjectsToArray(Object array, List<XMLNode> children) throws Exception {
    String typeName = array.getClass().getComponentType().getSimpleName();
    for (int i = 0; i < children.size(); i++) {
      XMLNode child = children.get(i).getAllChildren().get(0);
      Config element = factory.getConfigInstanceByName(typeName);
      Array.set(array, i, element);
      traverse(element, child);
    }
  }

  Config createOutputObject() throws NoSuchFieldException {
    return factory.getConfigInstanceByName(mapperFile.getRoot().getName());
  }

  void setValue(Object object, XMLNode child)
      throws Exception {
    String path = child.getAttribute("path").getValue();
    String fieldName = child.getName();
    String value = inputFile.find(path);
    Optional<Field> field = getField(object, fieldName);
    if (!field.isPresent()) {
      throw new NoSuchFieldException(String
          .format("Value field %s was not found in %s class", child.getName(),
              object.getClass().getSimpleName()));
    }
    Object toWrite = ConfigFactory.mapType(field.get().getType().getSimpleName(), value);
    writeField(object, fieldName, toWrite);
  }

  Object createAndAssignObject(Object object, XMLNode child) throws Exception {
    Optional<Field> field = getField(object, child.getName());
    if (!field.isPresent()) {
      throw new NoSuchFieldException(String
          .format("Object field %s was not found in %s class", child.getName(),
              object.getClass().getSimpleName()));
    }
    Object result = factory.getConfigInstanceByName(field.get().getType().getSimpleName());
    writeField(object, field.get().getName(), result);
    return result;
  }

  void addValuesToArray(Object array, List<XMLNode> children) throws ParseException {
    String typeName = array.getClass().getComponentType().getSimpleName();
    for (int i = 0; i < children.size(); i++) {
      XMLNode child = children.get(i).getAllChildren().get(0);
      String path = child.getAttribute("path").getValue();
      String value = inputFile.find(path);
      Object element = ConfigFactory.mapType(typeName, value);
      Array.set(array, i, element);
    }
  }

  private Optional<Field> getField(Object object, String fieldName) throws Exception {
    try {
      Field field = FieldUtils.getField(object.getClass(), fieldName, true);
      if (field == null) {
        return Optional.empty();
      }
      return Optional.of(field);
    } catch (Exception e) {
      throw new Exception(String.format("Failed to get the %s field in %s object.", fieldName,
          object.getClass().getSimpleName()));
    }
  }

  private void writeField(Object object, String fieldName, Object toWrite)
      throws NoSuchFieldException {
    try {
      FieldUtils.writeDeclaredField(object, fieldName, toWrite, true);
    } catch (Exception e) {
      throw new NoSuchFieldException(
          String.format("Failed to write object to %s field %s", fieldName, e.getMessage()));
    }

  }

}
