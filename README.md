# Ego: the language

Ego is a "pet project". This is a sandbox to explore, experiment and learn about programming languages, lexers, parsers,
abstract syntax trees, interpreters and code generators. Don't be scared, lonely wanderer.
Guardians of Ego are not in any aspect professional language designers and implementors. We left academia ages ago,
to make money writing unnecessarily complex business applications nobody cares about.

Deep in our souls, we have devoted our lives to The Machine. We are guardians, priests, and followers of the only and sacred Machine.
And as it is written in the holy books, we have to explore its beauty, glorify its complexity, embrace its chaos and share this
passion with others.

Ego is our sandbox. It will not only provide implementation of Ego language but also all necessary documentation, articles and links,
to help you feel welcomed in a beautiful world of programming languages.

By any means, Ego is not and will not be a production language. There will be no stable releases. Everything can change. We are not
slaves to the beast of backward compatibility.

## The way of Ego

Ego has opinions, about things. Strong opinions. If you don't like any of them, you need to stop here. There are other
languages which will accept your ideas and ways of seeing things. Ego doesn't care. It doesn't seek fame and applause.
It is ok to leave, at any point.

Ego is of course a reflection of our understanding of programming languages. Which is probably wrong, as we favour pragmatism over mathematical correctness.
Don't get us wrong, we love computer science and math. But we are engeneers, which means we also trust experience and heuristics.

Some decisions were made to make things simpler, and some were made to show some concepts. And some, are just fruits of
our ignorance.

## Goals

### Syntax

One thing that will surprise you once you enter the land of Ego is its syntax.
Yes, s-expressions. Is Ego a LISP dialect? No. It doesn't even try to be.
Because we use s-expressions, it doesn't make us LISP.

But, why s-expressions? Because building lexer (aka. scanner) is trivial and yet s-expressions
are so powerful, that we can focus on other more complex and interesting concepts, while still being able to express universal programming languages concepts.

Are s-expressions ugly? It depends on your personal preferences.
Remember Ego is not a production-grade language. Your programs in Ego will be simple code snippets, not monstrous monoliths. 
So a little bit of parenthesis would hurt.

### Types

Ego is statically typed language. What does it mean? The type of every value is known at interpretation/compile time.

Because we will have static types and we are Lisp's little sibling, you will find a lot of inspiration from Scheme and Typed Racket.

But why types? This is a consequence of The Rule of Ego, "No surprises"

### No surprises

This is a central point of Ego design and implementation, aka The Rule of Ego.

Avoid surprises, any surprises. Programming is not a box of chocolates.

Ego will try hard not to surprise you, even for the price of more complex syntax.

Here is small subset of surprise we will avoid in Ego.

* no nulls
* no coercion rules
* no operator precedence
* no statements
* no global state
* no inheritance (don't worry we will have polymorphism)

### Less is more, aka simple is beautiful

Less is more. There is nothing more than can be said as this is the consequence of The Rule of Ego.
Ego has a minimal set of concepts you have to know and understand.

* functions
* primitive types
* list,unions & products

It also means minimal set of keywords and "special forms".

### Interoperability

Yes. Ego is implemented in Java. Nothing stops you from implementing it in a language of your choice.
Since Ego doesn't have a standard library (it is a so-called "hosted language"), you need to take care
of interoperability with your host platform/language on your own.

Why Java? Remember Ego is a sandbox. It also means a place where we can try out new and shiny Java features
(event the ones that are incubating or in preview mode). Yes, our default compiler setting is `--enable-preview`.

It means that this implementation will allow you to invoke Java code, under some clearly defined rules. It will forces use to add some "special forms"
to work with Java, like calling constructors and java methods.

## Welcome to Ego

### Types

We have there small and limited set of builtin primitive types:

* number
* atom 
* character
* boolean
* none

### Lists

Lists are ordered.

### User defined types

(@ Str [char])

(@ User (& (: firstname Str) (: lastname Str) (: age number)))

enums?

are unions of symbol

(@ Colors (| RED BLUE))

### Strings

Remember, less is more? Are strings a separate type in Ego?

We could see string as list of characters. It would mean that you would need to define string as:

`('H' 'e' 'l' 'l' 'o')

Thanks to this trick, all functions that take list as an argument, like

(fun len (: a []) )

will work with strings

### flow control

(if (== a 5)
    ()
    ()
)

(for (= i 0)
     (< i 100)
     (++ i)
    (
    )
)

### Java interoperability

```ego
(
    (let s (#new java.lang.String ""))
    (if (#isEmpty s)
        (print "String is empty)
        (print "String is not empty))
)
```

(lambda ((: x Str)) (+ "Hello" x))