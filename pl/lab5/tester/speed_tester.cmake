# CMP0007: list command no longer ignores empty elements.
if(POLICY CMP0007)
    cmake_policy(SET CMP0007 NEW)
endif()

function(exec_check)
    execute_process(COMMAND ${ARGV}
        OUTPUT_VARIABLE out
        ERROR_VARIABLE  err
        RESULT_VARIABLE result)
    set(ARGV "${ARGV};--simd")
    execute_process(COMMAND ${ARGV}
        OUTPUT_VARIABLE outSimd
        ERROR_VARIABLE  errSimd
        RESULT_VARIABLE resultSimd)
    if(result)
        string(REPLACE "/" ";" name_components ${ARGV0})
        list(GET name_components -1 name)
        if(NOT out)
            set(out "<empty>")
        endif()
        if(NOT err)
            set(err "<empty>")
        endif()
        message(FATAL_ERROR "\nError running \"${name}\"\n*** Output: ***\n${out}\n*** Error: ***\n${err}\n")
    endif()
    if(resultSimd)
        string(REPLACE "/" ";" name_components ${ARGV0})
        list(GET name_components -1 name)
        if(NOT outSimd)
            set(outSimd "<empty>")
        endif()
        if(NOT errSimd)
            set(errSimd "<empty>")
        endif()
        message(FATAL_ERROR "\nError running \"${name}\"\n*** Output: ***\n${out}\n*** Error: ***\n${err}\n")
    endif()
    if (outSimd GREATER out)
        message(FATAL_ERROR "\nProgram that uses SSE is slower than one that doesn't use it\nNon-SIMD execution time: ${out}\nSIMD execution time: ${outSimd}")
    endif()
endfunction()

file(REMOVE ${TEST_DIR}/output.bmp)
exec_check(${IMAGE_TRANSFORMER} ${TEST_DIR}/input.bmp ${TEST_DIR}/output.bmp)
exec_check(${IMAGE_MATCHER} ${TEST_DIR}/output.bmp ${TEST_DIR}/output_expected.bmp)
