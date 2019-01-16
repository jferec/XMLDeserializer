package config;

import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.List;
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

  Object traverse(Object object, XMLNode node) throws Exception {
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
          //todo, jesli to obiekt -> tworzysz instance tego obiektu i puszczasz traverse
          //todo, jesli to atrybut -> setujesz musisz stworzyc obiekt i tak ;/ (ODCZYTUJESZ Z TYPU czy value czy object !!!)
          //ODCZYTUJESZ TYP Z ARRAYA, TWORZYSZ INSTANCJE OBIEKTU, PATRZYSZ CZY OBIEKT CZY AKTRYBUT I LECISZ 1) LUB 2)
          List<XMLNode> children = child.getChildren(ARRAY_ELEMENT);
          Field arrayFiled = FieldUtils.getField(object.getClass(), child.getName(), true);
          Object array = Array
              .newInstance(arrayFiled.getType().getComponentType(), children.size());
          FieldUtils.writeDeclaredField(object, child.getName(), array, true);
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

  void setValue(Object object, XMLNode child) throws ParseException, IllegalAccessException {
    String path = child.getAttribute("path").getValue();
    String fieldName = child.getName();
    String value = inputFile.find(path);
    Field field = FieldUtils.getDeclaredField(object.getClass(), fieldName, true);
    Object toWrite = ConfigFactory.mapType(field.getType().getSimpleName(), value);
    FieldUtils.writeDeclaredField(object, fieldName, toWrite, true);
  }

  Object createAndAssignObject(Object object, XMLNode child) throws Exception {
    Field field = FieldUtils.getField(object.getClass(), child.getName(), true);
    Object result = factory.getConfigInstanceByName(field.getType().getSimpleName());
    FieldUtils.writeDeclaredField(object, field.getName(), result, true);
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

}
