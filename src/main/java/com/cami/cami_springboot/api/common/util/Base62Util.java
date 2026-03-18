package com.cami.cami_springboot.api.common.util;

import java.math.BigInteger;

/**
 * Base62 인코딩/디코딩 유틸리티 클래스
 * 0-9, A-Z, a-z 문자를 사용하여 62진법 인코딩을 수행합니다.
 */
public class Base62Util
{
    
    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = 62;
    
    /**
     * 숫자를 Base62 문자열로 인코딩합니다.
     * 
     * @param number 인코딩할 숫자
     * @return Base62로 인코딩된 문자열
     */
    public static String encode(long number)
    {
        if (number == 0)
        {
            return "0";
        }
        
        StringBuilder result = new StringBuilder();
        while (number > 0)
        {
            result.insert(0, BASE62_CHARS.charAt((int) (number % BASE)));
            number /= BASE;
        }
        return result.toString();
    }
    
    /**
     * Base62 문자열을 숫자로 디코딩합니다.
     * 
     * @param encoded Base62로 인코딩된 문자열
     * @return 디코딩된 숫자
     */
    public static long decode(String encoded)
    {
        if (encoded == null || encoded.isEmpty())
        {
            return 0;
        }
        
        long result = 0;
        long power = 1;
        
        for (int i = encoded.length() - 1; i >= 0; i--) 
        {
            char c = encoded.charAt(i);
            int index = BASE62_CHARS.indexOf(c);
            if (index == -1)
            {
                throw new IllegalArgumentException("Invalid Base62 character: " + c);
            }
            result += index * power;
            power *= BASE;
        }
        
        return result;
    }
    
    /**
     * BigInteger를 Base62 문자열로 인코딩합니다.
     * 
     * @param number 인코딩할 BigInteger
     * @return Base62로 인코딩된 문자열
     */
    public static String encode(BigInteger number)
    {
        if (number.equals(BigInteger.ZERO))
        {
            return "0";
        }
        
        StringBuilder result = new StringBuilder();
        BigInteger base = BigInteger.valueOf(BASE);
        
        while (number.compareTo(BigInteger.ZERO) > 0)
        {
            BigInteger[] divmod = number.divideAndRemainder(base);
            result.insert(0, BASE62_CHARS.charAt(divmod[1].intValue()));
            number = divmod[0];
        }
        
        return result.toString();
    }
    
    /**
     * Base62 문자열을 BigInteger로 디코딩합니다.
     * 
     * @param encoded Base62로 인코딩된 문자열
     * @return 디코딩된 BigInteger
     */
    public static BigInteger decodeToBigInteger(String encoded)
    {
        if (encoded == null || encoded.isEmpty())
        {
            return BigInteger.ZERO;
        }
        
        BigInteger result = BigInteger.ZERO;
        BigInteger power = BigInteger.ONE;
        BigInteger base = BigInteger.valueOf(BASE);
        
        for (int i = encoded.length() - 1; i >= 0; i--)
        {
            char c = encoded.charAt(i);
            int index = BASE62_CHARS.indexOf(c);
            if (index == -1)
            {
                throw new IllegalArgumentException("Invalid Base62 character: " + c);
            }
            result = result.add(BigInteger.valueOf(index).multiply(power));
            power = power.multiply(base);
        }
        
        return result;
    }
}
