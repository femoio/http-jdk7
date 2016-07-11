package io.femo.http.support;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by felix on 7/11/16.
 */
public class HttpUtilTest {
    @Test
    public void joinPaths() throws Exception {
        assertEquals("/style", HttpUtil.joinPaths("/", "style"));
        assertEquals("/style", HttpUtil.joinPaths("/", "/style"));
        assertEquals("/style", HttpUtil.joinPaths("//", "/style"));
    }

}