package com.culwonder.leeds_profile_springboot_core.api.common.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * User ID 생성기
 * 형식: usr_날짜(Base62)_일련번호
 * 예시: usr_2bG7_1, usr_2bG7_2, usr_2bG8_1
 */
@Component
public class UserIdGenerator
{
    
    private static final String PREFIX = "usr_";
    private static final String SEPARATOR = "_";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    
    // 일련번호 관리를 위한 AtomicLong (실제 운영환경에서는 Redis나 DB를 사용하는 것이 좋습니다)
    private final AtomicLong sequenceCounter = new AtomicLong(1);
    
    /**
     * 새로운 User ID를 생성합니다.
     * 
     * @return 생성된 User ID
     */
    public String generateUserId()
    {
        LocalDate today = LocalDate.now(java.time.ZoneId.of("Asia/Seoul"));
        String dateString = today.format(DATE_FORMATTER);
        
        // 날짜를 Base62로 인코딩
        long dateNumber = Long.parseLong(dateString);
        String encodedDate = Base62Util.encode(dateNumber);
        
        // 일련번호 생성 (날짜별로 초기화되어야 하므로 실제로는 더 정교한 로직이 필요)
        long sequence = sequenceCounter.getAndIncrement();
        
        return PREFIX + encodedDate + SEPARATOR + sequence;
    }
    
    /**
     * 특정 날짜의 User ID를 생성합니다.
     * 
     * @param date 날짜
     * @param sequence 일련번호
     * @return 생성된 User ID
     */
    public String generateUserId(LocalDate date, long sequence)
    {
        String dateString = date.format(DATE_FORMATTER);
        long dateNumber = Long.parseLong(dateString);
        String encodedDate = Base62Util.encode(dateNumber);
        
        return PREFIX + encodedDate + SEPARATOR + sequence;
    }
    
    /**
     * User ID에서 날짜를 추출합니다.
     * 
     * @param userId User ID
     * @return 추출된 날짜
     */
    public LocalDate extractDateFromUserId(String userId)
    {
        if (userId == null || !userId.startsWith(PREFIX))
        {
            throw new IllegalArgumentException("Invalid User ID format: " + userId);
        }
        
        String[] parts = userId.split(SEPARATOR);
        if (parts.length != 3)
        {
            throw new IllegalArgumentException("Invalid User ID format: " + userId);
        }
        
        String encodedDate = parts[1];
        long dateNumber = Base62Util.decode(encodedDate);
        String dateString = String.format("%08d", dateNumber);
        
        return LocalDate.parse(dateString, DATE_FORMATTER);
    }
    
    /**
     * User ID에서 일련번호를 추출합니다.
     * 
     * @param userId User ID
     * @return 추출된 일련번호
     */
    public long extractSequenceFromUserId(String userId)
    {
        if (userId == null || !userId.startsWith(PREFIX))
        {
            throw new IllegalArgumentException("Invalid User ID format: " + userId);
        }
        
        String[] parts = userId.split(SEPARATOR);
        if (parts.length != 3)
        {
            throw new IllegalArgumentException("Invalid User ID format: " + userId);
        }
        
        return Long.parseLong(parts[2]);
    }
    
    /**
     * User ID 형식이 유효한지 검증합니다.
     * 
     * @param userId 검증할 User ID
     * @return 유효하면 true, 아니면 false
     */
    public boolean isValidUserId(String userId)
    {
        try
        {
            if (userId == null || !userId.startsWith(PREFIX))
            {
                return false;
            }
            
            String[] parts = userId.split(SEPARATOR);
            if (parts.length != 3)
            {
                return false;
            }
            
            // 날짜 부분 검증
            String encodedDate = parts[1];
            long dateNumber = Base62Util.decode(encodedDate);
            String dateString = String.format("%08d", dateNumber);
            LocalDate.parse(dateString, DATE_FORMATTER);
            
            // 일련번호 부분 검증
            long sequence = Long.parseLong(parts[2]);
            if (sequence <= 0)
            {
                return false;
            }
            
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
