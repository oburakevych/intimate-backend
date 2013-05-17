package com.intimate.files;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class ContentRangeHeader {
    private final int rangeStart;
    private final int rangeEnd;
    private final Long totalSize;
    
    public ContentRangeHeader(String range) {
        if (StringUtils.isBlank(range)) {
            throw new IllegalStateException("Range is required");
        }
        Matcher matcher = Pattern.compile("^bytes ([0-9]+)-([0-9]+)/(\\*|[0-9]+)$").matcher(range);
        if (!matcher.matches()) { // Invalid Range: header
            throw new IllegalStateException("Illegal range definition: " + range);
        }
        rangeStart = Integer.parseInt(matcher.group(1));
        rangeEnd = Integer.parseInt(matcher.group(2));
        String total = matcher.group(3);
        totalSize =  ("*".equals(total)) ? null : Long.parseLong(total);
        
        if(rangeEnd < rangeStart) {
            throw new IllegalStateException("Illegal range definition: " + range);
        }
        if(totalSize != null) {
            if(rangeStart > totalSize) {
                throw new IllegalStateException("Range is illegal: rangeStart large than totalSize: " + range);
            }
            if(rangeEnd > totalSize) {
                throw new IllegalStateException("Range is illegal: rangeEnd large than totalSize: " + range);
            }
        }
    }
    
    public int getRangeEnd() {
        return rangeEnd;
    }
    
    public int getRangeStart() {
        return rangeStart;
    }
    
    public Long getTotalSize() {
        return totalSize;
    }
    
    public String toString() {
        return "ContentRangeHeader. rangeStart: " + rangeStart + ". rangeEnd: " + rangeEnd + ". totalSize: " + totalSize;
    };
}
