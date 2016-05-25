package au.com.acegi.sbedao;

import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import org.agrona.generation.CompilerUtil;

/**
 * Implements a {@link Dao} interface at runtime.
 * <p>
 * The DAO must declare a {@link #MAX_MSG_LEN} int field and it must only extend
 * the {@link Dao} interface.
 * <p>
 * This class is not thread safe.
 * <p>
 */
public final class DaoFactory {

  private static final String IMPL_SUFFIX = "$$Generated";
  private static final String INDENT = "  ";
  private static final String INDENT2 = "    ";
  private static final String MAX_MSG_LEN = "MAX_MSG_LEN";
  private static final String NL = System.lineSeparator();
  private final Map<String, Class<?>> classes;
  private final Map<String, CharSequence> sources;

  public DaoFactory() {
    classes = new HashMap<>();
    sources = new HashMap<>();
  }

  /**
   * Dynamically build an implementation of the DAO, returning a new instance of
   * that implementation.
   *
   * @param <T>   the DAO that defines the required contract
   * @param clazz the DAO class
   * @return a new instance of the implementation of that DAO (never null)
   */
  @SuppressWarnings("unchecked")
  public <T extends Dao<?, ?, ?, ?, ?, ?>> T newInstance(Class<T> clazz) {
    final String implName = clazz.getName() + IMPL_SUFFIX;
    final Class<?> impl;
    if (classes.containsKey(implName)) {
      impl = classes.get(implName);
    } else {
      generate(clazz);
      impl = classes.get(implName);
      assert impl != null;
    }

    try {
      return (T) impl.newInstance();
    } catch (IllegalAccessException | InstantiationException ex) {
      throw new IllegalStateException("Cannot instantiate " + implName, ex);
    }
  }

  private Class<?>[] findParameters(Class<? extends Dao<?, ?, ?, ?, ?, ?>> clazz) {
    Type[] superc = clazz.getGenericInterfaces();
    assert superc.length == 1;
    assert superc[0] instanceof ParameterizedType;
    ParameterizedType t = (ParameterizedType) superc[0];
    Type[] args = t.getActualTypeArguments();
    Class<?>[] res = new Class<?>[args.length];
    for (int index = 0; index < args.length; index++) {
      res[index] = (Class<?>) args[index];
    }
    return res;
  }

  private void generate(Class<? extends Dao<?, ?, ?, ?, ?, ?>> clazz) {
    if (!clazz.isInterface()) {
      throw new IllegalStateException("DAO must be an interface");
    }

    final Field maxMsgLen;
    try {
      maxMsgLen = clazz.getDeclaredField(MAX_MSG_LEN);
    } catch (NoSuchFieldException | SecurityException ex) {
      throw new IllegalStateException(MAX_MSG_LEN + " field not found", ex);
    }
    if (!maxMsgLen.getType().equals(int.class)) {
      throw new IllegalStateException(MAX_MSG_LEN + " must be primitive int");
    }

    final Class<?>[] daoParams = findParameters(clazz);
    assert (daoParams.length == 6);

    final String implFullName = clazz.getName() + IMPL_SUFFIX;
    final String implSimpleName = clazz.getSimpleName() + IMPL_SUFFIX;

    StringWriter writer = new StringWriter();
    writer.append("package " + clazz.getPackage().getName() + ";" + NL + NL);
    writer.append("public final class " + implSimpleName + NL);
    writer.append(INDENT + "extends " + AbstractDao.class.getName());
    writer.append("<" + NL);
    for (int index = 0; index < daoParams.length; index++) {
      if (index > 0) {
        writer.append("," + NL);
      }
      writer.append(INDENT2 + daoParams[index].getName());
    }
    writer.append(">" + NL);
    writer.append(INDENT + "implements " + clazz.getName() + " {" + NL + NL);

    writer.append(INDENT + "public " + implSimpleName + "() {" + NL);
    writer.append(INDENT2 + "super(" + MAX_MSG_LEN + ")" + ";" + NL);
    writer.append(INDENT + "}" + NL + NL);

    final String keyEncoder = daoParams[1].getName();
    final String keyDecoder = daoParams[2].getName();
    final String valEncoder = daoParams[4].getName();
    final String valDecoder = daoParams[5].getName();

    writer.append(INDENT + "protected " + keyDecoder + " keyDecoder() {" + NL);
    writer.append(INDENT2 + "return new " + keyDecoder + "();" + NL);
    writer.append(INDENT + "}" + NL + NL);

    writer.append(INDENT + "protected " + keyEncoder + " keyEncoder() {" + NL);
    writer.append(INDENT2 + "return new " + keyEncoder + "();" + NL);
    writer.append(INDENT + "}" + NL + NL);

    writer.append(INDENT + "protected " + valDecoder + " valDecoder() {" + NL);
    writer.append(INDENT2 + "return new " + valDecoder + "();" + NL);
    writer.append(INDENT + "}" + NL + NL);

    writer.append(INDENT + "protected " + valEncoder + " valEncoder() {" + NL);
    writer.append(INDENT2 + "return new " + valEncoder + "();" + NL);
    writer.append(INDENT + "}" + NL);

    writer.append("}");

    sources.put(implFullName, writer.toString());

    final Class<?> compiled;
    try {
      compiled = CompilerUtil.compileInMemory(implFullName, sources);
    } catch (ClassNotFoundException ex) {
      throw new IllegalStateException("Cannot compile " + implFullName, ex);
    }

    classes.put(implFullName, compiled);
  }

}
