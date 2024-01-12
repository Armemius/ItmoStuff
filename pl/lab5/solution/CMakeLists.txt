enable_language(ASM_NASM)

file(GLOB_RECURSE sources CONFIGURE_DEPENDS
    src/*.c
    src/*.h
    include/*.h
    asm/*.asm
)

add_executable(sepia-filter ${sources})
target_include_directories(sepia-filter PRIVATE src include)
