%include 'words.inc'
%define BUFFER_SIZE 255

global _start

extern exit
extern find_word
extern print_string
extern print_err_string
extern read_word

section .bss
buffer:         resb BUFFER_SIZE

section .data
not_found_msg:  db "No such key", 0
overflow_msg:   db "Key is too long", 0

section .text

_start:         
                mov     rdi, buffer
                mov     rsi, BUFFER_SIZE
                call    read_word
                test    rax, rax
                jz      .buffer_overflow
                mov     rsi, dict_ptr
                call    find_word
                test    rax, rax
                jz      .error
                mov     rdi, rax
                call    print_string
                xor     rdi, rdi
                call    exit
    .error:
                mov     rdi, not_found_msg
                call    print_err_string
                xor     rdi, rdi
                call    exit
    .buffer_overflow:
                mov     rdi, overflow_msg
                call    print_err_string
                xor     rdi, rdi
                call    exit
