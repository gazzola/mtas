package mtas.parser.function.util;

import java.io.IOException;
import java.util.HashSet;

import mtas.codec.util.CodecUtil;
import mtas.parser.function.ParseException;

/**
 * The Class MtasFunctionParserFunction.
 */
public abstract class MtasFunctionParserFunction {

  /** The parser doubles. */
  protected MtasFunctionParserFunction[] parserDoubles;
  
  /** The parser longs. */
  protected MtasFunctionParserFunction[] parserLongs;
  
  /** The constant doubles. */
  protected Double[] constantDoubles;
  
  /** The constant longs. */
  protected long[] constantLongs;

  /** The data type. */
  protected String dataType = null;
  
  /** The sum rule. */
  protected boolean sumRule = false;
  
  /** The need positions. */
  protected boolean needPositions = false;
  
  /** The degree. */
  protected Integer degree = null;
  
  /** The need argument. */
  protected HashSet<Integer> needArgument = new HashSet<Integer>();

  /** The defined. */
  private boolean defined = false;

  /**
   * Gets the response.
   *
   * @param args the args
   * @param n the n
   * @return the response
   */
  public final MtasFunctionParserFunctionResponse getResponse(long[] args, long n) {
    if(dataType.equals(CodecUtil.DATA_TYPE_LONG)) {
      try {
        long l = getValueLong(args, n);
        return new MtasFunctionParserFunctionResponseLong(l, true);
      } catch (IOException e) {
        return new MtasFunctionParserFunctionResponseLong(0, false);
      }
    } else if(dataType.equals(CodecUtil.DATA_TYPE_DOUBLE)) {
      try {
        double d = getValueDouble(args, n);
        return new MtasFunctionParserFunctionResponseDouble(d, true);
      } catch (IOException e) {
        return new MtasFunctionParserFunctionResponseDouble(0, false);
      }
    } else {
      return null;
    }
  }

  /**
   * Gets the value double.
   *
   * @param args the args
   * @param n the n
   * @return the value double
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public abstract double getValueDouble(long[] args, long n) throws IOException;
  
  /**
   * Gets the value long.
   *
   * @param args the args
   * @param n the n
   * @return the value long
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public abstract long getValueLong(long[] args, long n) throws IOException;

  /**
   * Close.
   *
   * @throws ParseException the parse exception
   */
  public void close() throws ParseException {
    defined = true;
  }

  /**
   * Gets the type.
   *
   * @return the type
   */
  public final String getType() {
    return dataType;
  }

  /**
   * Sum rule.
   *
   * @return the boolean
   */
  public final Boolean sumRule() {
    return sumRule;
  }

  /**
   * Need positions.
   *
   * @return the boolean
   */
  public final Boolean needPositions() {
    return needPositions;
  }
  
  /**
   * Need argument.
   *
   * @param i the i
   * @return the boolean
   */
  public final Boolean needArgument(int i) {
    return needArgument.contains(i);
  }
  
  /**
   * Need arguments number.
   *
   * @return the int
   */
  public final int needArgumentsNumber() {
    int number = 0;
    for(int i: needArgument) {
      number = Math.max(number, (i+1));
    }
    return number;
  }
  
  /**
   * Need arguments.
   *
   * @return the integer[]
   */
  public final Integer[] needArguments() {
    return needArgument.toArray(new Integer[needArgument.size()]);
  }
  
  /**
   * Defined.
   *
   * @return true, if successful
   */
  protected final boolean defined() {
    return defined;
  }

}
