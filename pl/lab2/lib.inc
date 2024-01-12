%define ZERO_SYMBOL     0x0
%define NEWLINE_SYMBOL  0xA
%define TAB_SYMBOL      0x20
%define SPACE_SYMBOL    0x9

section .text
 
; exit() -> void
exit:
                mov     rax, 60
                syscall

; string_length(rdi *string) -> rax
string_length:
                xor     rax, rax
    .count:
                cmp     byte [rdi + rax], 0
                je      .end
                inc     rax
                jmp     .count
    .end:
                ret

; print_string(rdi *string) -> void
print_string:
                push    rdi
                call    string_length
                mov     rdx, rax
                pop     rsi
                xor     rdi, rdi
                mov     rax, 1
                mov     rdi, 1
                syscall
                ret

; print_string(rdi *string) -> void
print_err_string:
                push    rdi
                call    string_length
                mov     rdx, rax
                pop     rsi
                xor     rdi, rdi
                mov     rax, 1
                mov     rdi, 2
                syscall
                ret

; print_char(rdi char) -> void
print_char:
                sub     rsp, 8
                mov     [rsp], dil
                mov     rax, 1
                mov     rdi, 1
                mov     rsi, rsp
                mov     rdx, 1
                syscall
                add     rsp, 8
                ret

; print_newline() -> void
print_newline:
                mov     rdi, 10
                jmp     print_char

; print_uint(rdi value) -> void
print_uint:
                mov     rax, rdi
                xor     r8, r8
    .loop:
                mov     rcx, 10
                mov     rdx, 0
                div     rcx
                mov     rdi, rdx
                add     rdi, '0'
                inc     r8
                push    rdi
                test    rax, rax
                jnz     .loop
                mov     rcx, r8
    .print_loop:
                test    rcx, rcx
                je      .end
                dec     rcx
                pop     rdi
                push    rcx
                call    print_char
                pop     rcx
                jmp     .print_loop
    .end:
                ret

; print_int(rdi value) -> void
print_int:
                mov     rax, rdi
                test    rax, rax
                jns     .end
                neg     rax
                mov     rdi, '-'
                push    rax
                call    print_char
                pop     rax
    .end:
                mov     rdi, rax
                jmp     print_uint

; string_equals(rdi *string_1, rsi *string_2) -> rax
string_equals:
                xor     rcx, rcx
    .loop:
                mov     al, byte [rdi + rcx]
                cmp     al, byte [rsi + rcx]
                je      .check_end
                xor     rax, rax
                ret
    .check_end:
                inc     rcx
                test    al, al
                jne     .loop
                mov     rax, 1
                ret

; read_char() -> rax
read_char:
                xor     rax, rax
                xor     rdi, rdi
                sub     rsp, 8
                mov     rsi, rsp
                mov     rdx, 1
                syscall
                test    rax, rax
                jle     .end_stream
                xor     rdi, rdi
                mov     al, [rsp]
                add     rsp, 8
                ret
    .end_stream:
                add     rsp, 8
                xor     rax, rax
                ret

; read_word(rdi *buffer, rsi buffer_size) -> rax, rdx
read_word:
                push    r12
                push    r13
                push    r14
                push    r15

                mov     r12, rdi
                mov     r13, rsi
                mov     r14, rdx
                test    r13, r13
                jz      .error
                xor     r14, r14
    .loop:
                call    read_char
                cmp     al, TAB_SYMBOL
                je      .check_whitespace
                cmp     al, SPACE_SYMBOL
                je      .check_whitespace
                cmp     al, NEWLINE_SYMBOL
                je      .check_whitespace
                cmp     al, ZERO_SYMBOL
                je      .end
                mov     r15, 1
                jmp     .insert
    .insert:
                mov     byte [r12 + r14], al
                inc     r14
                cmp     r14, r13
                je      .error
                jmp     .loop
    .check_whitespace:
                test    r15, r15
                jz     .loop
                jmp    .end
    .error:
                mov     byte [r12 + r14 - 1], 0
                xor     rax, rax
                jmp     .restore_data
    .end:
                mov     byte [r12 + r14], 0
                mov     rax, r12
                jmp     .restore_data
    .restore_data:
                mov     rdi, r12
                mov     rdx, r14
                pop     r15
                pop     r14
                pop     r13
                pop     r12
                ret
 

; parse_uint(rdi *string) -> rax, rdx
parse_uint:
                push    rbx
                xor     rcx, rcx
                xor     rsi, rsi
                xor     rax, rax
                xor     rdx, rdx
    .loop:
                mov     sil, byte [rdi + rcx]
                cmp     sil, '0'
                jl      .end
                cmp     sil, '9'
                jg      .end
                mov     rbx, 10
                mul     rbx
                sub     sil, '0'
                add     rax, rsi
                inc     rcx
                jmp     .loop
    .end:
                mov     rdx, rcx
                pop     rbx
                ret

; parse_uint(rdi *string) -> rax, rdx
parse_int:
                push    r12
                cmp     byte [rdi + rcx], '+'
                je     .parse
                cmp     byte [rdi + rcx], '-'
                jne     .parse
                inc     rdi
                mov     r12, 1
    .parse:
                call    parse_uint
                test    r12, r12
                jz      .end
                inc     rdx
                neg     rax
    .end:
                pop     r12
                ret

; string_copy(rdi *string, rsi *buffer, rdx buffer_length) -> rax
string_copy:
                test    rdx, rdx
                jz      .error
                push    rdi
                push    rsi
                push    rdx
                call    string_length
                pop     rdx
                pop     rsi
                pop     rdi
                cmp     rax, rdx
                jge     .error
                xor     rcx, rcx
    .loop:
                mov     al, byte [rdi + rcx]
                mov     byte [rsi + rcx], al
                test    al, al
                je      .end
                inc     rcx
                jmp     .loop
    .error:
                mov     byte [rsi], 0
                xor     rax, rax
                ret
    .end:
                mov     rax, rcx
                ret
