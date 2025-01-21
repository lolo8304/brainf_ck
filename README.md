# brainf_ck
is my [codingchallenges.fyi](https://codingchallenges.fyi/challenges/challenge-brainfuck) implementation with a compiler and VM for the esoteric language brainf_ck 

![brainf_ck-mandelbrot.gif](docu%2Fbrainf_ck-mandelbrot.gif)

## what is branfuck?

Brainf*ck is an esoteric programming language that was created by Urban Müller in 1993. Apparently the intention was to be able to create a language that allowed for the creation of a tiny compiler. The first compiler being just 296 bytes, which later shrank to 240 bytes. There’s now one that is around 170 bytes

Official website: https://brainfuck.org/

# build

- use gradle
- build

```bash
./gradlew installDist
```

# run

run the application with parameter --help to see the arguments

```bash
./brainf_uck.sh
Usage: brainf_ck [-hivV] [-ci] [-clear] [-vv] [-c[=<command>]] [-t[=<timeout>]]
                 [-b=<bufferSize>] [<file>]
This challenge is to build your own brainfuck implementation. In console mode,
press enter to close
      [<file>]       parameter file to execute
  -b=<bufferSize>    size of the outputbuffer = default 50. e.g for each use -b
                       1
  -c=[<command>]     executes the command passed
      -ci            clear the screen after input
      -clear         clear screen at every step
  -h, --help         Show this help message and exit.
  -i                 use interpreter only instead of byte code
  -t=[<timeout>]     timeout in ms
  -v                 verbose model level 1
  -V, --version      Print version information and exit.
      -vv            verbose model level 2

```

## run command line

just pass no arguments

```bash
./brainf_uck.sh
brainf_ck>> test
test
brainf_ck>> 
bye

```

to close: just press <enter>

## execute a command from a file

```bash
./brainf_uck.sh cc.bf
```

## run with inline command

```bash
./brainf_uck.sh -c "++++++++++[>+>+++>+++++++>++++++++++<<<<-]>>>++.>+++++.<<<."
```

## run with other options

-i

run in interpreter mode and not compiled to see the time difference

-b <number>

run with different buffer to write out characters. default 50.  Every 50 chars the out is printed to screen.
this is how you can speed up the execution

-ci

clear the screen after entering data from System.in. This is used e.g. for gameoflife.bf to show the board new at each entry

-t <number>

waits for <number> ms at each step. Switches on automatically -vv verbose switch to see memory and pointer

-v

verbose mode 1 - shows statistics at each step

```log
pc=85 (max=11669), dc=0 (30000), pc-ops=1’826, read=6, write=46
```

-vv

verbose mode 2 - shows every step for 10 memory cells and the current program position and stats

```log
><
pc=85 (max=11669), dc=0 (30000), pc-ops=1’826, read=6, write=46
---------------------------------------------------------------------------------
      0        0        0        0       10        6        0        0       15  
  29996    29997    29998    29999        0 ^      1        2        3        4  
---------------------------------------------------------------------------------
        '++++[->++'
             ^    
---------------------------------------------------------------------------------

```

## Run examples

I have save some *.bf examples in the root folder

cc.bf - coding challenges
cc-small.bf - coding challenges for betterr debugging
gameoflife.bf - game of life (use -ci option to clear at each input)
LostOfKingdom.bf - game - still trying to figure out what it does
mandelbrot.bf - famous mandelbrot set - see top of readme - https://codingchallenges.fyi/blog/mandelbrot-set-brainfuck/
test-brain_ck.bf - a test file for any bf implementations - https://brainfuck.org/tests.b
numwarp.p - showing some numbers warped on screen - https://brainfuck.org/numwarp.b

```log
./brainf_ck.sh numwarp.bf -i
42 24    
         \
        \/\
      /\   
       / 
       \/
       
  /\   
   / 
 \ \/
\/\
   
```

some more examples: https://brainfuck.org/

# Build

checkout

gradle installDist or use gradle inside your IDE


## Run testcases

I have implemented some testcases - 3 of them are now failing due to the switchoff of some performacen optimization features. 


# Credits

To John 