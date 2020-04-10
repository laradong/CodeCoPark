package org.laradong.ccp;

import java.util.Arrays;
import java.util.Stack;
import java.util.function.BiFunction;

public class CalculateUtil {

  public static void rpn(Stack<Character> operators, Stack output, String str) {
    char[] chars = str.toCharArray();
    int pre = 0;
    boolean digital; //是否为数字（只要不是运算符，都是数字），用于截取字符串
    int len = chars.length;
    int bracket = 0; // 左括号的数量

    for (int i = 0; i < len; ) {
      pre = i;
      digital = Boolean.FALSE;
      //截取数字
      while (i < len && !Operator.isOperator(chars[i])) {
        i++;
        digital = Boolean.TRUE;
      }

      if (digital) {
        output.push(str.substring(pre, i) + " ");
      } else {
        char o = chars[i++]; //运算符
        if (o == '(') {
          bracket++;
        }
        if (bracket > 0) {
          if (o == ')') {
            while (!operators.empty()) {
              char top = operators.pop();
              if (top == '(') {
                break;
              }
              output.push(top + " ");
            }
            bracket--;
          } else {
            //如果栈顶为 ( ，则直接添加，不顾其优先级
            //如果之前有 ( ，但是 ( 不在栈顶，则需判断其优先级，如果优先级比栈顶的低，则依次出栈
            while (!operators.empty() && operators.peek() != '(' && Operator.cmp(o, operators.peek()) <= 0) {
              output.push(operators.pop() + " ");
            }
            operators.push(o);
          }
        } else {
          while (!operators.empty() && Operator.cmp(o, operators.peek()) <= 0) {
            output.push(operators.pop() + " ");
          }
          operators.push(o);
        }
      }

    }
    //遍历结束，将运算符栈全部压入output
    while (!operators.empty()) {
      output.push(operators.pop() + " ");
    }
  }

  public static Double calc(String input) {
    Stack<Character> operators = new Stack<>(); //运算符
    Stack output = new Stack(); //输出结果
    CalculateUtil.rpn(operators, output, input);
    StringBuffer sb = new StringBuffer();
    output.forEach(item -> sb.append(item));
    return calcRpn(sb.toString().trim());
  }

  public static Double calcRpn(String input) {
    Stack<Double> numbers = new Stack<>();
    Arrays.asList(input.split(" ")).stream().forEach(number -> {
      switch (number) {
        case "+":
          calcSign(numbers, (n1, n2) -> n2 + n1);
          break;
        case "-":
          calcSign(numbers, (n1, n2) -> n2 - n1);
          break;
        case "*":
          calcSign(numbers, (n1, n2) -> n2 * n1);
          break;
        case "/":
          calcSign(numbers, (n1, n2) -> n2 / n1);
          break;
        default:
          numbers.push(Double.parseDouble(number));
      }
    });
    return numbers.pop();
  }

  public static Stack<Double> calcSign(Stack<Double> numbers, BiFunction<Double, Double, Double> operation) {
    numbers.push(operation.apply(numbers.pop(), numbers.pop()));
    return numbers;
  }
}
