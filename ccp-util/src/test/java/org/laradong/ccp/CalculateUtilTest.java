package org.laradong.ccp;

import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;

public class CalculateUtilTest {
  private static final Gson gson = new Gson();

  @Test
  public void test() {
    String str = "A=1 AND ( B=2 OR C=3 ) AND D=4";
    System.out.println(gson.toJson(str.split(" ")));
  }

  @Test
  public void testRpn() {
    Map<String, String> data = new HashMap<>();
    data.put("1+2*3-4*5-6+7*8-9", "1 2 3 * + 4 5 * - 6 - 7 8 * + 9 -");
    data.put("a*(b-c*d)+e-f/g*(h+i*j-k)", "a b c d * - * e + f g / h i j * + k - * -");
    data.put("6*(5+(2+3)*8+3)", "6 5 2 3 + 8 * + 3 + *");
    data.put("a+b*c+(d*e+f)*g", "a b c * + d e * f + g * +");

    for (String key : data.keySet()) {
      String value = data.get(key);
      Stack<Character> operators = new Stack<>(); //运算符
      Stack output = new Stack(); //输出结果
      CalculateUtil.rpn(operators, output, key);
      StringBuffer sb = new StringBuffer();
      output.forEach(item -> sb.append(item));
      Assert.assertEquals(value, sb.toString().trim());
    }
  }

  @Test
  public void calcSignShouldReturnStackWithTwoElementsPoppedAndOneElementPushed() {
    Stack<Double> numbers = new Stack<>();
    IntStream.rangeClosed(1, 5).forEach(number -> numbers.add((double) number));
    Stack<Double> actual = CalculateUtil.calcSign(numbers, (num1, num2) -> num2 / num1);
    Assert.assertEquals(actual.size(), 4);
    System.out.println(actual);
  }

  @Test
  public void calcSignShouldUseOperationParameterToCalculatePushedValue() {
    Stack<Double> numbers = new Stack<>();
    numbers.push((double) 15);
    numbers.push((double) 3);
    Stack<Double> actual = CalculateUtil.calcSign(numbers, (num1, num2) -> num2 / num1);
    Assert.assertTrue(actual.pop() == 5);
  }

  @Test
  public void testCalc() {
    Assert.assertTrue(CalculateUtil.calc("1-(2+3)-4") == -8.0);
  }

  @Test
  public void testCalcRpn() {
    Assert.assertTrue(CalculateUtil.calcRpn("1 2 3 + - 4 -") == -8.0);
  }

  @Test
  public void calcShouldBeAbleToCalculateSingleDigitNumbers() {
    Assert.assertTrue(CalculateUtil.calc("1 2 +") == 3.0);
  }

  @Test
  public void calcShouldBeAbleToCalculateMultiDigitNumbers() {
    Assert.assertTrue(CalculateUtil.calc("12 3 /") == 4.0);
  }

  @Test
  public void calcShouldBeAbleToHandleNegativeNumbers() {
    Assert.assertTrue(CalculateUtil.calc("-12 3 /") == -4.0);
  }

  @Test
  public void calShouldBeAbleToHandleDecimalNumbers() {
    Assert.assertTrue(CalculateUtil.calc("-12.9 3 /") == -4.3);
  }

  @Test
  public void calShouldBeAbleToHandleMoreComplexNotations() {
    Assert.assertTrue(CalculateUtil.calc("1 2 + 4 * 5 + 3 -") == 14.0);
  }
}
