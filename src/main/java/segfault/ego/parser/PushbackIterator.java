package segfault.ego.parser;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class PushbackIterator<E> implements Iterator<E>
{
    private final Iterator<E> iterator;
    private final Deque<E> items = new ArrayDeque<>();

    private E current;

    public PushbackIterator( Iterator<E> iterator )
    {
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext()
    {
        return !items.isEmpty() ? true : iterator.hasNext();
    }

    @Override
    public E next()
    {
        return (current = !items.isEmpty() ? items.pop() : iterator.next());
    }

    public E current()
    {
        return current;
    }

    public void pushback( E element )
    {
        items.push( element );
    }
}
