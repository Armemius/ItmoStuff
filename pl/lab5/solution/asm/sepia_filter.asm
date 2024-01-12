%macro sum_xmm_register 1
    haddps %1, %1
    haddps %1, %1
%endmacro

%macro cap_channel_value 1
    cmp %1, 0xFF
    jle %%end
    mov %1, 0xFF
    %%end:
%endmacro

%define STACK_ALIGN 8

%define WIDTH_DATA_OFFSET 0
%define HEIGHT_DATA_OFFSET 4
%define RASTER_DATA_OFFSET 8

%define PIXEL_SIZE 3
%define PTR_SIZE 8

global gen_processed_image_simd

section .data
r_koefs: dd 0.189, 0.769, 0.393, 0.0
g_koefs: dd 0.168, 0.686, 0.349, 0.0
b_koefs: dd 0.131, 0.534, 0.272, 0.0

section .text
bits 64

gen_processed_image_simd:
        sub rsp, STACK_ALIGN
        push r12
        push r13

        movdqu xmm0, [rel r_koefs]
        movdqu xmm1, [rel g_koefs]
        movdqu xmm2, [rel b_koefs]

        mov r12, rdi
        mov r8, [r12 + RASTER_DATA_OFFSET]
        xor ecx, ecx
    .loop_width:
        mov r9, [r8]
        xor edx, edx
    .loop_height:
        movdqu xmm3, [r9]
        pmovzxbd xmm3, xmm3
        cvtdq2ps xmm3, xmm3

        movdqu xmm4, xmm3
        movdqu xmm5, xmm3

        mulps xmm3, xmm0
        mulps xmm4, xmm1
        mulps xmm5, xmm2

        sum_xmm_register xmm3
        sum_xmm_register xmm4
        sum_xmm_register xmm5

        cvtps2dq xmm3, xmm3
        cvtps2dq xmm4, xmm4
        cvtps2dq xmm5, xmm5

        extractps rax, xmm3, 0
        cap_channel_value rax
        mov byte[r9 + 2], al
        extractps rax, xmm4, 0
        cap_channel_value rax
        mov byte[r9 + 1], al
        extractps rax, xmm5, 0
        cap_channel_value rax
        mov byte[r9], al

        add r9, PIXEL_SIZE
        inc edx
        cmp edx, dword[r12 + HEIGHT_DATA_OFFSET]
        jne .loop_height

        add r8, PTR_SIZE
        inc ecx
        cmp ecx, dword[r12 + WIDTH_DATA_OFFSET]
        jne .loop_width

        mov rax, r12

        pop r13
        pop r12

        add rsp, STACK_ALIGN
        ret
