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
Usage: brainf_ck [-hvV] [-vv] [-c[=<command>]] [<file>]
This challenge is to build your own brainf_ck implementation
      [<file>]      parameter file to execute
  -c=[<command>]    executes the command passed
  -h, --help        Show this help message and exit.
  -v                verbose model level 1
  -V, --version     Print version information and exit.
      -vv           verbose model level 2

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
./brainf_uck.sh hello.bf
```

## run with inline command

```bash
./brainf_uck.sh -c "++++++++++[>+>+++>+++++++>++++++++++<<<<-]>>>++.>+++++.<<<."
```