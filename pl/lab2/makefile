main.o: main.asm words.inc
	nasm -f elf64 -g main.asm -o main.o

main: main.o lib.o dict.o
	ld -o main main.o lib.o dict.o

lib.o: lib.inc lib.asm
	nasm -f elf64 -g lib.asm -o lib.o

dict.o: dict.inc dict.asm
	nasm -f elf64 -g dict.asm -o dict.o

.PHONY: run
run: main
	./main

.PHONY: test
test: main
	python3 -u test.py
	

.PHONY: clean
clean:
	rm -f *.o main
