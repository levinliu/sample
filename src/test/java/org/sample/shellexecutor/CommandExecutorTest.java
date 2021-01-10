package org.sample.shellexecutor;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author levinliu
 * Created on 2021/1/10
 * (Change file header on Settings -> Editor -> File and Code Templates)
 */
public class CommandExecutorTest {
    @Test
    public void testPerOS() {
        CommandExecutor.execute(".", "echo \"abc\"", 1,
                log -> assertEquals("abc", log),
                err -> assertTrue("do not expect error " + err, false));
    }

}
