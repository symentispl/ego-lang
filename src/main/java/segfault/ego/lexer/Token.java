package segfault.ego.lexer;

public record Token(Kind kind, String value)
{

    public static Token openingBracket()
    {
        return new Token( Kind.OPENING_BRACKET, "(" );
    }

    public static Token closingBracket()
    {
        return new Token( Kind.CLOSING_BRACKET, ")" );
    }

    public static Token eof()
    {

        return new Token( Kind.EOF, null );
    }

    public static Token atom( String value )
    {
        return new Token( Kind.ATOM, value );
    }

    public static Token string( String str )
    {
        return new Token( Kind.STRING, str );
    }
}
