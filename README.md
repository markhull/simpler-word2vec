# simpler-word2vec
A simple and reasonably efficient toolbox for using large word2vec datasets in Java

This toolbox grew out of some frustration with existing methods of incorporating word2vec into Java programs -- I wanted a small, simple, efficient way to utilize word2vec BIN files in various programs; I wanted to be able to handle some massive files; and I didn't want a whole laboratory for working with word2vec, just something that will let me use existing vector BIN files in my code. Hence this toolbox. It can read in any size file (to the limits of your hardware), and it has no external dependencies, yay for that.

By the way, the reason I always capitalize BIN file is to emphasize that it doesn't work with TEXT files.

The workflow that I use, and for which this toolkit is built, is to use some other tool (I always use the original C programs) to create a vector BIN file, then I use these provided Java classes to load the BIN file into a java.util.LinkedHashMap and start doing wondrous and strange things with the data.

I should at this point say a couple of things: first, do not attempt to use this toolbox (or pretty much any word2vec library) on an old clunker 32-bit desktop PC. It won't work and you will be immensely frustrated and will yell at me. Don't. The nature of the word2vec datasets is that they require some hardware power, because they're large and full of computational goodness, which is why we like them. Case in point -- the original word2vec code comes with a script that will create a massive vector BIN file, 9.9GB in size, consisting of a vocabulary of 4,889,031 words and phrases, each of which has a vector of 500 floats associated with it. In fact, it's because I wanted to use this file that I wrote this toolbox.

Which brings me to the second point: you will need *a lot* of heap space to run the big vector BIN files. I use an nio direct memory buffer, but the map and vectors consume a lot of heap space. I finally ended up using a heap space JVM parameter of -Xmx11264M, which is pretty hefty. Your mileage may vary, but be forewarned. If it becomes a problem for you you might consider an alternative to the LinkedHashMap, although I get good performance from it.

If you want to do more with word2vec files in Java, you should check out DeepLearning4J and / or the Medallia Word2Vec Java library. This toolbox simply reads the BIN file and allows you to match words and run analogies to your heart's content; it doesn't create the BIN files themselves.

So enjoy, and happy word2veccing. -- Mark Hull
