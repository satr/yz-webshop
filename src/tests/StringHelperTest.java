package tests;

import io.github.satr.yzwebshop.helpers.StringHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

public class StringHelperTest {
    @Test
    public void isInteger() throws Exception {
        Assert.assertTrue(StringHelper.isInteger("" + Integer.MAX_VALUE));
        Assert.assertTrue(StringHelper.isInteger("" + Integer.MIN_VALUE));
        Assert.assertTrue(StringHelper.isInteger("00000123"));
        Assert.assertTrue(StringHelper.isInteger("123"));
        Assert.assertTrue(StringHelper.isInteger("-00000123"));
        Assert.assertTrue(StringHelper.isInteger("-123"));

        Assert.assertFalse(StringHelper.isInteger("" + Integer.MAX_VALUE + "0"));
        Assert.assertFalse(StringHelper.isInteger("" + Integer.MIN_VALUE + "0"));
        Assert.assertFalse(StringHelper.isInteger("123.0"));
        Assert.assertFalse(StringHelper.isInteger("--123"));
        Assert.assertFalse(StringHelper.isInteger("+123"));
        Assert.assertFalse(StringHelper.isInteger(".0"));
        Assert.assertFalse(StringHelper.isInteger("abc"));
    }

    @Test
    public void isDouble() throws Exception {
        Assert.assertTrue(StringHelper.isDouble("11111111111111111111.11111111111111111111"));
        Assert.assertTrue(StringHelper.isDouble("-11111111111111111111.11111111111111111111"));
        Assert.assertTrue(StringHelper.isDouble("00000123"));
        Assert.assertTrue(StringHelper.isDouble("123"));
        Assert.assertTrue(StringHelper.isDouble("-00000123"));
        Assert.assertTrue(StringHelper.isDouble("-123"));
        Assert.assertTrue(StringHelper.isDouble("00000123.0001"));
        Assert.assertTrue(StringHelper.isDouble("123.12"));
        Assert.assertTrue(StringHelper.isDouble("123,12"));
        Assert.assertTrue(StringHelper.isDouble("-00000123.12"));
        Assert.assertTrue(StringHelper.isDouble("-123.12"));
        Assert.assertTrue(StringHelper.isDouble("-123,12"));
        Assert.assertTrue(StringHelper.isDouble(".0"));

        Assert.assertFalse(StringHelper.isDouble("" + Double.MAX_VALUE + "0"));
        Assert.assertFalse(StringHelper.isDouble("" + Double.MIN_VALUE + "0"));
        Assert.assertFalse(StringHelper.isDouble("--123"));
        Assert.assertFalse(StringHelper.isDouble("+123"));
        Assert.assertFalse(StringHelper.isDouble("abc"));
        Assert.assertFalse(StringHelper.isDouble("123..0"));
        Assert.assertFalse(StringHelper.isDouble("--123"));
        Assert.assertFalse(StringHelper.isDouble("+123"));
        Assert.assertFalse(StringHelper.isDouble("123."));
        Assert.assertFalse(StringHelper.isDouble("..0"));
        Assert.assertFalse(StringHelper.isDouble("abc"));
    }

}