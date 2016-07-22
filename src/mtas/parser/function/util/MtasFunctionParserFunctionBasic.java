package mtas.parser.function.util;

import java.io.IOException;
import java.util.ArrayList;

import mtas.codec.util.CodecUtil;
import mtas.parser.function.ParseException;

/**
 * The Class MtasFunctionParserFunctionBasic.
 */
public class MtasFunctionParserFunctionBasic
    extends MtasFunctionParserFunction {

  /** The first type. */
  private String firstType;
  
  /** The first id. */
  private int firstId;

  /** The tmp parser longs. */
  private ArrayList<MtasFunctionParserFunction> tmpParserLongs = new ArrayList<MtasFunctionParserFunction>();
  
  /** The tmp parser doubles. */
  private ArrayList<MtasFunctionParserFunction> tmpParserDoubles = new ArrayList<MtasFunctionParserFunction>();
  
  /** The tmp constant longs. */
  private ArrayList<Long> tmpConstantLongs = new ArrayList<Long>();
  
  /** The tmp constant doubles. */
  private ArrayList<Double> tmpConstantDoubles = new ArrayList<Double>();

  /** The number. */
  private int number;
  
  /** The operator list. */
  private String[] operatorList;
  
  /** The type list. */
  private String[] typeList;
  
  /** The id list. */
  private int[] idList;

  /** The tmp operator list. */
  private ArrayList<String> tmpOperatorList = new ArrayList<String>();
  
  /** The tmp type list. */
  private ArrayList<String> tmpTypeList = new ArrayList<String>();
  
  /** The tmp id list. */
  private ArrayList<Integer> tmpIdList = new ArrayList<Integer>();

  /** The Constant BASIC_OPERATOR_ADD. */
  public final static String BASIC_OPERATOR_ADD = "add";
  
  /** The Constant BASIC_OPERATOR_SUBTRACT. */
  public final static String BASIC_OPERATOR_SUBTRACT = "subtract";
  
  /** The Constant BASIC_OPERATOR_MULTIPLY. */
  public final static String BASIC_OPERATOR_MULTIPLY = "multiply";
  
  /** The Constant BASIC_OPERATOR_DIVIDE. */
  public final static String BASIC_OPERATOR_DIVIDE = "divide";
  
  /** The Constant BASIC_OPERATOR_POWER. */
  public final static String BASIC_OPERATOR_POWER = "power";

  /**
   * Instantiates a new mtas function parser function basic.
   *
   * @param item the item
   * @throws ParseException the parse exception
   */
  public MtasFunctionParserFunctionBasic(MtasFunctionParserItem item)
      throws ParseException {
    sumRule=true;
    String type = item.getType();
    MtasFunctionParserFunction parser;
    firstType = type;  
    degree = item.getDegree();
    switch (type) {
    case MtasFunctionParserItem.TYPE_N:
      firstId = 0;
      dataType = CodecUtil.DATA_TYPE_LONG;
      needPositions = true;
      break;
    case MtasFunctionParserItem.TYPE_CONSTANT_LONG:
      firstId = tmpConstantLongs.size();
      dataType = CodecUtil.DATA_TYPE_LONG;
      tmpConstantLongs.add(item.getValueLong());
      break;
    case MtasFunctionParserItem.TYPE_CONSTANT_DOUBLE:
      firstId = tmpConstantDoubles.size();
      dataType = CodecUtil.DATA_TYPE_DOUBLE;
      tmpConstantDoubles.add(item.getValueDouble());
      break;
    case MtasFunctionParserItem.TYPE_ARGUMENT:
      firstType = type;
      firstId = item.getId();
      dataType = CodecUtil.DATA_TYPE_LONG;
      needArgument.add(item.getId());
      break;
    case MtasFunctionParserItem.TYPE_PARSER_LONG:
      parser = item.getParser();
      parser.close();
      if (parser.getType().equals(CodecUtil.DATA_TYPE_LONG)) {
        firstId = tmpParserLongs.size();
        tmpParserLongs.add(parser);
        sumRule = parser.sumRule();
        dataType = CodecUtil.DATA_TYPE_LONG;
        needPositions = needPositions?needPositions:parser.needPositions();
        needArgument.addAll(parser.needArgument);
      } else {
        throw new ParseException("incorrect dataType");
      }
      break;
    case MtasFunctionParserItem.TYPE_PARSER_DOUBLE:
      parser = item.getParser();
      parser.close();
      if (parser.getType().equals(CodecUtil.DATA_TYPE_DOUBLE)) {
        firstId = tmpParserDoubles.size();
        tmpParserDoubles.add(parser);
        sumRule = parser.sumRule();
        dataType = CodecUtil.DATA_TYPE_DOUBLE;
        needPositions = needPositions?needPositions:parser.needPositions();
        needArgument.addAll(parser.needArgument);
      } else {
        throw new ParseException("incorrect dataType");
      }
      break;
    default:
      throw new ParseException("unknown type");
    }    
  }

  /* (non-Javadoc)
   * @see mtas.parser.function.util.MtasFunctionParserFunction#close()
   */
  @Override
  public void close() throws ParseException {
    if (!defined()) {
      super.close();
      if (tmpParserLongs.size() > 0) {
        parserLongs = new MtasFunctionParserFunction[tmpParserLongs
            .size()];
        parserLongs = tmpParserLongs.toArray(parserLongs);
      }
      if (tmpParserDoubles.size() > 0) {
        parserDoubles = new MtasFunctionParserFunction[tmpParserDoubles.size()];
        parserDoubles = tmpParserDoubles.toArray(parserDoubles);
      }
      if (tmpConstantLongs.size() > 0) {
        constantLongs = new long[tmpConstantLongs.size()];
        for (int i = 0; i < tmpConstantLongs.size(); i++) {
          constantLongs[i] = tmpConstantLongs.get(i);
        }
      }
      if (tmpConstantDoubles.size() > 0) {
        constantDoubles = new Double[tmpConstantDoubles.size()];
        for (int i = 0; i < tmpConstantDoubles.size(); i++) {
          constantDoubles[i] = tmpConstantDoubles.get(i);
        }
      }
      if (firstType == null) {
        throw new ParseException("incorrect definition: no firstType");
      }
      if (tmpOperatorList.size() > 0) {
        number = tmpOperatorList.size();
        if ((tmpTypeList.size() != number) || (tmpIdList.size() != number)) {
          throw new ParseException("incorrect definition additional items");
        } else {
          operatorList = new String[number];
          operatorList = tmpOperatorList.toArray(operatorList);
          typeList = new String[number];
          typeList = tmpTypeList.toArray(typeList);
          idList = new int[number];
          for (int i = 0; i < number; i++) {
            idList[i] = tmpIdList.get(i).intValue();
          }          
        }
      } else {
        number = 0;
        operatorList = null;
        typeList = null;
        idList = null;
      }
    }
  }

  /**
   * Adds the.
   *
   * @param item the item
   * @throws ParseException the parse exception
   */
  public void add(MtasFunctionParserItem item) throws ParseException {
    basic(BASIC_OPERATOR_ADD, item);
  }

  /**
   * Subtract.
   *
   * @param item the item
   * @throws ParseException the parse exception
   */
  public void subtract(MtasFunctionParserItem item) throws ParseException {
    basic(BASIC_OPERATOR_SUBTRACT, item);
  }

  /**
   * Multiply.
   *
   * @param item the item
   * @throws ParseException the parse exception
   */
  public void multiply(MtasFunctionParserItem item) throws ParseException {
    basic(BASIC_OPERATOR_MULTIPLY, item);
  }

  /**
   * Divide.
   *
   * @param item the item
   * @throws ParseException the parse exception
   */
  public void divide(MtasFunctionParserItem item) throws ParseException {
    basic(BASIC_OPERATOR_DIVIDE, item);
  }

  /**
   * Power.
   *
   * @param item the item
   * @throws ParseException the parse exception
   */
  public void power(MtasFunctionParserItem item) throws ParseException {
    basic(BASIC_OPERATOR_POWER, item);
  }

  /**
   * Basic.
   *
   * @param operator the operator
   * @param item the item
   * @throws ParseException the parse exception
   */
  private void basic(String operator, MtasFunctionParserItem item)
      throws ParseException {
    if (!defined()) {
      String type = item.getType();
      MtasFunctionParserFunction parser;
      tmpOperatorList.add(operator);
      if (operator.equals(BASIC_OPERATOR_DIVIDE)) {
        dataType = CodecUtil.DATA_TYPE_DOUBLE;
      }
      switch (type) {
      case MtasFunctionParserItem.TYPE_N:
        tmpTypeList.add(type);
        tmpIdList.add(0);
        needPositions = true;
        if(sumRule && degree!=null) {
          if(operator.equals(BASIC_OPERATOR_ADD)||operator.equals(BASIC_OPERATOR_SUBTRACT)) {
            if(degree<0) {
              sumRule=false;
              degree=null;
            } else if(degree>0) {
              sumRule=false;
            }
          } else if(operator.equals(BASIC_OPERATOR_POWER)) {
            if(degree!=0) {
              sumRule=false;
              degree=null;
            }
          }
        }
        break;
      case MtasFunctionParserItem.TYPE_ARGUMENT:
        tmpTypeList.add(type);
        tmpIdList.add(item.getId());
        needArgument.add(item.getId());
        if(sumRule && degree!=null) {
          if(operator.equals(BASIC_OPERATOR_ADD)||operator.equals(BASIC_OPERATOR_SUBTRACT)) {
            if(degree!=1) {
              sumRule=false;
            }
            if(degree>=0) {
              degree=Math.max(degree, 1);
            } else {
              degree=null;
            }
          } else if(operator.equals(BASIC_OPERATOR_MULTIPLY)) {
            if(degree!=0) {
              sumRule=false;
            }
            degree+=1;
          } else if(operator.equals(BASIC_OPERATOR_DIVIDE)) {
            sumRule=false;
            degree-=1;
          } else if(operator.equals(BASIC_OPERATOR_POWER)) {
            sumRule=false;
            degree=null;
          } 
        }
        break;
      case MtasFunctionParserItem.TYPE_CONSTANT_LONG:
        tmpTypeList.add(type);
        tmpIdList.add(tmpConstantLongs.size());
        tmpConstantLongs.add(item.getValueLong());
        if(sumRule && degree!=null) {
          if(operator.equals(BASIC_OPERATOR_ADD)||operator.equals(BASIC_OPERATOR_SUBTRACT)) {
            if(degree<0) {
              sumRule=false;
              degree=null;
            } else if(degree>0) {
              sumRule=false;
            }
          } else if(operator.equals(BASIC_OPERATOR_POWER)) {
            if(degree!=0) {
              sumRule = false;
              degree=null;
            }
          }
        }
        break;
      case MtasFunctionParserItem.TYPE_CONSTANT_DOUBLE:
        tmpTypeList.add(type);
        tmpIdList.add(tmpConstantDoubles.size());
        dataType = CodecUtil.DATA_TYPE_DOUBLE;
        tmpConstantDoubles.add(item.getValueDouble());
        if(sumRule && degree!=null) {
          if(operator.equals(BASIC_OPERATOR_ADD)||operator.equals(BASIC_OPERATOR_SUBTRACT)) {
            if(degree<0) {
              sumRule=false;
              degree=null;
            } else if(degree>0) {
              sumRule=false;
            }
          } else if(operator.equals(BASIC_OPERATOR_POWER)) {
            if(degree!=0) {
              sumRule = false;
              degree=null;
            }
          }
        }
        break;
      case MtasFunctionParserItem.TYPE_PARSER_LONG:
        tmpTypeList.add(type);
        tmpIdList.add(tmpParserLongs.size());
        parser = item.getParser();
        parser.close();
        tmpParserLongs.add(parser);
        sumRule = sumRule?parser.sumRule():false;
        needPositions = needPositions?needPositions:parser.needPositions();
        needArgument.addAll(parser.needArgument);
        if(sumRule && degree!=null) {
          if(operator.equals(BASIC_OPERATOR_ADD)||operator.equals(BASIC_OPERATOR_SUBTRACT)) {            
            if(parser.degree!=degree) {
              sumRule=false;
              if(degree<0) {
                degree=null;               
              } else {
                degree = Math.max(degree, parser.degree);
              }
            }
          } else if(operator.equals(BASIC_OPERATOR_MULTIPLY)) {
            if(degree!=0 || parser.degree!=0) {
              sumRule = false;              
            }
            degree+=parser.degree;                        
          } else if(operator.equals(BASIC_OPERATOR_POWER)) {
            if(degree!=0) {
              sumRule = false;
              degree = null;              
            }
          }
        }
        break;
      case MtasFunctionParserItem.TYPE_PARSER_DOUBLE:
        tmpTypeList.add(type);
        tmpIdList.add(tmpParserDoubles.size());
        dataType = CodecUtil.DATA_TYPE_DOUBLE;
        parser = item.getParser();
        parser.close();
        tmpParserDoubles.add(parser);
        sumRule = sumRule?parser.sumRule():false;
        needPositions = needPositions?needPositions:parser.needPositions();
        needArgument.addAll(parser.needArgument);
        if(sumRule && degree!=null) {
          if(operator.equals(BASIC_OPERATOR_ADD)||operator.equals(BASIC_OPERATOR_SUBTRACT)) {            
            if(parser.degree!=degree) {
              sumRule=false;
              if(degree<0) {
                degree=null;               
              } else {
                degree = Math.max(degree, parser.degree);
              }
            }
          } else if(operator.equals(BASIC_OPERATOR_MULTIPLY)) {
            if(degree!=0 || parser.degree!=0) {
              sumRule = false;              
            }
            degree+=parser.degree;                        
          } else if(operator.equals(BASIC_OPERATOR_POWER)) {
            if(degree!=0) {
              sumRule = false;
              degree = null;              
            }
          }
        }
        break;
      default:
        throw new ParseException("incorrect type");
      }
    } else {
      throw new ParseException("already defined");
    }
  }

  /* (non-Javadoc)
   * @see mtas.parser.function.util.MtasFunctionParserFunction#getValueDouble(long[], long)
   */
  @Override
  public double getValueDouble(long[] args, long n) throws IOException {
    double sum;
    switch (firstType) {
    case MtasFunctionParserItem.TYPE_ARGUMENT:
      sum = args[firstId];
      break;
    case MtasFunctionParserItem.TYPE_PARSER_DOUBLE:
      sum = parserDoubles[firstId].getValueDouble(args, n);
      break;
    case MtasFunctionParserItem.TYPE_PARSER_LONG:
      sum = parserLongs[firstId].getValueLong(args, n);
      break;
    case MtasFunctionParserItem.TYPE_CONSTANT_DOUBLE:
      sum = constantDoubles[firstId];
      break;
    case MtasFunctionParserItem.TYPE_CONSTANT_LONG:
      sum = constantLongs[firstId];
      break;
    case MtasFunctionParserItem.TYPE_N:
      sum = n;
      break;
    default:
      throw new IOException("no first value");
    }
    for (int i = 0; i < number; i++) {
      switch (operatorList[i]) {
      case BASIC_OPERATOR_ADD:
        switch (typeList[i]) {
        case MtasFunctionParserItem.TYPE_ARGUMENT:
          sum += args[idList[i]];
          break;
        case MtasFunctionParserItem.TYPE_PARSER_DOUBLE:
          sum += parserDoubles[idList[i]].getValueDouble(args, n);
          break;
        case MtasFunctionParserItem.TYPE_PARSER_LONG:
          sum += parserLongs[idList[i]].getValueLong(args, n);
          break;
        case MtasFunctionParserItem.TYPE_CONSTANT_DOUBLE:
          sum += constantDoubles[idList[i]];
          break;
        case MtasFunctionParserItem.TYPE_CONSTANT_LONG:
          sum += constantLongs[idList[i]];
          break;
        case MtasFunctionParserItem.TYPE_N:
          sum += n;
          break;
        default:
          throw new IOException("unknown type");
        }
        break;
      case BASIC_OPERATOR_SUBTRACT:
        switch (typeList[i]) {
        case MtasFunctionParserItem.TYPE_ARGUMENT:
          sum -= args[idList[i]];
          break;
        case MtasFunctionParserItem.TYPE_PARSER_DOUBLE:
          sum -= parserDoubles[idList[i]].getValueDouble(args, n);
          break;
        case MtasFunctionParserItem.TYPE_PARSER_LONG:
          sum -= parserLongs[idList[i]].getValueLong(args, n);
          break;
        case MtasFunctionParserItem.TYPE_CONSTANT_DOUBLE:
          sum -= constantDoubles[idList[i]];
          break;
        case MtasFunctionParserItem.TYPE_CONSTANT_LONG:          
          sum -= constantLongs[idList[i]];
          break;
        case MtasFunctionParserItem.TYPE_N:
          sum -= n;
          break;
        default:
          throw new IOException("unknown type");
        }
        break;
      case BASIC_OPERATOR_MULTIPLY:
        switch (typeList[i]) {
        case MtasFunctionParserItem.TYPE_ARGUMENT:
          sum *= args[idList[i]];
          break;
        case MtasFunctionParserItem.TYPE_PARSER_DOUBLE:
          sum *= parserDoubles[idList[i]].getValueDouble(args, n);
          break;
        case MtasFunctionParserItem.TYPE_PARSER_LONG:
          sum *= parserLongs[idList[i]].getValueLong(args, n);
          break;
        case MtasFunctionParserItem.TYPE_CONSTANT_DOUBLE:
          sum *= constantDoubles[idList[i]];
          break;
        case MtasFunctionParserItem.TYPE_CONSTANT_LONG:
          sum *= constantLongs[idList[i]];
          break;
        case MtasFunctionParserItem.TYPE_N:
          sum *= n;
          break;
        default:
          throw new IOException("unknown type");
        }
        break;
      case BASIC_OPERATOR_DIVIDE:
        double v;
        switch (typeList[i]) {
        case MtasFunctionParserItem.TYPE_ARGUMENT:
          v = args[idList[i]];
          break;
        case MtasFunctionParserItem.TYPE_PARSER_DOUBLE:
          v = parserDoubles[idList[i]].getValueDouble(args, n);
          break;
        case MtasFunctionParserItem.TYPE_PARSER_LONG:
          v = parserLongs[idList[i]].getValueLong(args, n);
          break;
        case MtasFunctionParserItem.TYPE_CONSTANT_DOUBLE:
          v = constantDoubles[idList[i]];
          break;
        case MtasFunctionParserItem.TYPE_CONSTANT_LONG:
          v = constantLongs[idList[i]];
          break;
        case MtasFunctionParserItem.TYPE_N:
          v = n;
          break;
        default:
          throw new IOException("unknown type");
        }
        if (v != 0) {
          sum /= v;
        } else {
          throw new IOException("division by zero");
        }
        break;
      case BASIC_OPERATOR_POWER:
        switch (typeList[i]) {
        case MtasFunctionParserItem.TYPE_ARGUMENT:
          sum = Math.pow(sum,args[idList[i]]);
          break;
        case MtasFunctionParserItem.TYPE_PARSER_DOUBLE:
          sum = Math.pow(sum,parserDoubles[idList[i]].getValueDouble(args, n));
          break;
        case MtasFunctionParserItem.TYPE_PARSER_LONG:
          sum = Math.pow(sum,parserLongs[idList[i]].getValueLong(args, n));
          break;
        case MtasFunctionParserItem.TYPE_CONSTANT_DOUBLE:
          sum = Math.pow(sum,constantDoubles[idList[i]]);
          break;
        case MtasFunctionParserItem.TYPE_CONSTANT_LONG:
          sum = Math.pow(sum,constantLongs[idList[i]]);
          break;
        case MtasFunctionParserItem.TYPE_N:
          sum = Math.pow(sum,n);
          break;
        default:
          throw new IOException("unknown type");
        }
        break;
      default:
        throw new IOException("unknown operator");
      }
    }
    return sum;
  }

  /* (non-Javadoc)
   * @see mtas.parser.function.util.MtasFunctionParserFunction#getValueLong(long[], long)
   */
  @Override
  public long getValueLong(long[] args, long n) throws IOException {
    try {
      long sum;
      switch (firstType) {
      case MtasFunctionParserItem.TYPE_ARGUMENT:
        sum = args[firstId];
        break;
      case MtasFunctionParserItem.TYPE_PARSER_DOUBLE:
        sum = (long) parserDoubles[firstId].getValueDouble(args, n);
        break;
      case MtasFunctionParserItem.TYPE_PARSER_LONG:
        sum = parserLongs[firstId].getValueLong(args, n);
        break;
      case MtasFunctionParserItem.TYPE_CONSTANT_DOUBLE:
        sum = constantDoubles[firstId].longValue();
        break;
      case MtasFunctionParserItem.TYPE_CONSTANT_LONG:
        sum = constantLongs[firstId];
        break;
      case MtasFunctionParserItem.TYPE_N:
        sum = n;
        break;
      default:
        throw new IOException("no first value");
      }
      for (int i = 0; i < number; i++) {
        switch (operatorList[i]) {
        case BASIC_OPERATOR_ADD:
          switch (typeList[i]) {
          case MtasFunctionParserItem.TYPE_ARGUMENT:
            sum += args[idList[i]];
            break;
          case MtasFunctionParserItem.TYPE_PARSER_DOUBLE:
            sum += (long) parserDoubles[idList[i]].getValueDouble(args, n);
            break;
          case MtasFunctionParserItem.TYPE_PARSER_LONG:
            sum += parserLongs[idList[i]].getValueLong(args, n);
            break;
          case MtasFunctionParserItem.TYPE_CONSTANT_DOUBLE:
            sum += constantDoubles[idList[i]].longValue();
            break;
          case MtasFunctionParserItem.TYPE_CONSTANT_LONG:
            sum += constantLongs[idList[i]];
            break;
          case MtasFunctionParserItem.TYPE_N:
            sum += n;
            break;
          default:
            throw new IOException("unknown type");
          }
          break;
        case BASIC_OPERATOR_SUBTRACT:
          switch (typeList[i]) {
          case MtasFunctionParserItem.TYPE_ARGUMENT:
            sum -= args[idList[i]];
            break;
          case MtasFunctionParserItem.TYPE_PARSER_DOUBLE:
            sum -= (long) parserDoubles[idList[i]].getValueDouble(args, n);
            break;
          case MtasFunctionParserItem.TYPE_PARSER_LONG:
            sum -= parserLongs[idList[i]].getValueLong(args, n);
            break;
          case MtasFunctionParserItem.TYPE_CONSTANT_DOUBLE:
            sum -= constantDoubles[idList[i]].longValue();
            break;
          case MtasFunctionParserItem.TYPE_CONSTANT_LONG:
            sum -= constantLongs[idList[i]];
            break;
          case MtasFunctionParserItem.TYPE_N:
            sum -= n;
            break;
          default:
            throw new IOException("unknown type");
          }
          break;
        case BASIC_OPERATOR_MULTIPLY:
          switch (typeList[i]) {
          case MtasFunctionParserItem.TYPE_ARGUMENT:
            sum *= args[idList[i]];
            break;
          case MtasFunctionParserItem.TYPE_PARSER_DOUBLE:
            sum *= (long) parserDoubles[idList[i]].getValueDouble(args, n);
            break;
          case MtasFunctionParserItem.TYPE_PARSER_LONG:
            sum *= parserLongs[idList[i]].getValueLong(args, n);
            break;
          case MtasFunctionParserItem.TYPE_CONSTANT_DOUBLE:
            sum *= constantDoubles[idList[i]].longValue();
            break;
          case MtasFunctionParserItem.TYPE_CONSTANT_LONG:
            sum *= constantLongs[idList[i]];
            break;
          case MtasFunctionParserItem.TYPE_N:
            sum *= n;
            break;
          default:
            throw new IOException("unknown type");
          }
          break;
        case BASIC_OPERATOR_DIVIDE:
          long v;
          switch (typeList[i]) {
          case MtasFunctionParserItem.TYPE_ARGUMENT:
            v = args[idList[i]];
            break;
          case MtasFunctionParserItem.TYPE_PARSER_DOUBLE:
            v = (long) parserDoubles[idList[i]].getValueDouble(args, n);
            break;
          case MtasFunctionParserItem.TYPE_PARSER_LONG:
            v = parserLongs[idList[i]].getValueLong(args, n);
            break;
          case MtasFunctionParserItem.TYPE_CONSTANT_DOUBLE:
            v = constantDoubles[idList[i]].longValue();
            break;
          case MtasFunctionParserItem.TYPE_CONSTANT_LONG:
            v = constantLongs[idList[i]];
            break;
          case MtasFunctionParserItem.TYPE_N:
            v = n;
            break;
          default:
            throw new IOException("unknown type");
          }
          if(v!=0) {
            sum /= v;
          } else {
            throw new IOException("division by zero");
          }
          break;
        case BASIC_OPERATOR_POWER:
          switch (typeList[i]) {
          case MtasFunctionParserItem.TYPE_ARGUMENT:
            sum = sum^args[idList[i]];
            break;
          case MtasFunctionParserItem.TYPE_PARSER_DOUBLE:
            sum = sum^(long) parserDoubles[idList[i]].getValueDouble(args, n);
            break;
          case MtasFunctionParserItem.TYPE_PARSER_LONG:
            sum = sum^parserLongs[idList[i]].getValueLong(args, n);
            break;
          case MtasFunctionParserItem.TYPE_CONSTANT_DOUBLE:
            sum = sum^constantDoubles[idList[i]].longValue();
            break;
          case MtasFunctionParserItem.TYPE_CONSTANT_LONG:
            sum = sum^constantLongs[idList[i]];
            break;
          case MtasFunctionParserItem.TYPE_N:
            sum = sum^n;
            break;
          default:
            throw new IOException("unknown type");
          }
          break;
        default:
          throw new IOException("unknown operator");
        }
      }
      return sum;
    } catch (java.lang.ArithmeticException e) {
      throw new IOException(e.getMessage());
    }
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    String text = "?";
    if(firstType!=null) {
      text = toString(firstType, firstId);
      for(int i=0; i<tmpOperatorList.size(); i++) {
        String operator = tmpOperatorList.get(i);
        if(operator.equals(BASIC_OPERATOR_ADD)) {
          text+=" + ";
        } else if(operator.equals(BASIC_OPERATOR_SUBTRACT)) {
          text+=" - ";
        } else if(operator.equals(BASIC_OPERATOR_MULTIPLY)) {
          text+=" * ";
        } else if(operator.equals(BASIC_OPERATOR_DIVIDE)) {
          text+=" / ";
        } else if(operator.equals(BASIC_OPERATOR_POWER)) {
          text+=" ^ ";
        } else {
          text+=" ? ";
        }
        text+=toString(tmpTypeList.get(i), tmpIdList.get(i));
      }
    }
    return text;
  }
  
  /**
   * To string.
   *
   * @param type the type
   * @param id the id
   * @return the string
   */
  private String toString(String type, int id) {
    if(type.equals(MtasFunctionParserItem.TYPE_CONSTANT_LONG)) {
      return tmpConstantLongs.get(id).toString();
    } else if(type.equals(MtasFunctionParserItem.TYPE_CONSTANT_DOUBLE)) {
      return tmpConstantDoubles.get(id).toString();
    } else if(type.equals(MtasFunctionParserItem.TYPE_PARSER_LONG)) {
      return "("+tmpParserLongs.get(id).toString()+")";
    } else if(type.equals(MtasFunctionParserItem.TYPE_PARSER_DOUBLE)) {
      return "("+tmpParserDoubles.get(id).toString()+")";
    } else if(type.equals(MtasFunctionParserItem.TYPE_ARGUMENT)) {
      return "$q"+id;
    } else if(type.equals(MtasFunctionParserItem.TYPE_N)) {
      return "$n";
    } else{
      return "..";
    }
  }

}
