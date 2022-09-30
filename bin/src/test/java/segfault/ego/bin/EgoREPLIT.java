/*
 * Copyright © 2020 Segfault (wiktor@segfault.events,jarek@segfault.events)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package segfault.ego.bin;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public class EgoREPLIT
{
    @Test
    void runREPL() throws IOException, InterruptedException
    {
        var process = new ProcessBuilder( "./ego" )
                .directory( new File( "target/jlink-image/bin" ) )
                .redirectErrorStream( true )
                .start();

        try ( var writer = new PrintWriter( new OutputStreamWriter( process.getOutputStream() ) ) )
        {
            writer.println( "()" );
            writer.flush();
        }

        var hasProcessExited = process.waitFor( 5, TimeUnit.SECONDS );

        if ( hasProcessExited )
        {
            var exitValue = process.exitValue();
            assertThat( exitValue ).isEqualTo( 0 );
            try ( var reader = new BufferedReader( new InputStreamReader( process.getInputStream() ) ) )
            {
                assertThat( reader.readLine() ).isEqualTo( "[]" );
            }
        }
        else

        {
            process.destroyForcibly();
            fail( "process didn't exit" );
        }
    }
}
