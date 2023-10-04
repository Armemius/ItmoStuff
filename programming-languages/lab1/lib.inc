section .text
 
; exit() -> void
exit:
                mov     rax, 60
                syscall

; string_length(rdi *string) -> rax string_length
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
                xor     rcx, rcx
                mov     rsi, rdi
                push    rcx
                push    rsi
                call    string_length
                mov     rdx, rax
                pop     rsi
                pop     rdi
                mov     rax, 1
                mov     rdi, 1
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
                call    print_char
                ret

; print_uint(rdi value) -> void
print_uint:
                mov     rax, rdi
                mov     r8, 0
    .loop:
                mov     rcx, 10
                mov     rdx, 0
                div     rcx
                mov     rdi, rdx
                add     rdi, '0'
                inc     r8
                push    rdi
                cmp     rax, 0
                jnz     .loop

                mov rcx, r8
    .print_loop:
                cmp     rcx, 0
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
                cmp     rax, 0
                jns     .end
                neg     rax
                mov     rdi, '-'
                push    rax
                call    print_char
                pop     rax
    .end:
                mov     rdi, rax
                call    print_uint
                ret

; string_equals(rdi *string_1, rsi *string_2) -> rax equality_flag
string_equals:
                xor     rcx, rcx
    .loop:
                mov     al, byte [rdi + rcx]
                cmp     al, byte [rsi + rcx]
                je      .check_end
                mov     rax, 0
                ret
    .check_end:
                inc     rcx
                cmp     al, 0
                jne     .loop
                mov     rax, 1
                ret

; read_char() -> rdi
read_char:
                mov     rax, 0
                mov     rdi, 0
                sub     rsp, 8
                mov     rsi, rsp
                mov     rdx, 1
                syscall
                cmp     rax, 0
                jle     .end_stream
                xor     rdi, rdi
                mov     al, [rsp]
                add     rsp, 8
                ret
    .end_stream:
                add     rsp, 8
                xor     rax, rax
                ret

; read_word(rdi *buffer, rsi buffer_size) -> rax *buffer, rdx word_length
read_word:
                xor     rdx, rdx
                xor     r8, r8
    .loop:
                push    rdi
                push    rsi
                push    rdx
                push    r8
                call    read_char
                pop     r8
                pop     rdx
                pop     rsi
                pop     rdi
                cmp     r8, 0
                jne     .insert
                cmp     al, 0x20
                je      .loop
                cmp     al, 0x9
                je      .loop
                cmp     al, 0xA
                je      .loop
                cmp     al, 0x0
                je      .end
    .insert:
                mov     r8, 1
                cmp     al, 0x0
                je      .end
                cmp     al, 0x20
                je      .end
                cmp     al, 0x9
                je      .end
                cmp     al, 0xA
                je      .end
                mov     byte [rdi + rdx], al
                inc     rdx
                cmp     rdx, rsi
                je      .error
                jmp     .loop
    .error:
                mov     byte [rdi + rdx], 0
                xor     rax, rax
                ret
    .end:
                mov     byte [rdi + rdx], 0
                mov     rax, rdi
                ret
 

; parse_uint(rdi *string) -> rax value, rdx number_length
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

; parse_uint(rdi *string) -> rax value, rdx number_length
parse_int:
                push    r8
                push    rbx
                xor     rcx, rcx
                xor     rsi, rsi
                xor     rax, rax
                xor     rdx, rdx
                cmp     byte [rdi + rcx], '-'
                jne     .loop
                inc     rcx
                mov     r8, 1
    .loop:
                mov     sil, byte [rdi + rcx]
                cmp     sil, '0'
                jl      .check_neg
                cmp     sil, '9'
                jg      .check_neg
                mov     rbx, 10
                mul     rbx
                sub     sil, '0'
                add     rax, rsi
                inc     rcx
                jmp     .loop
    .check_neg:
                cmp     r8, 1
                jne     .end
                neg     rax
    .end:
                mov     rdx, rcx
                pop     rbx
                pop     r8
                ret

; string_copy(rdi *string, rsi *buffer, rdx buffer_length) -> rax string_length
string_copy:
                
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
                cmp     al, 0
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
