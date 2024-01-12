%define ADDRESS_OFFSET 8

extern string_equals
extern string_length

section .text

; find_word(rdi *key, rsi *dict_ptr) -> rax *value
find_word:
                mov     rdx, rsi
                add     rsi, ADDRESS_OFFSET
                mov     rcx, rsi
                push    rdi
                push    rdx
                push    rcx
                call    string_equals
                pop     rcx
                pop     rdx
                pop     rdi
                test    rax, rax
                jnz     .get_dict_value
                mov     rax, [rdx]
                test    rax, rax
                jz      .end
                mov     rsi, [rdx]
                jmp     find_word
    .get_dict_value:
                mov     rdi, rcx
                call    string_length
                add     rax, rcx
                inc     rax
    .end:
                ret
