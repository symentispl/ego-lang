package segfault.ego.lexer;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

import static segfault.ego.lexer.Token.atom;
import static segfault.ego.lexer.Token.closingBracket;
import static segfault.ego.lexer.Token.eof;
import static segfault.ego.lexer.Token.openingBracket;
import static segfault.ego.lexer.Token.string;

public class Lexer
{
    public static void main( String[] args )
    {
        var lexer = new Lexer();
        List<Token> tokens = lexer.tokenize( "()" );

        assert tokens.equals(
                List.of(
                        openingBracket(),
                        closingBracket(),
                        eof()
                )
        );

        tokens = lexer.tokenize( "(print Hi)" );
        assert tokens.equals(
                List.of(
                        openingBracket(),
                        atom( "print" ),
                        atom( "Hi" ),
                        closingBracket(),
                        eof()
                )
        );

        tokens = lexer.tokenize( """
                                         (print "Hello world")""" );
        assert tokens.equals(
                List.of(
                        openingBracket(),
                        atom( "print" ),
                        string( "Hello world" ),
                        closingBracket(),
                        eof()
                )
        );
    }

    public List<Token> tokenize( String s )
    {
        var tokens = new ArrayList<Token>();
        var reader = new PushbackReader( new StringReader( s ) );

        int ch;
        try
        {
            while ( (ch = reader.read()) != -1 )
            {
                if ( ch == '(' )
                {
                    tokens.add( openingBracket() );
                    continue;
                }

                if ( ch == ')' )
                {
                    tokens.add( closingBracket() );
                    continue;
                }

                if ( ch == '\"' )
                {
                    var buffer = new StringBuilder();
                    int tmp;
                    while ( (tmp = reader.read()) != '\"' )
                    {
                        if ( tmp == -1 )
                        {
                            throw new IllegalStateException( "missing closing quote " + buffer.toString() );
                        }
                        buffer.appendCodePoint( tmp );
                    }

                    tokens.add( string( buffer.toString() ) );
                    continue;
                }

                if ( !Character.isWhitespace( ch ) )
                {
                    var buffer = new StringBuilder();
                    int tmp = ch;
                    do
                    {
                        buffer.appendCodePoint( tmp );
                    }
                    while ( !Character.isWhitespace( (tmp = reader.read()) ) && tmp != '(' && tmp != ')' && tmp != -1 );
                    reader.unread( tmp );
                    tokens.add( atom( buffer.toString() ) );
                    continue;
                }
            }
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
        tokens.add( eof() );
        return tokens;
    }
}
